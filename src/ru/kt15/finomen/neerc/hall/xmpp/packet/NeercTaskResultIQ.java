package ru.kt15.finomen.neerc.hall.xmpp.packet;

import ru.kt15.finomen.neerc.hall.Task;

/**
 * @author Dmitriy Trofimov
 */
public class NeercTaskResultIQ extends NeercIQ {
	private Task task;
	private Task.TaskState result;

	public NeercTaskResultIQ(Task task, Task.TaskState result) {
		super("taskstatus", "taskstatus");
		this.task = task;
		this.result = result;
	}

	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(getElementName());
		buf.append(" xmlns=\"").append(getNamespace()).append("\"");
		buf.append(" id=\"").append(task.getId()).append("\"");
		buf.append(" type=\"").append(escape(result.getId().xmlValue)).append("\"");
		buf.append(" value=\"").append(escape(result.getMessage())).append("\"");
		buf.append(" />");
		return buf.toString();
	}
	
}
