package ru.kt15.finomen.neerc.hall.desktop;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ru.kt15.finomen.neerc.core.LocaleManager;
import ru.kt15.finomen.neerc.core.Localized;
import ru.kt15.finomen.neerc.hall.ChatManager;
import ru.kt15.finomen.neerc.hall.xmpp.NeercXMPPConnection;

public class MainWindow implements Localized {

	protected Shell shell;
	private TaskWindow taskTab;
	private ChatWindow chatTab;
	private PCMS2Window pcmsTab;
	private final LocaleManager localeManager;
	private CTabFolder tabFolder;
	private CTabItem tbtmTasks;
	private CTabItem tbtmChat;
	private CTabItem tbtmPCMS;
	private MenuItem mntmLanguage;
	private MenuItem mntmHelp;
	private MenuItem mntmHelp_1;
	private MenuItem mntmAbout;
	private MenuItem mntmView;
	private MenuItem mntmFullScreen;
	
	//Managers
	private final NeercXMPPConnection xmppConnection = new NeercXMPPConnection();
	
	public MainWindow(LocaleManager localeManager) {
		this.localeManager = localeManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LocaleManager lm = new LocaleManager();
			MainWindow window = new MainWindow(lm);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		xmppConnection.Stop();
	}
	
	public void setLocaleStrings() {
		shell.setText(localeManager.localize("Neerc hall console"));
		tbtmTasks.setText(localeManager.localize("Tasks"));
		tbtmChat.setText(localeManager.localize("Chat"));
		tbtmPCMS.setText(localeManager.localize("PCMS2 Monitor"));
		mntmLanguage.setText(localeManager.localize("Language"));
		mntmHelp.setText(localeManager.localize("Help"));
		mntmHelp_1.setText(localeManager.localize("Help"));
		mntmAbout.setText(localeManager.localize("About"));
		mntmView.setText(localeManager.localize("View"));
		mntmFullScreen.setText(localeManager.localize("Full screen"));
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(570, 474);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new CTabFolder(shell, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		tbtmTasks = new CTabItem(tabFolder, SWT.NONE);
				
		taskTab = new TaskWindow(localeManager, tabFolder, SWT.BORDER);
		tbtmTasks.setControl(taskTab);
		
		tbtmChat = new CTabItem(tabFolder, SWT.NONE);
		chatTab = new ChatWindow(xmppConnection, localeManager, tabFolder, SWT.BORDER);
		xmppConnection.addListener(chatTab);
		tbtmChat.setControl(chatTab);
		
		tbtmPCMS = new CTabItem(tabFolder, SWT.NONE);
		//pcmsTab = new PCMS2Window(localeManager, tabFolder, SWT.BORDER);
		//tbtmPCMS.setControl(pcmsTab);
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		mntmLanguage = new MenuItem(menu, SWT.CASCADE);	
		
		mntmView =  new MenuItem(menu, SWT.CASCADE);
		Menu menu_3 = new Menu(mntmLanguage);
		mntmView.setMenu(menu_3);
		mntmFullScreen = new MenuItem(menu_3, SWT.CHECK);
		mntmFullScreen.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setFullScreen(mntmFullScreen.getSelection());
			}		
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		Menu menu_2 = new Menu(mntmLanguage);
		mntmLanguage.setMenu(menu_2);
		
		for(final LocaleManager.Locale locale : localeManager.getLocales()) {
			MenuItem cLocale = new MenuItem(menu_2, SWT.RADIO);
			if (locale == localeManager.getCurrentLocale()) {
				cLocale.setSelection(true);
			}
			cLocale.setText(locale.getName());
			cLocale.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					//FIME: double change
					localeManager.setLocale(locale);
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
		}
		
		
		mntmHelp = new MenuItem(menu, SWT.CASCADE);
		
		Menu menu_1 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_1);
		
		mntmHelp_1 = new MenuItem(menu_1, SWT.NONE);
				
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		mntmAbout = new MenuItem(menu_1, SWT.NONE);
		
		mntmAbout.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				new About(localeManager, shell, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM).open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
				
		localeManager.addLocalizedObject(this);
		
		
		xmppConnection.Start();
	}

	@Override
	public boolean isDisposed() {
		return shell.isDisposed();
	}
}
