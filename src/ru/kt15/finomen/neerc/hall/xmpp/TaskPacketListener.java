package ru.kt15.finomen.neerc.hall.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;

import ru.kt15.finomen.neerc.hall.xmpp.provider.NeercTaskPacketExtension;
import ru.kt15.finomen.neerc.hall.xmpp.utils.XmlUtils;

/**
 * @author Evgeny Mandrikov
 */
public class TaskPacketListener implements PacketListener {
	private final NeercXMPPConnection connection;
	
	public TaskPacketListener(NeercXMPPConnection connection) {
		this.connection = connection;
	}
	
    @Override
    public void processPacket(Packet packet) {
        Message message = (Message) packet;
        NeercTaskPacketExtension extension = (NeercTaskPacketExtension) message.getExtension("x", XmlUtils.NAMESPACE_TASKS);
        PacketExtension delay = message.getExtension("x", "jabber:x:delay");
        if (extension != null && delay == null) {
            connection.incomingTask(extension.getTask());
        }
    }
}
