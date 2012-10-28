package ru.kt15.finomen.neerc.hall.xmpp;

import java.util.Date;

/**
 * @author Evgeny Mandrikov
 */
public interface MUCListener {
	//TODO:
    //void connected(XmppChat chat);

    void roleChanged(String jid, String role);

    void joined(String participant);

    void left(String participant);

    void messageReceived(String jid, String message, Date timestamp);

    void historyMessageReceived(String jid, String message, Date timestamp);
}
