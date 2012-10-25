package ru.kt15.finomen.neerc.hall.desktop;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ru.kt15.finomen.neerc.core.LocaleManager;
import ru.kt15.finomen.neerc.core.Localized;
import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.hall.ChatListener;
import ru.kt15.finomen.neerc.hall.ChatManager;
import ru.kt15.finomen.neerc.hall.Message;
import ru.kt15.finomen.neerc.hall.UserInfo;
import swing2swt.layout.BorderLayout;

public class ChatWindow extends Composite implements Localized, ChatListener {
	private Text text;
	private Table table;
	private LocaleManager localeManager;
	private Group grpUsers;
	private Button btnSend;
	private TableColumn tblclmnTime;
	private TableColumn tblclmnUser;
	private TableColumn tblclmnMessage;
	private List users;
	private Composite composite;
	private Map<String, UserInfo> chatMembers;
	private final ChatManager chatManager;
	/**
	 * Create the composite.
	 * @param localeManager 
	 * @param parent
	 * @param style
	 */
	public ChatWindow(ChatManager chatMgr, LocaleManager localeManager, Composite parent, int style) {
		super(parent, style);
		chatManager = chatMgr;
		this.localeManager = localeManager;
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		setLayout(gridLayout);	
		
		grpUsers = new Group(this, SWT.NONE);
		GridData gd_grpUsers = new GridData(GridData.FILL_VERTICAL);
		gd_grpUsers.widthHint = 138;
		grpUsers.setLayoutData(gd_grpUsers);
		grpUsers.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		users = new List(grpUsers, SWT.BORDER);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLinesVisible(false);
		
		tblclmnTime = new TableColumn(table, SWT.NONE);
		tblclmnTime.setWidth(100);
		
		tblclmnUser = new TableColumn(table, SWT.NONE);
		tblclmnUser.setWidth(100);
		
		tblclmnMessage = new TableColumn(table, SWT.NONE);
		tblclmnMessage.setWidth(100);

		int[] mask = {100, 100, 0}; 
		new TableResizer(table, mask);
		
		composite = new Composite(this, SWT.NONE);
		GridData lData = new GridData(GridData.FILL_HORIZONTAL);
		lData.horizontalSpan = 2;
		lData.heightHint = 70;
		lData.minimumHeight = 70;
		composite.setLayoutData(lData);
		composite.setLayout(new BorderLayout());
		
		text = new Text(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		
		btnSend = new Button(composite, SWT.NONE);
		btnSend.setLayoutData(BorderLayout.EAST);
		
		btnSend.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Message msg = new Message();
				msg.text = text.getText();
				text.setText("");
				chatManager.sendMessage(msg);
			}
			
		});
		
		chatMembers = new HashMap<String, UserInfo>();
		
		localeManager.addLocalizedObject(this);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setLocaleStrings() {
		grpUsers.setText(localeManager.localize("Users"));
		btnSend.setText(localeManager.localize("Send"));
		tblclmnTime.setText(localeManager.localize("Time"));
		tblclmnUser.setText(localeManager.localize("User"));
		tblclmnMessage.setText(localeManager.localize("Message"));
		this.layout();
		composite.layout();
	}
	
	private void displayUsers() {
		System.out.println("DisplayUsers");
		getDisplay().asyncExec(new Runnable(){
			@Override
			public void run() {
				users.removeAll();
				for (UserInfo ui : chatMembers.values()) {
					users.add(ui.name);
				}
			}
		});
	}

	@Override
	public void addUser(UserInfo info) {
		System.out.println("Add user " + info.id);
		chatMembers.put(info.id, info);
		displayUsers();
	}

	@Override
	public void updateUser(UserInfo info) {
		chatMembers.put(info.id, info);
		displayUsers();		
	}

	@Override
	public void removeUser(String id) {
		chatMembers.remove(id);
		displayUsers();
	}

	@Override
	public void newMessgae(final Message message) {
		getDisplay().asyncExec(new Runnable(){
			@Override
			public void run() {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, message.time.toString());
				item.setText(1, message.fromName);
				item.setText(2, message.text);
			}
			
		});
	}
}
