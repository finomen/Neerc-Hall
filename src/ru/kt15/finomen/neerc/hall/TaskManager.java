package ru.kt15.finomen.neerc.hall;

import ru.kt15.finomen.neerc.hall.Task.TaskPerformer;

public interface TaskManager {
	public void newTask(Task task);
	public void changeTaskState(int id, Task.TaskState state);
	public void addListener(TaskListener listener);
	public TaskPerformer getSelf();
	public void Start();
	public void Stop();
	public int getNextId();
}
