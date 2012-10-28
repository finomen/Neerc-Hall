package ru.kt15.finomen.neerc.hall.xmpp;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;

import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.hall.ChatListener;
import ru.kt15.finomen.neerc.hall.ChatManager;
import ru.kt15.finomen.neerc.hall.Message;
import ru.kt15.finomen.neerc.hall.Task;
import ru.kt15.finomen.neerc.hall.Task.TaskPerformer;
import ru.kt15.finomen.neerc.hall.Task.TaskState;
import ru.kt15.finomen.neerc.hall.TaskListener;
import ru.kt15.finomen.neerc.hall.TaskManager;
import ru.kt15.finomen.neerc.hall.UserInfo;
import ru.kt15.finomen.neerc.hall.UserStatus;

public class NeercXMPPConnection implements ChatManager, TaskManager, Runnable {
	private final Set<ChatListener> chatListeners = new HashSet<ChatListener>();
	private final Set<TaskListener> taskListeners = new HashSet<TaskListener>();
	
	private UserInfo user;
	private TaskPerformer self;
	private ConnectionConfiguration connConfig;
	private XMPPConnection connection;
	private Thread worker;
	private MultiUserChat conference;
	private final Map<String, UserInfo> users;
	
	private final Map<Integer, Task> tasks = new HashMap<Integer, Task>();
	static int nextTaskId = 1;

	public NeercXMPPConnection() {
		users = new HashMap<String, UserInfo>();
		user = new UserInfo();
		user.id = "test@finomen.kt15.ru";
		user.status = UserStatus.OFFLINE;
		self = new TaskPerformer(user.id);

		connConfig = new ConnectionConfiguration("finomen.kt15.ru", 5222,
				"finomen.kt15.ru");
		connection = new XMPPConnection(connConfig);
		worker = new Thread(this);
	}

	@Override
	public void addListener(ChatListener listener) {
		chatListeners.add(listener);
	}
	
	@Override
	public void addListener(TaskListener listener) {
		taskListeners.add(listener);
	}

	@Override
	public void sendMessage(Message message) {
		try {
			conference.sendMessage(message.text);
		} catch (XMPPException e) {
			Log.writeError(e.getLocalizedMessage());
		}
		
	}

	@Override
	public UserInfo getUser() {
		return user;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				//int priority = 10;
				SASLAuthentication.supportSASLMechanism("PLAIN", 0);
				connection.connect();
				connection.login("test", "test");
				//Presence presence = new Presence(Presence.Type.available);
				//presence.setStatus("статус бота");
				//connection.sendPacket(presence);
				//presence.setPriority(priority);
				
				conference = new MultiUserChat(connection, "neerc@conference.finomen.kt15.ru");		
				PacketFilter filter = new AndFilter(new PacketTypeFilter(
						org.jivesoftware.smack.packet.Message.class));
			
				PacketListener myListener = new PacketListener() {
					public void processPacket(Packet packet) {
						if (packet instanceof org.jivesoftware.smack.packet.Message) {
							org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
							processMessage(message);
						}
					}
				};
				
				conference.addParticipantListener(new PacketListener() {
					@Override
					public void processPacket(Packet packet) {
						if (packet instanceof org.jivesoftware.smack.packet.Presence) {
							org.jivesoftware.smack.packet.Presence presence = (org.jivesoftware.smack.packet.Presence) packet;
							processPresence(presence);
						}
					}
				});
				
				connection.addPacketListener(myListener, filter);
				conference.join("test");

				while (connection.isConnected() && ! Thread.interrupted()) {
					Thread.sleep(10000);
				}
			} catch (InterruptedException e) {
				return;
			} catch (Exception e) {
				Log.writeError(e.getLocalizedMessage());
			}
		}
	}
	
	private void processPresence(org.jivesoftware.smack.packet.Presence presence) {	
		System.out.println(presence.getExtensions().toString());	
		
		String id = presence.getFrom();
		
		if (!users.containsKey(id)) {
			UserInfo ui = new UserInfo();
			ui.id = id;
			ui.name = id;
			Occupant occupant = conference.getOccupant(id);
			System.out.println(id + " " + occupant);
			if (occupant != null) {
				ui.name = occupant.getNick();
			}
			ui.status = UserStatus.OFFLINE;			
			users.put(id, ui);
			
			for(ChatListener lst : chatListeners) {
	        	try {
	        		lst.addUser(ui);
	        	} catch (Exception e) {
					Log.writeError(e.getLocalizedMessage());
				}
	        }
		}
		
		UserInfo ui = users.get(id);
				
		switch(presence.getType()) {
		case available:
			ui.status = UserStatus.ONLINE;
			break;
		case unavailable:
			ui.status = UserStatus.OFFLINE;
			break;
		default:
			break;
		}
		
		switch(presence.getMode()) {
		case available:
			ui.status = UserStatus.ONLINE;
			break;
		case away:
			ui.status = UserStatus.AWAY;
			break;
		case chat:
			ui.status = UserStatus.CHAT;
			break;
		case dnd:
			ui.status = UserStatus.DND;
			break;
		case xa:
			ui.status = UserStatus.EXTENDED_AWAY;
			break;
		default:
			break;
		}
		
		users.put(id,  ui);
		
		for(ChatListener lst : chatListeners) {
        	try {
        		lst.updateUser(ui);
        	} catch (Exception e) {
				Log.writeError(e.getLocalizedMessage());
			}
        }
		
	}
	
	private void processMessage(org.jivesoftware.smack.packet.Message message)
    {      
        Message msg = new Message();
        msg.fromId = message.getFrom();
        msg.fromName = message.getFrom();
        
        Occupant o = conference.getOccupant(msg.fromId);
        if (o != null) {
        	msg.fromName = o.getNick();
        }
        
        msg.toId = message.getTo();
        msg.toName = message.getTo();
        msg.text = message.getBody();
        msg.time = new Date();
        
        for(ChatListener lst : chatListeners) {
        	try {
        		lst.newMessgae(msg);
        	} catch (Exception e) {
				Log.writeError(e.getLocalizedMessage());
			}
        }
    }
	

	@Override
	public void Start() {
		worker.start();
	}

	@Override
	public void changeTaskState(int id, TaskState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TaskPerformer getSelf() {
		return self;
	}

	@Override
	public void Stop() {
		worker.interrupt();
		try {
			worker.join();
		} catch (InterruptedException e) {
		}
		
		conference.leave();
		connection.disconnect();
	}

	@Override
	public int getNextId() {
		return nextTaskId;
	}

	public void incomingTask(Task task) {
		if (task.getId() >= nextTaskId) {
			nextTaskId = task.getId() + 1;
		}
		
		boolean newTask = !tasks.containsKey(task.getId());
		tasks.put(task.getId(), task);
		
		for (TaskListener listener : taskListeners) {
			if (newTask) {
				listener.addTask(task);
			} else {
				listener.updateTask(task);
			}
		}
		
	}

}
