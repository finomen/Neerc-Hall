package ru.kt15.finomen.neerc.hall.xmpp.provider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

import ru.kt15.finomen.neerc.hall.Task;
import ru.kt15.finomen.neerc.hall.Task.TaskPerformer;
import ru.kt15.finomen.neerc.hall.Task.TaskState;
import ru.kt15.finomen.neerc.hall.TaskManager;
import ru.kt15.finomen.neerc.hall.xmpp.utils.XmlUtils;

/**
 * @author Evgeny Mandrikov
 */
public class NeercTaskPacketExtensionProvider implements PacketExtensionProvider {
	private final TaskManager taskManager;
	
	public NeercTaskPacketExtensionProvider(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
    public static void register(TaskManager taskManager) {
        ProviderManager.getInstance().addExtensionProvider(
                "x",
                XmlUtils.NAMESPACE_TASKS,
                new NeercTaskPacketExtensionProvider(taskManager)
        );
    }

    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
        NeercTaskPacketExtension neercPacketExtension = new NeercTaskPacketExtension();
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("task")) {
                    neercPacketExtension.setTask(parseTask(taskManager, parser));
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals("x")) {
                    done = true;
                }
            }
        }
        return neercPacketExtension;
    }

    private Task parseTask(TaskManager taskManager, XmlPullParser parser) throws Exception {
    	Map<TaskPerformer, TaskState> state = new HashMap<Task.TaskPerformer, Task.TaskState>();
		boolean done = false;
		int id = Integer.parseInt(parser.getAttributeValue("", "id"));
		String title = parser.getAttributeValue("", "title");
		Task.TaskType type = Task.TaskType.fromString(parser.getAttributeValue("", "type"));
		
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("status")) {
					TaskPerformer p = new TaskPerformer(parser.getAttributeValue("", "for"));
					Task.TaskState s = Task.TaskState.fromStrings(parser.getAttributeValue("", "type"), parser.getAttributeValue("", "value"));
					state.put(p, s);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("task")) {
					done = true;
				}
			}
		}
		
		TaskPerformer[] performers = state.keySet().toArray(new TaskPerformer[0]);
				
		return new Task(
			taskManager,
			id,
			title,
			new Date(), //FIXME
			performers,
			type,
			state
		);
    }
}
