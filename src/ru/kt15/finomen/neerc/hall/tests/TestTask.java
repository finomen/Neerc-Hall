/**
 * 
 */
package ru.kt15.finomen.neerc.hall.tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import ru.kt15.finomen.neerc.hall.Task;
import ru.kt15.finomen.neerc.hall.TaskListener;
import ru.kt15.finomen.neerc.hall.TaskManager;
import ru.kt15.finomen.neerc.hall.Task.TaskState;

/**
 * @author Nikolay Filchenko
 *
 */
public class TestTask {
	class TaskManagerImpl implements TaskManager {
		@Override
		public void changeTaskState(int id, Task.TaskState state) {
			
		}

		@Override
		public void addListener(TaskListener listener) {
			
		}

		@Override
		public Task.TaskPerformer getSelf() {
			return new Task.TaskPerformer("self");
		}

		@Override
		public void Start() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void Stop() {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskState#assigned()
	 */
	@Test
	public void testTaskTaskStateAssigned() {
		Task.TaskState ts = Task.TaskState.assigned();
		assertEquals(Task.TaskState.StateId.ASSIGNED, ts.getId());
		assertTrue(ts.getMessage().isEmpty());
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskState#updating()
	 */
	@Test
	public void testTaskTaskStateUpdating() {
		Task.TaskState ts = Task.TaskState.updating();
		assertEquals(Task.TaskState.StateId.UPDATING, ts.getId());
		assertTrue(ts.getMessage().isEmpty());
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskState#inProgress()
	 */
	@Test
	public void testTaskTaskStateInProgress() {
		Task.TaskState ts = Task.TaskState.inProgress();
		assertEquals(Task.TaskState.StateId.IN_PROGRESS, ts.getId());
		assertTrue(ts.getMessage().isEmpty());
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskState#inProgress(String)
	 */
	@Test
	public void testTaskTaskStateInProgressString() {
		Task.TaskState ts = Task.TaskState.inProgress("Message");
		assertEquals(Task.TaskState.StateId.IN_PROGRESS, ts.getId());
		assertEquals("Message", ts.getMessage());
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskState#Done()
	 */
	@Test
	public void testTaskTaskStateDone() {
		Task.TaskState ts = Task.TaskState.done();
		assertEquals(Task.TaskState.StateId.DONE, ts.getId());
		assertTrue(ts.getMessage().isEmpty());
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskState#Done(String)
	 */
	@Test
	public void testTaskTaskStateDoneString() {
		Task.TaskState ts = Task.TaskState.done("Message");
		assertEquals(Task.TaskState.StateId.DONE, ts.getId());
		assertEquals("Message", ts.getMessage());
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskState#Failed()
	 */
	@Test
	public void testTaskTaskStateFailed() {
		Task.TaskState ts = Task.TaskState.failed();
		assertEquals(Task.TaskState.StateId.FAILED, ts.getId());
		assertTrue(ts.getMessage().isEmpty());
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskState#Failed(String)
	 */
	@Test
	public void testTaskTaskStateFailedString() {
		Task.TaskState ts = Task.TaskState.failed("Message");
		assertEquals(Task.TaskState.StateId.FAILED, ts.getId());
		assertEquals("Message", ts.getMessage());
	}	
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskPerformer#TaskPerformer(String)
	 */
	@Test
	public void testTaskTaskPerformerString() {
		Task.TaskPerformer tp = new Task.TaskPerformer("self");
		assertEquals("self", tp.getName());
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskPerformer#hashCode()
	 */
	@Test
	public void testTaskTaskPerformerHashCode() {
		Task.TaskPerformer tp = new Task.TaskPerformer("self");
		assertEquals("self".hashCode(), tp.hashCode());
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task.TaskPerformer#equals(Object)
	 */
	@Test
	public void testTaskTaskPerformerEquals() {
		Task.TaskPerformer tp1 = new Task.TaskPerformer("self");
		Task.TaskPerformer tp2 = new Task.TaskPerformer("self");
		Task.TaskPerformer tp3 = new Task.TaskPerformer("other");
		assertTrue(tp1.equals(tp1));
		assertTrue(tp1.equals(tp2));
		assertFalse(tp1.equals(tp3));
		assertFalse(tp1.equals(null));
		assertFalse(tp1.equals(new Integer(2)));
	}
	
	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#Task(ru.kt15.finomen.neerc.hall.TaskManager, int, java.lang.String, java.util.Date, ru.kt15.finomen.neerc.hall.Task.TaskPerformer[], ru.kt15.finomen.neerc.hall.Task.TaskState.StateId[])}.
	 */
	@Test
	public void testTaskTaskManagerIntStringDateTaskPerformerArrayStateIdArray() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED};
		Task t = new Task(tm, 1, "Text", d, perfs, psd);
		
		assertEquals(1, t.getId());
		assertEquals("Text", t.getText());
		assertEquals(d, t.getTime());
		assertEquals(1, t.getPossibleStates().length);
		assertEquals(Task.TaskState.StateId.ASSIGNED, t.getPossibleStates()[0]);
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#Task(ru.kt15.finomen.neerc.hall.TaskManager, int, java.lang.String, java.util.Date, ru.kt15.finomen.neerc.hall.Task.TaskPerformer[], ru.kt15.finomen.neerc.hall.Task.TaskState.StateId[], java.util.Map)}.
	 */
	@Test
	public void testTaskTaskManagerIntStringDateTaskPerformerArrayStateIdArrayMapOfTaskPerformerTaskState() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE};
		Map<Task.TaskPerformer, Task.TaskState> tsa = new HashMap<Task.TaskPerformer, Task.TaskState>();
		tsa.put(perfs[0], TaskState.done());
		Task t = new Task(tm, 1, "Text", d, perfs, psd, tsa);		
		
		assertEquals(1, t.getId());
		assertEquals("Text", t.getText());
		assertEquals(d, t.getTime());
		assertEquals(2, t.getPossibleStates().length);
		assertEquals(Task.TaskState.StateId.ASSIGNED, t.getPossibleStates()[0]);
		assertEquals(Task.TaskState.StateId.DONE, t.getPossibleStates()[1]);
		assertEquals(Task.TaskState.StateId.DONE, t.getState().getId());
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#getId()}.
	 */
	@Test
	public void testGetId() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED};
		Task t = new Task(tm, 1, "Text", d, perfs, psd);
		assertEquals(1, t.getId());
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#getText()}.
	 */
	@Test
	public void testGetText() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE};
		Map<Task.TaskPerformer, Task.TaskState> tsa = new HashMap<Task.TaskPerformer, Task.TaskState>();
		tsa.put(perfs[0], TaskState.done());
		Task t = new Task(tm, 1, "Text", d, perfs, psd, tsa);		
		
		assertEquals("Text", t.getText());
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#getTime()}.
	 */
	@Test
	public void testGetTime() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE};
		Map<Task.TaskPerformer, Task.TaskState> tsa = new HashMap<Task.TaskPerformer, Task.TaskState>();
		tsa.put(perfs[0], TaskState.done());
		Task t = new Task(tm, 1, "Text", d, perfs, psd, tsa);		
		
		assertEquals(d, t.getTime());
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#getPerformerList()}.
	 */
	@Test
	public void testGetPerformerList() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE};
		Map<Task.TaskPerformer, Task.TaskState> tsa = new HashMap<Task.TaskPerformer, Task.TaskState>();
		tsa.put(perfs[0], TaskState.done());
		Task t = new Task(tm, 1, "Text", d, perfs, psd, tsa);		
		
		assertEquals(1, t.getPerformerList().length);
		assertEquals(perfs[0], t.getPerformerList()[0]);
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#getState()}.
	 */
	@Test
	public void testGetState() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE};
		Map<Task.TaskPerformer, Task.TaskState> tsa = new HashMap<Task.TaskPerformer, Task.TaskState>();
		tsa.put(perfs[0], TaskState.done());
		Task t = new Task(tm, 1, "Text", d, perfs, psd, tsa);		
		
		assertEquals(Task.TaskState.StateId.DONE, t.getState().getId());
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#getState(ru.kt15.finomen.neerc.hall.Task.TaskPerformer)}.
	 */
	@Test
	public void testGetStateTaskPerformer() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE};
		Map<Task.TaskPerformer, Task.TaskState> tsa = new HashMap<Task.TaskPerformer, Task.TaskState>();
		tsa.put(perfs[0], TaskState.done());
		Task t = new Task(tm, 1, "Text", d, perfs, psd, tsa);		
		
		assertEquals(Task.TaskState.StateId.DONE, t.getState(new Task.TaskPerformer("self")).getId());
		assertEquals(null, t.getState(new Task.TaskPerformer("other")));
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#getPossibleStates()}.
	 */
	@Test
	public void testGetPossibleStates() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE};
		Map<Task.TaskPerformer, Task.TaskState> tsa = new HashMap<Task.TaskPerformer, Task.TaskState>();
		tsa.put(perfs[0], TaskState.done());
		Task t = new Task(tm, 1, "Text", d, perfs, psd, tsa);		
		
		assertEquals(Task.TaskState.StateId.DONE, t.getPossibleStates()[1]);
		assertEquals(Task.TaskState.StateId.DONE, t.getState().getId());
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#changeState(ru.kt15.finomen.neerc.hall.Task.TaskState)}.
	 */
	@Test
	public void testChangeState() {
		final AtomicBoolean b = new AtomicBoolean();
		b.set(false);
		
		TaskManager tm = new TaskManagerImpl() {
			@Override
			public void changeTaskState(int id, TaskState state) {
				assertEquals(1, id);
				assertEquals(Task.TaskState.StateId.DONE, state.getId());
				b.set(true);
			}
		};
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE};
		Map<Task.TaskPerformer, Task.TaskState> tsa = new HashMap<Task.TaskPerformer, Task.TaskState>();
		tsa.put(perfs[0], TaskState.done());
		Task t = new Task(tm, 1, "Text", d, perfs, psd, tsa);
		t.changeState(Task.TaskState.done());
		
		assertTrue(b.get());
	}

	/**
	 * Test method for {@link ru.kt15.finomen.neerc.hall.Task#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		TaskManager tm = new TaskManagerImpl();
		Date d = new Date();
		Task.TaskPerformer[] perfs = {new Task.TaskPerformer("self")};
		Task.TaskState.StateId[] psd = {Task.TaskState.StateId.ASSIGNED, Task.TaskState.StateId.DONE};
		Map<Task.TaskPerformer, Task.TaskState> tsa = new HashMap<Task.TaskPerformer, Task.TaskState>();
		tsa.put(perfs[0], TaskState.done());
		Task t1 = new Task(tm, 1, "Text", d, perfs, psd, tsa);
		Task t2 = new Task(tm, 1, "Text", d, perfs, psd, tsa);
		Task t3 = new Task(tm, 2, "Text", d, perfs, psd, tsa);
		
		assertTrue(t1.equals(t1));
		assertTrue(t1.equals(t2));
		assertFalse(t1.equals(t3));
		assertFalse(t1.equals(null));
		assertFalse(t1.equals(new Integer(1)));

	}

}
