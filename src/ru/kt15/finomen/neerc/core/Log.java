package ru.kt15.finomen.neerc.core;

import java.util.Date;

public abstract class Log implements ILog{
	private static ILog logImpl = new SystemLog();
	
	public static void writeError(String s, Exception e) {
		writeError(s + ":" + e.getLocalizedMessage());
	}
	
	public static void writeError(String s) {
		logImpl.writeError(new Date(), s);
	}
	
	public static void writeInfo(String s) {
		logImpl.writeInfo(new Date(), s);
	}
	
	public static void writeDebug(String s) {
		logImpl.writeDebug(new Date(), s);
	}
	
	public static void setImpl(ILog impl) {
		logImpl = impl;
	}
		
	private static class SystemLog extends Log {

		@Override
		public void writeError(Date time, String s) {
			System.err.println("[ " + time.toString() + " ] ERROR: " + s);
		}

		@Override
		public void writeInfo(Date time, String s) {
			System.out.println("[ " + time.toString() + " ] INFO:  " + s);
		}

		@Override
		public void writeDebug(Date time, String s) {
			System.out.println("[ " + time.toString() + " ] DEBUG: " + s);
		}
		
	}
}
