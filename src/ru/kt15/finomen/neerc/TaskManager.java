package ru.kt15.finomen.neerc;

import ru.kt15.finomen.neerc.Task.TaskPerformer;

public interface TaskManager {
	public void changeTaskState(int id, Task.TaskState state);
	public void addListener(TaskListener listener);
	public TaskPerformer getSelf();
}
