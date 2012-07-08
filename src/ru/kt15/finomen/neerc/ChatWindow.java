package ru.kt15.finomen.neerc;

import org.eclipse.swt.widgets.Composite;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ChatWindow extends Composite implements Localized {
	private Text text;
	private Table table;
	private LocaleManager localeManager;
	private Group grpUsers;
	private Button btnSend;
	private TableColumn tblclmnTime;
	private TableColumn tblclmnUser;
	private TableColumn tblclmnMessage;

	/**
	 * Create the composite.
	 * @param localeManager 
	 * @param parent
	 * @param style
	 */
	public ChatWindow(LocaleManager localeManager, Composite parent, int style) {
		super(parent, style);
		this.localeManager = localeManager;
		setLayout(new BorderLayout(0, 0));
		
		grpUsers = new Group(this, SWT.NONE);
		grpUsers.setLayoutData(BorderLayout.WEST);
		grpUsers.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		List list = new List(grpUsers, SWT.BORDER);
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new BorderLayout(0, 0));
		
		text = new Text(composite, SWT.BORDER | SWT.MULTI);
		btnSend = new Button(composite, SWT.NONE);
		btnSend.setLayoutData(BorderLayout.EAST);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(BorderLayout.CENTER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnTime = new TableColumn(table, SWT.NONE);
		tblclmnTime.setWidth(100);
				
		tblclmnUser = new TableColumn(table, SWT.NONE);
		tblclmnUser.setWidth(100);
		
		tblclmnMessage = new TableColumn(table, SWT.NONE);
		tblclmnMessage.setWidth(100);
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
	}
}
