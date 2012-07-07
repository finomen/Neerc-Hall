/**
 * 
 */
package ru.kt15.finomen.neerc;

/**
 * @author Nikolay Filchenko
 *
 */
public interface TaskListener {
	public void addTask(Task task);
	public void removeTask(int taskId);
	public void updateTask(Task task);
}
