package ru.kt15.finomen.neerc;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;

import ru.kt15.finomen.neerc.Task.TaskPerformer;
import ru.kt15.finomen.neerc.Task.TaskState;
import org.eclipse.swt.widgets.Menu;

public class TaskWindow extends Composite implements TaskListener {
	private Table activeTasks;
	private Table historyTasks;
	private Map<Task.TaskPerformer, Integer> knownPerformerActive;
	private Map<Task.TaskPerformer, Integer> knownPerformerHistory;
	private Map<Task.TaskPerformer, TableColumn> performerActiveColumn;
	private Map<Task.TaskPerformer, TableColumn> performerHistoryColumn;
	private Map<Integer, TableItem> taskActiveRow;
	private Map<Integer, TableItem> taskHistoryRow;
	
	private static class TableResizer implements Listener {
		private final Table table;
		private final int[] mask;

		TableResizer(Table table, int[] mask) {
			this.table = table;
			this.mask = mask;
			table.addListener(SWT.Resize, this);
			table.addListener(SWT.CHANGED, this);
		}

		@Override
		synchronized public void handleEvent(Event arg0) {
				Rectangle rect = table.getClientArea ();
				int sum = 0;
				int count = 0;
				int realMask[] = new int[table.getColumnCount()];
				
				for (int i = 0; i < Math.min(mask.length, table.getColumnCount()); ++i) {
					realMask[i] = mask[i];
				}
				
				if (mask.length < table.getColumnCount()) {
					for (int i = mask.length; i < table.getColumnCount(); ++i) {
						realMask[i] = mask[mask.length - 1];
					}
				}
				
				for (int i = 0; i < realMask.length; ++i) {
					if (realMask[i] == 0) {
						count++;
						//TODO: this content width calculation is ugly
						int cWidth = table.getColumns()[table.getColumnOrder()[i]].getText().length() * 5 + 10;
						for (TableItem row : table.getItems()) {
							cWidth = Math.max(cWidth,  row.getText(i).length() * 5 + 10);
							
						}
						
						sum += cWidth;
						realMask[i] = -cWidth;
					} else {
						sum += realMask[i];
					}
				}
				
				for (int i = 0; i < realMask.length; ++i) {
					if (realMask[i] <= 0) {
						table.getColumn(i).setWidth(-realMask[i] + ((rect.width > sum) ? (rect.width - sum) / count : 0));
					} else {
						table.getColumn(i).setWidth(realMask[i]);
					}
				}
				
		}
		
	}
	
	static class StateChangeListener implements Listener {
		private final Table table;
		private final Shell shell;
		public StateChangeListener(Shell shell, Table table) {
			this.shell = shell;
			this.table = table;
			table.addListener(SWT.MouseDoubleClick, this);
		}
		
		@Override
		public void handleEvent(Event arg0) {
			TableItem[] sel = table.getSelection();
			if (sel.length == 1) {
				Task task = (Task) sel[0].getData();
				if (task.getState() != null) {
					new TaskStateChange(task, shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM).open();
				}
			}
		}
		
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TaskWindow(Composite parent, int style) {
		super(parent, style);
		
		
		knownPerformerActive = new HashMap<Task.TaskPerformer, Integer>();
		knownPerformerHistory = new HashMap<Task.TaskPerformer, Integer>();
		performerActiveColumn = new HashMap<Task.TaskPerformer, TableColumn>();
		performerHistoryColumn = new HashMap<Task.TaskPerformer, TableColumn>();
		taskActiveRow = new HashMap<Integer, TableItem>();
		taskHistoryRow = new HashMap<Integer, TableItem>();
		
		setLayout(new FillLayout(SWT.VERTICAL));
		
		Group grpActiveTasks = new Group(this, SWT.NONE);
		grpActiveTasks.setText("Active tasks");
		grpActiveTasks.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		activeTasks = new Table(grpActiveTasks, SWT.BORDER | SWT.FULL_SELECTION);
		activeTasks.setHeaderVisible(true);
		activeTasks.setLinesVisible(true);
		
		int[] mask = {50, 100, 0};
		new TableResizer(activeTasks, mask);
		
		TableColumn tblclmnId = new TableColumn(activeTasks, SWT.NONE);
		tblclmnId.setWidth(100);
		tblclmnId.setText("ID");
		
		TableColumn tblclmnTime = new TableColumn(activeTasks, SWT.NONE);
		tblclmnTime.setWidth(100);
		tblclmnTime.setText("Time");
		
		TableColumn tblclmnText = new TableColumn(activeTasks, SWT.NONE);
		tblclmnText.setWidth(100);
		tblclmnText.setText("Text");
		
		Group grpHistory = new Group(this, SWT.NONE);
		grpHistory.setText("History");
		grpHistory.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		historyTasks = new Table(grpHistory, SWT.BORDER | SWT.FULL_SELECTION);
		historyTasks.setHeaderVisible(true);
		historyTasks.setLinesVisible(true);
		new TableResizer(historyTasks, mask);
		
		TableColumn tblclmnId_1 = new TableColumn(historyTasks, SWT.NONE);
		tblclmnId_1.setWidth(100);
		tblclmnId_1.setText("ID");
		
		TableColumn tblclmnTime_1 = new TableColumn(historyTasks, SWT.NONE);
		tblclmnTime_1.setWidth(100);
		tblclmnTime_1.setText("Time");
		
		TableColumn tblclmnText_1 = new TableColumn(historyTasks, SWT.NONE);
		tblclmnText_1.setWidth(100);
		tblclmnText_1.setText("Text");
		
		new StateChangeListener(getShell(), activeTasks);
		new StateChangeListener(getShell(), historyTasks);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized(this) {
					try {
						wait(3000);
						TaskManager tm = new TaskManager() {

							@Override
							public void changeTaskState(int id, TaskState state) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void addListener(TaskListener listener) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public TaskPerformer getSelf() {
								return new TaskPerformer("self");
							}}; 
						Task.TaskPerformer[] pls = {new TaskPerformer("self"), new TaskPerformer("other1")};
						Task.TaskState.StateId[] pst = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE, Task.TaskState.StateId.IN_PROGRESS};
						Task t = new Task(tm, 1, "Task1", new Date(), pls, pst);
						addTask(t);
						Task.TaskPerformer[] pls1 = {new TaskPerformer("self1"), new TaskPerformer("other1")};
						Task.TaskState.StateId[] pst1 = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE, Task.TaskState.StateId.FAILED};
						t = new Task(tm, 2, "Task2", new Date(), pls1, pst1);
						System.out.println("UPD1");
						addTask(t);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		}).start();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void addTaskPerformers(Task.TaskPerformer[] list, Map<Task.TaskPerformer, Integer> knownPerformers, Map<Task.TaskPerformer, TableColumn> performerColumn, Table table) {
		for (Task.TaskPerformer performer : list) {
			if (knownPerformers.containsKey(performer)) {
				knownPerformers.put(performer, knownPerformers.get(performer) + 1);
			} else {
				System.out.println("Add column " + performer.getName());
				TableColumn tblclmn = new TableColumn(table, SWT.NONE);
				tblclmn.setWidth(100);
				tblclmn.setText(performer.getName());
				tblclmn.setData(performer);
				performerColumn.put(performer, tblclmn);
				knownPerformers.put(performer, 1);
			}
		}
	}
	
	private void removeTaskPerformers(Task.TaskPerformer[] list, Map<Task.TaskPerformer, Integer> knownPerformers, Map<Task.TaskPerformer, TableColumn> performerColumn) {
		for (Task.TaskPerformer performer : list) {
			knownPerformers.put(performer, knownPerformers.get(performer) - 1);
			if (knownPerformers.get(performer) == 0) {
				knownPerformers.remove(performer);
				performerColumn.get(performer).dispose();
				performerColumn.remove(performer);
			}
		}
	}
	
	private void fillRowWithTask(TableItem row, Task task) {
		row.setData(task);
		row.setText(0, "#" + task.getId());
		row.setText(1, task.getTime().toString());
		row.setText(2, task.getText());
		Table table = row.getParent();
		
		for (Task.TaskPerformer performer : task.getPerformerList()) {
			for (int i = 3; i < table.getColumnCount(); ++i) {
				if (performer.equals(table.getColumns()[table.getColumnOrder()[i]].getData())) {
					row.setText(table.getColumnOrder()[i], task.getState(performer).getId().name());
				}
			}
		}
	}
	
	@Override
	public void addTask(final Task task) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				
				if (task.getState() != null) {
					addTaskPerformers(task.getPerformerList(), knownPerformerActive, performerActiveColumn, activeTasks);
					TableItem row = new TableItem(activeTasks, SWT.NONE);
					fillRowWithTask(row, task);
					taskActiveRow.put(task.getId(), row);
				}
				
				addTaskPerformers(task.getPerformerList(), knownPerformerHistory, performerHistoryColumn, historyTasks);
				TableItem row = new TableItem(historyTasks, SWT.NONE);
				fillRowWithTask(row, task);
				taskHistoryRow.put(task.getId(), row);
			}
			
		});
	}

	@Override
	public void removeTask(final int taskId) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (taskActiveRow.containsKey(taskId)) {
					Task t = (Task) taskActiveRow.get(taskId).getData();
					taskActiveRow.get(taskId).dispose();
					removeTaskPerformers(t.getPerformerList(), knownPerformerActive, performerActiveColumn);
				}
				
				Task t = (Task) taskHistoryRow.get(taskId).getData();
				taskHistoryRow.get(taskId).dispose();
				removeTaskPerformers(t.getPerformerList(), knownPerformerHistory, performerHistoryColumn);
			}
			
		});
	}

	@Override
	public void updateTask(final Task task) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (taskActiveRow.containsKey(task.getId())) {
					Task t = (Task) taskActiveRow.get(task.getId()).getData();
					if (task.getState() == null) {
						taskActiveRow.get(task.getId()).dispose();
					} else {
						addTaskPerformers(task.getPerformerList(), knownPerformerActive, performerActiveColumn, activeTasks);
						fillRowWithTask(taskActiveRow.get(task.getId()), task);
					}
					removeTaskPerformers(t.getPerformerList(), knownPerformerActive, performerActiveColumn);
				}
				
				Task t = (Task) taskHistoryRow.get(task.getId()).getData();
				addTaskPerformers(task.getPerformerList(), knownPerformerHistory, performerHistoryColumn, activeTasks);
				fillRowWithTask(taskHistoryRow.get(task.getId()), task);
				removeTaskPerformers(t.getPerformerList(), knownPerformerHistory, performerHistoryColumn);
			}
			
		});
	}
}
