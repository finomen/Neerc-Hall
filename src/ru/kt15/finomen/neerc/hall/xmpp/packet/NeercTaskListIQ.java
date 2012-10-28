package ru.kt15.finomen.neerc.hall.xmpp.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.hall.Task;
import ru.kt15.finomen.neerc.hall.Task.TaskPerformer;
import ru.kt15.finomen.neerc.hall.Task.TaskState;
import ru.kt15.finomen.neerc.hall.TaskManager;
import ru.kt15.finomen.neerc.hall.xmpp.utils.XmlUtils;

/**
 * @author Dmitriy Trofimov
 */
public class NeercTaskListIQ extends NeercIQ {
	private Collection<Task> tasks = new ArrayList<Task>();
	private final TaskManager taskManager;

	public NeercTaskListIQ(TaskManager taskManager) {
		super("tasks");
		this.taskManager = taskManager;
	}
	public String getElementName() {
		return "query";
	}

	public String getNamespace() {
		return XmlUtils.NAMESPACE_TASKS;
	}

	public Collection<Task> getTasks() {
		return Collections.unmodifiableCollection(tasks);
	}
 
	public void addTask(Task task) {
		tasks.add(task);
	}

	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
		for (Task task: tasks) {
			buf.append("<task");
			buf.append(" xmlns=\"").append(getNamespace()).append("\"");
			buf.append(" id=\"").append(task.getId()).append("\"");
			buf.append(" type=\"").append(escape(task.getType().xmlValue)).append("\"");
			buf.append(" title=\"").append(escape(task.getText())).append("\"");		
			buf.append(">");
			
			for (TaskPerformer performer: task.getPerformerList()) {
				buf.append("<status ");
				buf.append(" for=\"").append(escape(performer.getName())).append("\"");
				buf.append(" type=\"").append(escape(task.getState(performer).getId().xmlValue)).append("\"");
				
				if (!task.getState(performer).getMessage().isEmpty()) {
					buf.append(" value=\"").append(escape(task.getState(performer).getMessage())).append("\"");
				}
				
				buf.append(" />");
			}
			
			buf.append("</task>");
		}
		buf.append("</").append(getElementName()).append(">");
		return buf.toString();
	}

	public void parse(XmlPullParser parser) throws Exception {
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("task")) {
					addTask(parseTask(taskManager, parser));
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("query")) {
					done = true;
				}
			}
		}
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
					Log.writeDebug("Task state: " + p.getName() + " = " + s.toString());
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
