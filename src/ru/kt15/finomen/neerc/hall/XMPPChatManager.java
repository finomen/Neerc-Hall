package ru.kt15.finomen.neerc.hall;

import java.util.Date;
import java.util.HashSet;
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
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import ru.kt15.finomen.neerc.core.Log;

public class XMPPChatManager implements ChatManager, Runnable {
	private final Set<ChatListener> listeners;
	private UserInfo self;
	private ConnectionConfiguration connConfig;
	private XMPPConnection connection;
	private Thread worker;
	private MultiUserChat conference;

	public XMPPChatManager() {
		listeners = new HashSet<ChatListener>();
		self = new UserInfo();
		self.id = "test@finomen.kt15.ru";
		self.status = UserStatus.OFFLINE;

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
	public UserInfo getSelf() {
		return self;
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
				conference.join("test");
				
				//PacketFilter filter = new AndFilter(new PacketTypeFilter(
				//		org.jivesoftware.smack.packet.Message.class));
			
				PacketListener myListener = new PacketListener() {
					public void processPacket(Packet packet) {
						if (packet instanceof org.jivesoftware.smack.packet.Message) {
							org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
							processMessage(message);
						}
					}
				};
				
				conference.addMessageListener(myListener);

				//connection.addPacketListener(myListener, filter);

				// раз в минуту просыпаемся и проверяем, что соединение не
				// разорвано
				while (connection.isConnected()) {
					Thread.sleep(60000);
				}
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

}
