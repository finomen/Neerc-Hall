package ru.kt15.finomen.neerc.core;

public class SynchronizedTime {
	private final long cTime; 
	private long sync;
	private long frozen, correction;
	public SynchronizedTime(long time, boolean frozen) {
		cTime = time;
		sync = System.currentTimeMillis();
		correction = 0;
		if (frozen)
			this.frozen = System.currentTimeMillis();
	}

	public long get() {
		if (frozen == 0) {
			return Math.max(0, cTime + sync - System.currentTimeMillis() + correction);
		} else {
			return Math.max(0, cTime + sync - frozen + correction);
		}
	}
	
	public void sync(long time) {
		long ct = System.currentTimeMillis();
		long diff = ct - get();
			correction += diff;
		sync = ct;
	}

	public void freeze() {
		if (frozen == 0) {
			frozen = System.currentTimeMillis();
		}
	}

	public void resume() {
		if (frozen != 0) {
			correction += System.currentTimeMillis() - frozen;
			frozen = 0;
		}	
	}
}