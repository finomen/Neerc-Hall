package ru.kt15.finomen.neerc.hall.xmpp.packet;

import ru.kt15.finomen.neerc.hall.Task;
import ru.kt15.finomen.neerc.hall.Task.TaskPerformer;


/**
 * @author Dmitriy Trofimov
 */
public class NeercTaskIQ extends NeercIQ {
	private Task task;
	//TODO:
	public NeercTaskIQ(Task task) {
		super("task", "task");
		this.task = task;
	}

	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(getElementName());
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
		
		buf.append("</").append(getElementName()).append(">");
		return buf.toString();
	}
	
}
