package ru.kt15.finomen.neerc;

public interface ChatListener {
	void addUser(UserInfo info);
	void updateUser(UserInfo info);
	void removeUser(String id);
	void newMessgae(Message  message);
}
