package ru.kt15.finomen.neerc.hall;

public interface ChatListener {
	void addUser(UserInfo info);
	void updateUser(UserInfo info);
	void removeUser(String id);
	void newMessage(Message  message);
}
