package ru.kt15.finomen.neerc.core;

public class SynchronizedTime {
	private final long startTime; 
	private long frozen, correction;
		
	public SynchronizedTime(long time, boolean frozen) {
		startTime = System.nanoTime() / 1000;
		correction = 0;
		if (frozen) {
			this.frozen = 0;
		} else {
			this.frozen = -1;
		}
	}

	synchronized public long get() {
		if (frozen >= 0) {
			return frozen;
		} else {
			return System.nanoTime() / 1000 - startTime + correction;
		}
	}
	
	synchronized public void sync(long time) {
		if (Math.abs(time - get()) > 1000 ) {
			Log.writeError("Something wrong with clock, diff: " + (get() - time));
		}
			
		correction += time - get();
	}

	synchronized public void freeze() {
		frozen = get();
	}

	synchronized public void resume() {
		if (frozen >= 0) {
			long realTime = System.nanoTime() / 1000 - startTime;
			correction = get() - realTime;
			frozen = -1;
		}
	}
}