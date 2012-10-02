package ru.kt15.finomen.neerc.timer;

public enum TimerStatus {
	BEFORE,
	RUNNING,
	PAUSED,
	OVER,
	UNKNOWN,
	BAD_STATUS;
	
	public static TimerStatus getById(int id) {
		switch (id) {
		case 0:
			return UNKNOWN;
		case 1:
			return BEFORE;
		case 2:
			return RUNNING;
		case 3:
			return PAUSED;
		case 4:
			return OVER;
		}
		
		return BAD_STATUS;
	}
}
