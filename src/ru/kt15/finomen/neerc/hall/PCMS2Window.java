package ru.kt15.finomen.neerc.hall;

import org.eclipse.swt.widgets.Composite;

import ru.kt15.finomen.neerc.core.LocaleManager;
import ru.kt15.finomen.neerc.core.Localized;
import swing2swt.layout.BorderLayout;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class PCMS2Window extends Composite implements Localized {
	private LocaleManager localeManager;
	private Table table;
	private Text teamIdFilter;
	private Text teamIdFilter_1;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PCMS2Window(LocaleManager lm, Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout());
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group grpFilter = new Group(composite, SWT.NONE);
		grpFilter.setText("Filter");
		grpFilter.setLayout(new GridLayout(2, false));
		
		Label lblTeamId = new Label(grpFilter, SWT.NONE);
		lblTeamId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTeamId.setText("Team ID");
		
		teamIdFilter = new Text(grpFilter, SWT.BORDER);
		teamIdFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblTeamName = new Label(grpFilter, SWT.NONE);
		lblTeamName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTeamName.setText("Team name");
		
		teamIdFilter_1 = new Text(grpFilter, SWT.BORDER);
		teamIdFilter_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Group grpStatus = new Group(composite, SWT.NONE);
		grpStatus.setText("Status");
		grpStatus.setLayout(new GridLayout(2, false));
		
		Label lblContest = new Label(grpStatus, SWT.NONE);
		lblContest.setText("Contest");
		
		Label lblContestname = new Label(grpStatus, SWT.NONE);
		lblContestname.setText("Contest_Name");
		
		Label lblStatus = new Label(grpStatus, SWT.NONE);
		lblStatus.setText("Status:");
		
		Label lblConteststatus = new Label(grpStatus, SWT.NONE);
		lblConteststatus.setText("Contest_Status");
		
		Label lblTime = new Label(grpStatus, SWT.NONE);
		lblTime.setText("Time:");
		
		Label lblContesttime = new Label(grpStatus, SWT.NONE);
		lblContesttime.setText("Contest_Time");
		
		Label lblLastSuccess = new Label(grpStatus, SWT.NONE);
		lblLastSuccess.setText("Last success:");
		
		Label lblContestsuccess = new Label(grpStatus, SWT.NONE);
		lblContestsuccess.setText("Contest_Success");
		
		Group grpMonitor = new Group(this, SWT.NONE);
		grpMonitor.setLayoutData(BorderLayout.CENTER);
		grpMonitor.setText("Monitor");
		grpMonitor.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(grpMonitor, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
				
		localeManager = lm;
		localeManager.addLocalizedObject(this);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setLocaleStrings() {
		// TODO Auto-generated method stub
		
	}
}
