package ru.kt15.finomen.neerc.hall.xmpp;

import org.jivesoftware.smack.ConnectionListener;

/**
 * @author Evgeny Mandrikov
 */
public abstract class DefaultConnectionListener implements ConnectionListener {
    @Override
    public void connectionClosed() {
    }

    @Override
    public void connectionClosedOnError(Exception e) {
    }

    @Override
    public void reconnectingIn(int i) {
    }

    @Override
    public void reconnectionSuccessful() {
    }

    @Override
    public void reconnectionFailed(Exception e) {
    }
}
