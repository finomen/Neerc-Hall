package ru.kt15.finomen.neerc.core;

import java.util.Date;

public interface ILog {
	void writeError(Date time, String s);
	void writeInfo(Date time, String s);
	void writeDebug(Date time, String s);
}
