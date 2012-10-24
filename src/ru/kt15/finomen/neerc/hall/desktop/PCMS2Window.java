package ru.kt15.finomen.neerc.hall.desktop;

import java.net.MalformedURLException;

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

import com.caucho.hessian.client.HessianProxyFactory;

import pcms2.services.client.AuthorizationFailedException;
import pcms2.services.client.ClientStandingsService;
import pcms2.services.client.LoginDataService;
import pcms2.services.client.TransportStandings;
import pcms2.services.scoring.Standings;

public class PCMS2Window extends Composite implements Localized {
	private LocaleManager localeManager;
	private Table table;
	private Text teamIdFilter;
	private Text teamIdFilter_1;
	private Label lblContest;
	private Label lblContestname;
	private Label lblStatus;
	private Label lblConteststatus;
	private Label lblTime;
	private Label lblContesttime;
	private Label lblLastSuccess;
	private Label lblContestsuccess;
	private Group grpFilter;
	private Label lblTeamId;
	private Label lblTeamName;
	private Group grpStatus;
	private Group grpMonitor;
	private Thread worker;
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
		
		grpFilter = new Group(composite, SWT.NONE);
		grpFilter.setLayout(new GridLayout(2, false));
		
		lblTeamId = new Label(grpFilter, SWT.NONE);
		lblTeamId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		teamIdFilter = new Text(grpFilter, SWT.BORDER);
		teamIdFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblTeamName = new Label(grpFilter, SWT.NONE);
		lblTeamName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		teamIdFilter_1 = new Text(grpFilter, SWT.BORDER);
		teamIdFilter_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		grpStatus = new Group(composite, SWT.NONE);
		grpStatus.setLayout(new GridLayout(2, false));
		
		lblContest = new Label(grpStatus, SWT.NONE);
		lblContestname = new Label(grpStatus, SWT.NONE);
		lblStatus = new Label(grpStatus, SWT.NONE);
		lblConteststatus = new Label(grpStatus, SWT.NONE);
		lblTime = new Label(grpStatus, SWT.NONE);
		lblContesttime = new Label(grpStatus, SWT.NONE);
		lblLastSuccess = new Label(grpStatus, SWT.NONE);
		lblContestsuccess = new Label(grpStatus, SWT.NONE);
		
		grpMonitor = new Group(this, SWT.NONE);
		grpMonitor.setLayoutData(BorderLayout.CENTER);
		grpMonitor.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(grpMonitor, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		int[] mask = {20, 200, 0};
		new TableResizer(table, mask);
				
		localeManager = lm;
		localeManager.addLocalizedObject(this);		
		
		worker = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					HessianProxyFactory factory = new HessianProxyFactory();
					factory.setConnectTimeout(2000);
					ClientStandingsService standingsService = 
							(ClientStandingsService) factory.create(ClientStandingsService.class, "http://127.0.0.1:8080/pcms/party");
					
					TransportStandings standings = standingsService.getStandings("guest", "guest");
					
					final String lastSuccess = standings.lastAcceptedParty + " " +
							standings.lastAcceptedProblem + " " +
							standings.lastAcceptedTime;
					
					final String contestName = standings.contestName;
					long time = standings.contestLength - standings.timePassed;
					
					final String timeStr = 
							String.format("%d:%02d:%02d", 
									time / 3600,
									(time / 60) % 60,
									time % 60);
					
					getShell().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							lblContestsuccess.setText(lastSuccess);
							lblContestname.setText(contestName);
							lblContesttime.setText(timeStr);
						}
					});
					
					
					
				} catch (MalformedURLException | AuthorizationFailedException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		worker.start();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setLocaleStrings() {
		grpFilter.setText(localeManager.localize("Filter"));
		lblTeamId.setText(localeManager.localize("Team ID"));
		lblTeamName.setText(localeManager.localize("Team name"));
		grpStatus.setText(localeManager.localize("Status"));
		lblContest.setText(localeManager.localize("Contest"));
		lblContestname.setText(localeManager.localize("Contest_Name"));
		lblStatus.setText(localeManager.localize("Status:"));
		lblConteststatus.setText(localeManager.localize("Contest_Status"));
		lblTime.setText(localeManager.localize("Time:"));
		lblContesttime.setText(localeManager.localize("Contest_Time"));
		lblLastSuccess.setText(localeManager.localize("Last success:"));
		lblContestsuccess.setText(localeManager.localize("Contest_Success"));
		grpMonitor.setText(localeManager.localize("Monitor"));
	}
}
