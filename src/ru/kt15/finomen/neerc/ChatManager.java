package ru.kt15.finomen.neerc;

public interface ChatManager {
	public void addListener(ChatListener listener);
	public void sendMessage(Message message);
	public UserInfo getSelf();
}
