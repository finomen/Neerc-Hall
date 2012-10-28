package ru.kt15.finomen.neerc.hall.desktop;

import java.text.DateFormat;
import java.util.Date;

import javax.swing.text.DateFormatter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;

import ru.kt15.finomen.neerc.core.ILog;
import ru.kt15.finomen.neerc.core.LocaleManager;
import ru.kt15.finomen.neerc.core.Localized;
import ru.kt15.finomen.neerc.core.Log;

public class LogWindow extends Composite implements Localized, ILog {
	private Table table;
	private TableColumn tblclmnTime;
	private TableColumn tblclmnLevel;
	private TableColumn tblclmnMessage;
	private LocaleManager localeManager;

	/*
	 * Create the composite.
	 * 
	 * @param parent
	 * 
	 * @param style
	 */
	public LogWindow(LocaleManager localeManager, Composite parent, int style) {
		super(parent, style);
		this.localeManager = localeManager;
		setLayout(new FillLayout(SWT.HORIZONTAL));

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		

		tblclmnTime = new TableColumn(table, SWT.NONE);
		tblclmnTime.setWidth(100);

		tblclmnLevel = new TableColumn(table, SWT.NONE);
		tblclmnLevel.setWidth(100);

		tblclmnMessage = new TableColumn(table, SWT.NONE);
		tblclmnMessage.setWidth(100);
		
		int[] mask = {0};
		new TableResizer(table, mask);

		localeManager.addLocalizedObject(this);

		//Log.setImpl(this);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setLocaleStrings() {
		tblclmnTime.setText(localeManager.localize("Time"));
		tblclmnLevel.setText(localeManager.localize("Level"));
		tblclmnMessage.setText(localeManager.localize("Message"));
	}

	@Override
	public void writeError(final Date time, final String s) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TableItem it = new TableItem(table, SWT.NONE);
				String[] txt = new String[3];
				txt[0] = DateFormat.getInstance().format(time);
				txt[1] = "ERROR";
				txt[2] = s;
				it.setText(txt);
				it.setBackground(new Color(getDisplay(), 255, 0, 0));
				table.showItem(it);
			}
		});
	}

	@Override
	public void writeInfo(final Date time, final String s) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TableItem it = new TableItem(table, SWT.NONE);
				String[] txt = new String[3];
				txt[0] = DateFormat.getInstance().format(time);
				txt[1] = "INFO";
				txt[2] = s;
				it.setText(txt);
				table.showItem(it);
			}
		});
	}

	@Override
	public void writeDebug(final Date time, final String s) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TableItem it = new TableItem(table, SWT.NONE);
				String[] txt = new String[3];
				txt[0] = DateFormat.getInstance().format(time);
				txt[1] = "DEBUG";
				txt[2] = s;
				it.setText(txt);
				it.setForeground(new Color(getDisplay(), 125, 125, 125));
				table.showItem(it);
			}
		});
	}
}
