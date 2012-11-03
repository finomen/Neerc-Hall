package ru.kt15.finomen.neerc.hall.xmpp;

import java.util.Date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.packet.MUCUser;

import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.core.SettingsManager;
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
import ru.kt15.finomen.neerc.hall.xmpp.packet.NeercIQ;
import ru.kt15.finomen.neerc.hall.xmpp.packet.NeercTaskIQ;
import ru.kt15.finomen.neerc.hall.xmpp.packet.NeercTaskListIQ;
import ru.kt15.finomen.neerc.hall.xmpp.packet.NeercTaskResultIQ;
import ru.kt15.finomen.neerc.hall.xmpp.packet.NeercUserListIQ;
import ru.kt15.finomen.neerc.hall.xmpp.provider.NeercClockPacketExtensionProvider;
import ru.kt15.finomen.neerc.hall.xmpp.provider.NeercIQProvider;
import ru.kt15.finomen.neerc.hall.xmpp.provider.NeercTaskPacketExtensionProvider;
import ru.kt15.finomen.neerc.hall.xmpp.utils.XmlUtils;

import org.jivesoftware.smack.ConnectionListener;

public class NeercXMPPConnection implements ChatManager, TaskManager {
	private final Set<ChatListener> chatListeners = new HashSet<ChatListener>();
	private final Set<TaskListener> taskListeners = new HashSet<TaskListener>();
	
	private UserInfo user;
	private TaskPerformer self;
	
	private final Map<String, UserInfo> users;
	private final Map<Integer, Task> tasks = new HashMap<Integer, Task>();
	static int nextTaskId = 1;
	
	private MultiUserChat muc;
    private XMPPConnection connection;
    private boolean connected;
    
    private ReconnectThread reconnectThread;
    private ReconnectListener reconnectListener;
    private String password;

    private Date lastActivity = null;
    
    private final List<Message> history = new ArrayList<Message>();
    

	public NeercXMPPConnection() {
		users = new HashMap<String, UserInfo>();
		user = new UserInfo();
		user.id = SettingsManager.instance().get("hall.chat.xmpp.name", "test");
		password = SettingsManager.instance().get("hall.chat.xmpp.password", "12345");
		user.status = UserStatus.OFFLINE;
		self = new TaskPerformer(user.id);
				
		NeercTaskPacketExtensionProvider.register(this);
        NeercClockPacketExtensionProvider.register();
        NeercIQProvider.register(this);
        reconnectListener = new ReconnectListener();
        SASLAuthentication.supportSASLMechanism("PLAIN", 0);
	}
	
	public synchronized void disconnect() {
        connected = false;
        if (connection != null) {
            connection.disconnect();
        }
    }
    
    public synchronized void connect() {
        if (connection != null) {
            connection.removeConnectionListener(reconnectListener);
        }
        
        disconnect();
        
        Log.writeInfo("connecting to server");
                
        // Create the configuration for this new connection
        ConnectionConfiguration config = new ConnectionConfiguration(SettingsManager.instance().get("hall.chat.xmpp.server.host", "localhost"), SettingsManager.instance().get("hall.chat.xmpp.server.port", 5222));
        config.setCompressionEnabled(true);
        config.setSASLAuthenticationEnabled(true);
        config.setReconnectionAllowed(false);
        config.setDebuggerEnabled(SettingsManager.instance().get("hall.chat.xmpp.smack.debug", false));

        connection = new XMPPConnection(config);
        // Connect to the server
        try {
            connection.connect();
            authenticate();
        } catch (XMPPException e) {
            Log.writeError("Unable to connect", e);
            throw new RuntimeException(e);
        }
        connection.addConnectionListener(reconnectListener);


        // Create a MultiUserChat using an XMPPConnection for a room
        muc = new MultiUserChat(connection, SettingsManager.instance().get("hall.chat.xmpp.room", "neerc"));
        muc.addMessageListener(new MyMessageListener());

        connection.addPacketListener(new MyPresenceListener(), new PacketTypeFilter(Presence.class));
        connection.addPacketListener(new TaskPacketListener(this), new PacketExtensionFilter("x", XmlUtils.NAMESPACE_TASKS));

        join();

        debugConnection();

        connected = true;
        //FIXME: mucListener.connected(this);
    }

    public void initAutoReconnect(ConnectionListener listener) {
        stopAutoReconnect();
        reconnectThread = new ReconnectThread(listener);
        reconnectThread.setDaemon(true);
        reconnectThread.start();
    }
    
    public void stopAutoReconnect() {
        if (reconnectThread != null && reconnectThread.isAlive()) {
            reconnectThread.setDone();
        }        
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    private void authenticate() throws XMPPException {
        connection.login(user.id, password, connection.getHost());
    }
    
    private void join() {
        try {
            // Joins the new room and retrieves history
        	
            DiscussionHistory history = new DiscussionHistory();
            if (lastActivity != null) {
                history.setSince(new Date(lastActivity.getTime() + 1));
            } else {
                if (System.getProperty("history") != null) {
                    int size = Integer.parseInt(System.getProperty("history"));
                    history.setMaxStanzas(size);
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    history.setSince(calendar.getTime());
                }
            }
        	
            muc.join(
                    user.id, // nick
                    "",   // password
                    history,
                    SmackConfiguration.getPacketReplyTimeout()
            );
        } catch (XMPPException e) {
            Log.writeError("Unable to join room", e);
        }

        try {
            queryUsers();
            queryTasks();
        } catch (XMPPException e) {
            Log.writeError("Unable to communicate with NEERC service", e);
        }
    }
    
    public void debugConnection() {
        Log.writeDebug("User: " + connection.getUser());
        Log.writeDebug("Connected: " + connection.isConnected());
        Log.writeDebug("Authenticated: " +  connection.isAuthenticated());
        Log.writeDebug("Joined: " + muc.isJoined());
    }
    
	@Override
	public void newTask(Task task) {
		NeercTaskIQ packet = new NeercTaskIQ(task);
		packet.setTo(SettingsManager.instance().get("hall.chat.xmpp.service", "neerc.localhost"));
		Log.writeDebug(packet.toXML());
		connection.sendPacket(packet);
    }

	public IQ query(String what) throws XMPPException {
		Packet packet = new NeercIQ(what);
		packet.setTo(SettingsManager.instance().get("hall.chat.xmpp.service", "neerc.localhost"));
		
		PacketCollector collector = connection.createPacketCollector(
			new PacketIDFilter(packet.getPacketID()));
		connection.sendPacket(packet);

		IQ response = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		collector.cancel();
		if (response == null) {
			throw new XMPPException("No response from the server.");
		} else if (response.getType() == IQ.Type.ERROR) {
			throw new XMPPException(response.getError());
		}
//		LOG.debug("parsed " + response.getClass().getName());
		return response;
    }
	
	public void queryUsers() throws XMPPException {
		IQ iq = query("users");
		if (!(iq instanceof NeercUserListIQ)) {
		    throw new XMPPException("unparsed iq packet");
		}
		NeercUserListIQ packet = (NeercUserListIQ) iq;
        
		for (UserInfo user: packet.getUsers()) {
			boolean addUser = !users.containsKey(user.id); 
            users.put(user.id, user);
            
            for (ChatListener listener : chatListeners) {
            	if (addUser) {
            		listener.addUser(user);
            	} else {
            		listener.updateUser(user);
            	}
            }
		}
	}

	public void queryTasks() throws XMPPException {
		IQ iq = query("tasks");
		
		if (!(iq instanceof NeercTaskListIQ)) {
		    throw new XMPPException("unparsed iq packet");
		}
		NeercTaskListIQ packet = (NeercTaskListIQ) iq;
		
		for (Task task: packet.getTasks()) {
			boolean addTask = !tasks.containsKey(task.getId());
			if (task.getId() >= nextTaskId) {
				nextTaskId = task.getId() + 1; 
			}
			for (TaskListener listener : taskListeners) {
				if (addTask) {
					tasks.put(task.getId(), task);
					listener.addTask(task);
				} else {
					listener.updateTask(task);
				}
			}
			
		}
	}


    public MultiUserChat getMultiUserChat() {
        return muc;
    }

    public XMPPConnection getConnection() {
        return connection;
    }

    private class MyPresenceListener implements PacketListener {
        public void processPacket(Packet packet) {
            if (!(packet instanceof Presence)) {
                return;
            }
            Presence presence = (Presence) packet;
            // Filter presence by room name
            final String from = presence.getFrom();
            if (!from.startsWith(SettingsManager.instance().get("hall.chat.xmpp.room", "neerc"))) {
                return;
            }
            final MUCUser mucExtension = (MUCUser) packet.getExtension("x", "http://jabber.org/protocol/muc#user");
            if (mucExtension != null) {
                MUCUser.Item item = mucExtension.getItem();
                Log.writeDebug(from + " " + item.toString());
                //TODO: mucListener.roleChanged(from, item.getRole());
            }
            if (presence.isAvailable()) {
                //TODO: mucListener.joined(from);
            } else {
                //TODO: mucListener.left(from);
            	for (ChatListener listener: chatListeners) {
            		listener.removeUser(from);
            	}
            }
        }
    }

    private class MyMessageListener implements PacketListener {
        @Override
        public void processPacket(Packet packet) {
            if (!(packet instanceof org.jivesoftware.smack.packet.Message)) {
                return;
            }
            
            Log.writeDebug(packet.toXML());

            org.jivesoftware.smack.packet.Message xmppMessage = (org.jivesoftware.smack.packet.Message) packet;

            Date timestamp = null;
            for (PacketExtension extension : xmppMessage.getExtensions()) {
                if ("jabber:x:delay".equals(extension.getNamespace())) {
                    DelayInformation delayInformation = (DelayInformation) extension;
                    timestamp = delayInformation.getStamp();
                } else {
                    Log.writeError("Found unknown packet extenstion " + extension.getClass().getSimpleName() + " with namespace " + extension.getNamespace());
                }
            }

            boolean history = true;
            if (timestamp == null) {
                timestamp = new Date();
                history = false;
            }

            Message msg = new Message();
            msg.fromId = xmppMessage.getFrom();
            msg.fromName = users.get(msg.fromId) == null ? msg.fromId.substring(msg.fromId.indexOf("/") + 1, msg.fromId.length()) : users.get(msg.fromId).name;
            msg.toId = xmppMessage.getTo();
            msg.toName = users.get(msg.toId) == null ? msg.toId : users.get(msg.toId).name;
            msg.text = xmppMessage.getBody();
            
            msg.time = timestamp;
            
            for (ChatListener listener: chatListeners) {
            	listener.newMessage(msg);
            }

            lastActivity = timestamp;
        }
    }
    
    private class Pinger extends Thread {
        public void run() {
            while (true) {
                try {
                    sleep(SettingsManager.instance().get("hall.chat.xmpp.ping.interval", 5) * 1000);
                    if (!connected) {
                        continue;
                    }
                    query("ping");
                } catch (InterruptedException e) {
                    break;
                } catch (XMPPException e) {
                    Log.writeDebug("ping failed");
                    if (!connected) {
                        continue;
                    }
                    try {
                        disconnect();
                    } catch (Exception ex) {

                    }
                }
            }
        }
    }

    private class ReconnectThread extends Thread {
        private ConnectionListener listener;
        private final int RECONNECT_IN = 10;
        private boolean done;

        public ReconnectThread(ConnectionListener listener) {
            super();
            this.listener = listener;
        }
        
        public void setDone() {
            this.done = true;
        }

        public void run() {
            done = false;
            do {
                try {
                    for (int i = RECONNECT_IN; i > 0; i--) {
                        listener.reconnectingIn(i);
                        sleep(1000);
                        if (done) break;
                    }
                    if (done) break;
                    connect();
                    listener.reconnectionSuccessful();
                    done = true;
                } catch (InterruptedException e) {
                    break;
                } catch (RuntimeException e)  {
                    listener.reconnectionFailed(e);
                }
            } while (!done);
        }
    }


    private class ReconnectListener extends DefaultConnectionListener {
        @Override
        public void connectionClosed() {
            connected = false;
            //TODO: initAutoReconnect((ConnectionListener)mucListener);
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            connected = false;
            //TODO: initAutoReconnect((ConnectionListener)mucListener);
        }
    }
    
	@Override
	public void addListener(ChatListener listener) {
		chatListeners.add(listener);
		
		for (UserInfo user : users.values()) {
			listener.addUser(user);
		}
		
		for (Message msg : history) {
			listener.newMessage(msg);
		}
	}
	
	@Override
	public void addListener(TaskListener listener) {
		taskListeners.add(listener);
		
		for (Task task : tasks.values()) {
			listener.addTask(task);
		}
	}

	@Override
	public void sendMessage(Message message) {
		//TODO: private messages
		try {
            muc.sendMessage(message.text);
        } catch (XMPPException e) {
            Log.writeError("Unable to write message", e);
        }
	}

	@Override
	public UserInfo getUser() {
		return user;
	}

	@Override
	public void Start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				connect();
		        if (SettingsManager.instance().get("hall.chat.xmpp.ping.enabled", true)) {
		            (new Pinger()).start();
		        }
			}
			
		}).start();
	}

	@Override
	public void changeTaskState(int id, TaskState state) {
		NeercTaskResultIQ packet = new NeercTaskResultIQ(tasks.get(id), state);
		packet.setTo(SettingsManager.instance().get("hall.chat.xmpp.service", "neerc.localhost"));
		connection.sendPacket(packet);
	}

	@Override
	public TaskPerformer getSelf() {
		return self;
	}

	@Override
	public void Stop() {
		disconnect();
		if (reconnectThread != null) {
			reconnectThread.interrupt();
			try {
				reconnectThread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
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

	@Override
	public Iterable<TaskPerformer> getPerformers() {
		Set<TaskPerformer> performers = new HashSet<TaskPerformer>();
		for (UserInfo ui : users.values()) {
			TaskPerformer p = new TaskPerformer(ui.name);
			performers.add(p);
		}
		return performers;
	}

}
