package ru.kt15.finomen.neerc.hall;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Task {
	private final int id;
	private final String text;
	private final Date time;
	private final TaskPerformer[] performers;
	private final Map<TaskPerformer, TaskState> state;
	private final TaskState.StateId[] possibleStates;
	private final TaskManager manager;
	
	static public class TaskState {
		private final StateId id;
		private final String message;
		
		public enum StateId{
			ASSIGNED,
			IN_PROGRESS,
			DONE,
			FAILED,
			UPDATING
		}
		
		public static TaskState assigned() {
			return new TaskState(StateId.ASSIGNED);
		}
		
		public static TaskState updating() {
			return new TaskState(StateId.UPDATING);
		}
		
		public static TaskState inProgress() {
			return new TaskState(StateId.IN_PROGRESS);
		}
		
		public static TaskState inProgress(String message) {
			return new TaskState(StateId.IN_PROGRESS, message);
		}
		
		public static TaskState done() {
			return new TaskState(StateId.DONE);
		}
		
		public static TaskState done(String message) {
			return new TaskState(StateId.DONE, message);
		}
		
		public static TaskState failed() {
			return new TaskState(StateId.FAILED);
		}
		
		public static TaskState failed(String message) {
			return new TaskState(StateId.FAILED, message);
		}
		
		private TaskState(StateId id) {
			this.id = id;
			this.message = "";
		}
		
		private TaskState(StateId id, String message) {
			this.id = id;
			this.message = message;
		}
		
		public TaskState.StateId getId() {
			return id;
		}
		
		public String getMessage() {
			return message;
		}
	}

	static public class TaskPerformer {
		private final String name;
		
		public TaskPerformer(String name) {
			this.name = name;
			
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TaskPerformer) {
				TaskPerformer other = (TaskPerformer)obj;
				return other.name.equals(name);
			} else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}
	
	public Task(TaskManager manager, int id, String text, Date time, TaskPerformer[] performers, TaskState.StateId[] possibleStates) {
		this.manager = manager;
		this.id = id;
		this.text = text;
		this.time = time;
		this.performers = performers;
		this.possibleStates = possibleStates;
		this.state = new HashMap<TaskPerformer, TaskState>();
		
		for (TaskPerformer performer : performers) {
			TaskState ts = TaskState.assigned();
			state.put(performer, ts);
		}
	}
	
	public Task(TaskManager manager, int id, String text, Date time, TaskPerformer[] performers, TaskState.StateId[] possibleStates, Map<TaskPerformer, TaskState> state) {
		this.manager = manager;
		this.id = id;
		this.text = text;
		this.time = time;
		this.performers = performers;
		this.possibleStates = possibleStates;
		this.state = state;
	}

	public int getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public Date getTime() {
		return time;
	}
	
	public TaskPerformer[] getPerformerList() {
			return performers;
	}
	
	public TaskState getState() {
		return state.get(manager.getSelf());
	}
	
	public TaskState getState(TaskPerformer performer) {
		return state.get(performer);
	}
	
	public TaskState.StateId[] getPossibleStates() {
		return possibleStates;
	}
	
	public void changeState(TaskState state) {
		manager.changeTaskState(id, state);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task t = (Task) obj;
			return id == t.id;
		} else {
			return false;
		}
	}
}
