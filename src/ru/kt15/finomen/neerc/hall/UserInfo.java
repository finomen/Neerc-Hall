package ru.kt15.finomen.neerc.hall;

public class UserInfo {
	public String id;
	public String name;
	public UserStatus status;
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return id.equals(o);
	}
}
