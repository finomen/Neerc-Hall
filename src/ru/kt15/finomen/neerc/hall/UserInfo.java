package ru.kt15.finomen.neerc.hall;

public class UserInfo {
	public String id;
	public String name;
	public UserStatus status;
	
	public String group;
	public boolean power;
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return id.equals(o);
	}
	
	public String getGroup() {
		return group;
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isPower() {
		return power;
	}
}
