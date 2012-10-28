package ru.kt15.finomen.neerc.hall;

import java.util.Date;

//TODO: remove public access from fileds
public class Message {
	public String fromId;
	public String fromName;
	public String toId;
	public String toName;
	public Flag type;
	public String text;
	public Date time;
	public boolean history; 
}
