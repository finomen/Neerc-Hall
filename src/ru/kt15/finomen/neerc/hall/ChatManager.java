package ru.kt15.finomen.neerc.hall;

public interface ChatManager {
	public void addListener(ChatListener listener);
	public void sendMessage(Message message);
	public UserInfo getSelf();
	public void Start();
}
