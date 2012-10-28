package ru.kt15.finomen.neerc.hall.xmpp.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.xmlpull.v1.XmlPullParser;

import ru.kt15.finomen.neerc.hall.UserInfo;
import ru.kt15.finomen.neerc.hall.xmpp.utils.XmlUtils;

/**
 * @author Dmitriy Trofimov
 */
public class NeercUserListIQ extends NeercIQ {
	private Collection<UserInfo> users = new ArrayList<UserInfo>();

	public NeercUserListIQ() {
		super("users");
	}
	public String getElementName() {
		return "query";
	}

	public String getNamespace() {
		return XmlUtils.NAMESPACE_USERS;
	}

	public Collection<UserInfo> getUsers() {
		return Collections.unmodifiableCollection(users);
	}
 
	public void addUser(UserInfo user) {
		users.add(user);
	}

	public void addUser(String name, String group, boolean power) {
		UserInfo ui = new UserInfo();
		ui.name = name;
		ui.group = group;
		ui.power = power;
		ui.id = name;//FIXME:
		addUser(ui);
	}

	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
		for (UserInfo user: users) {
			buf.append("<user");
			buf.append(" name=\"").append(escape(user.getName())).append("\"");
			buf.append(" group=\"").append(escape(user.getGroup())).append("\"");
			buf.append(" power=\"").append(user.isPower() ? "yes" :"no").append("\" />");
		}
		buf.append("</").append(getElementName()).append(">");
		return buf.toString();
	}

	public void parse(XmlPullParser parser) throws Exception {
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("user")) {
					addUser(parseUser(parser));
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("query")) {
					done = true;
				}
			}
		}
	}

	private UserInfo parseUser(XmlPullParser parser) throws Exception {
		System.out.println(parser.getText()); //FIXME:
		String name = parser.getAttributeValue("", "name");
		String group = parser.getAttributeValue("", "group");
		boolean power = "yes".equals(parser.getAttributeValue("", "power"));
		UserInfo ui = new UserInfo();
		ui.id = name; //FIXME:
		ui.name = name;
		ui.group = group;
		ui.power = power;
		return ui;
	}
}
