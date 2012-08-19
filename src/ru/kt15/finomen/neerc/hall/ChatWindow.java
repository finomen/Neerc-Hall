package ru.kt15.finomen.neerc.hall;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ru.kt15.finomen.neerc.core.LocaleManager;
import ru.kt15.finomen.neerc.core.Localized;
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
	/**
	 * Create the composite.
	 * @param localeManager 
	 * @param parent
	 * @param style
	 */
	public ChatWindow(LocaleManager localeManager, Composite parent, int style) {
		super(parent, style);
		this.localeManager = localeManager;
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		setLayout(gridLayout);	
		
		grpUsers = new Group(this, SWT.NONE);
		grpUsers.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		grpUsers.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		users = new List(grpUsers, SWT.BORDER);
		users.setItems(new String[] {"user1", "user2", "suer23123123"});
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
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

	@Override
	public void addUser(UserInfo info) {
		//TODO:
	}

	@Override
	public void updateUser(UserInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUser(String id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void newMessgae(Message message) {
		TableItem item = new TableItem(table, SWT.None);
		item.setText(0, message.time.toString());
		item.setText(1, message.fromName);
		item.setText(2, message.text);
	}
}
