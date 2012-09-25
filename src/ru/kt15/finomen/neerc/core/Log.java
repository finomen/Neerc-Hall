package ru.kt15.finomen.neerc.core;

import java.util.Date;

public abstract class Log {
	private static Log logImpl = new SystemLog();
	public static void writeError(String s) {
		logImpl.writeError(new Date(), s);
	}
	
	public static void writeInfo(String s) {
		logImpl.writeInfo(new Date(), s);
	}
	
	public static void writeDebug(String s) {
		logImpl.writeDebug(new Date(), s);
	}
	
	public static void setImpl(Log impl) {
		logImpl = impl;
	}
	
	protected abstract void writeError(Date time, String s);
	protected abstract void writeInfo(Date time, String s);
	protected abstract void writeDebug(Date time, String s);
	
	private static class SystemLog extends Log {

		@Override
		protected void writeError(Date time, String s) {
			System.err.println("[ " + time.toString() + " ] ERROR: " + s);
		}

		@Override
		protected void writeInfo(Date time, String s) {
			System.out.println("[ " + time.toString() + " ] INFO:  " + s);
		}

		@Override
		protected void writeDebug(Date time, String s) {
			System.out.println("[ " + time.toString() + " ] DEBUG: " + s);
		}
		
	}
}
