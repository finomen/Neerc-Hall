package ru.kt15.finomen.neerc.hall;

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
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;

import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.hall.Task.TaskPerformer;
import ru.kt15.finomen.neerc.hall.Task.TaskState;

public class XMPPChatManager implements ChatManager, TaskManager, Runnable {
	private final Set<ChatListener> listeners;
	private UserInfo user;
	private TaskPerformer self;
	private ConnectionConfiguration connConfig;
	private XMPPConnection connection;
	private Thread worker;
	private MultiUserChat conference;
	private final Map<String, UserInfo> users;

	public XMPPChatManager() {
		listeners = new HashSet<ChatListener>();
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
		listeners.add(listener);
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
				
				//conference.addMessageListener(myListener);
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

				while (connection.isConnected()) {
					Thread.sleep(10000);
				}
			} catch (Exception e) {
				Log.writeError(e.getLocalizedMessage());
			}
		}
	}
	
	private void processPresence(org.jivesoftware.smack.packet.Presence presence) {
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
			
			for(ChatListener lst : listeners) {
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
		case error:
			break;
		case subscribe:
			break;
		case subscribed:
			break;
		case unavailable:
			ui.status = UserStatus.OFFLINE;
			break;
		case unsubscribe:
			break;
		case unsubscribed:
			break;
		default:
			break;
		}
		
		users.put(id,  ui);
		
		for(ChatListener lst : listeners) {
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
        
        for(ChatListener lst : listeners) {
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
	public void addListener(TaskListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TaskPerformer getSelf() {
		return self;
	}

}
