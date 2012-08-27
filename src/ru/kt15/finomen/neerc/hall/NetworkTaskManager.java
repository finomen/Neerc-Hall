/**
 * 
 */
package ru.kt15.finomen.neerc.hall;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.kt15.finomen.neerc.core.Core;
import ru.kt15.finomen.neerc.core.net.Endpoint;
import ru.kt15.finomen.neerc.core.net.ProtobufConnection;
import ru.kt15.finomen.neerc.core.net.proto.TaskManagement;
import ru.kt15.finomen.neerc.hall.Task.TaskPerformer;
import ru.kt15.finomen.neerc.hall.Task.TaskState;

/**
 * @author Nikolay Filchenko
 *
 */
public class NetworkTaskManager implements TaskManager {
	private ProtobufConnection connection;
	private final Set<TaskListener> listeners = new HashSet<TaskListener>();
	private final TaskPerformer self = new TaskPerformer(Core.getId());
	
	public void setConnection(ProtobufConnection connection) {
		this.connection = connection;
		connection.AddHandler(this);
	}
	
	public void HandlePacket(TaskManagement.TaskUpdate upd) {
		for (TaskManagement.Task task : upd.getTasksList()) {
			switch (task.getType()) {
			case REMOVE:
				for (TaskListener list : listeners) {
					list.removeTask(task.getId());
				}
				break;
			case ADD:
			case CHANGE:
				List<TaskPerformer> performers = new ArrayList<TaskPerformer>();
				Map<TaskPerformer, TaskState> states = new HashMap<TaskPerformer, TaskState>();
				for (TaskManagement.TaskPerformer p : task.getPerformersList()) {
					TaskPerformer perf = new TaskPerformer(p.getName()); 
					performers.add(perf);
					switch(p.getState()) {
					case ASSIGNED:
						states.put(perf, TaskState.assigned());
						break;
					case DONE:
						states.put(perf, TaskState.done(p.getStateMessage()));
						break;
					case FAILED:
						states.put(perf, TaskState.failed(p.getStateMessage()));
						break;
					case IN_PROGRESS:
						states.put(perf, TaskState.inProgress(p.getStateMessage()));
						break;
					}
				}
				List<TaskState.StateId> pStates = new ArrayList<TaskState.StateId>();
				for (TaskManagement.TaskState s : task.getPossibleStatesList()) {
					switch(s) {
					case ASSIGNED:
						pStates.add(TaskState.StateId.ASSIGNED);
						break;
					case DONE:
						pStates.add(TaskState.StateId.DONE);
						break;
					case FAILED:
						pStates.add(TaskState.StateId.FAILED);
						break;
					case IN_PROGRESS:
						pStates.add(TaskState.StateId.IN_PROGRESS);
						break;
					}
				}
								
				Task t = new Task(this, task.getId(), task.getText(), new Date(task.getTime()), performers.toArray(new TaskPerformer[0]), pStates.toArray(new TaskState.StateId[0]), states);
				for (TaskListener list : listeners) {
					switch(task.getType()) {
					case ADD:
						list.addTask(t);
						break;
					case CHANGE:
						list.updateTask(t);
						break;
					case REMOVE:
						throw new Error("Somthing wrong with JVM or compiled code");
					}
				}
			}
		}
	}
	
	@Override
	public void changeTaskState(int id, TaskState state) {
		TaskManagement.TaskState encodedState = TaskManagement.TaskState.ASSIGNED;
		switch (state.getId()) {
		case ASSIGNED:
			encodedState = TaskManagement.TaskState.ASSIGNED;
			break;
		case DONE:
			encodedState = TaskManagement.TaskState.DONE;
			break;	
		case FAILED:
			encodedState = TaskManagement.TaskState.FAILED;
			break;
		case IN_PROGRESS:
			encodedState = TaskManagement.TaskState.IN_PROGRESS;
			break;
		default:
			//FIXME: throw
			return;
		}
		TaskManagement.TaskUpdate task = TaskManagement.TaskUpdate.newBuilder().addTasks(TaskManagement.Task.newBuilder().setId(id).addPerformers(TaskManagement.TaskPerformer.newBuilder().setName(self.getName()).setStateMessage(state.getMessage()).setState(encodedState).build()).setType(TaskManagement.UpdateType.CHANGE).build()).build();
		connection.sendTo(new Endpoint("TaskServer"), task);
	}

	@Override
	public void addListener(TaskListener listener) {
		listeners.add(listener);		
	}

	@Override
	public TaskPerformer getSelf() {
		return self;
	}

}
