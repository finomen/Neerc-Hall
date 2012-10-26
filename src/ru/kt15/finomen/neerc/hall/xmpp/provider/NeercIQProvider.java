package ru.kt15.finomen.neerc.hall.xmpp.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

import ru.kt15.finomen.neerc.hall.TaskManager;
import ru.kt15.finomen.neerc.hall.xmpp.packet.*;
import ru.kt15.finomen.neerc.hall.xmpp.utils.XmlUtils;

/**
 * @author Dmitriy Trofimov
 */
public class NeercIQProvider implements IQProvider {
	private final TaskManager taskManager;
	
	private NeercIQProvider(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	public static void register(TaskManager taskManager) {
		ProviderManager pm = ProviderManager.getInstance();
		IQProvider provider = new NeercIQProvider(taskManager);
		pm.addIQProvider("query", XmlUtils.NAMESPACE_USERS, provider);
		pm.addIQProvider("query", XmlUtils.NAMESPACE_TASKS, provider);
	}

	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		String namespace = parser.getNamespace();
		NeercIQ packet;
		if (XmlUtils.NAMESPACE_USERS.equals(namespace)) {
			packet = new NeercUserListIQ();
		} else if (XmlUtils.NAMESPACE_TASKS.equals(namespace)) {
			packet = new NeercTaskListIQ(taskManager);
		} else {
			throw new UnsupportedOperationException();
		}
		// TODO: clock
		packet.parse(parser);
		return packet;
	}
}
