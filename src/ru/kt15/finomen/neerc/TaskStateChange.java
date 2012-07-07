package ru.kt15.finomen.neerc;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class TaskStateChange extends Dialog {

	protected Object result;
	protected Shell shell;
	private Task task;
	private Text text;
	private boolean assignedEnabled, inProgressEnabled, doneEnabled, failedEnabled;
	private Task.TaskState.StateId state;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TaskStateChange(Task task, Shell parent, int style) {
		super(parent, style);
		this.task = task;
		setText("SWT Dialog");
		assignedEnabled = inProgressEnabled = doneEnabled = failedEnabled = false;
		for (Task.TaskState.StateId sid : task.getPossibleStates()) {
			switch(sid) {
			case ASSIGNED:
				assignedEnabled = true;
				break;
			case DONE:
				doneEnabled = true;
				break;
			case IN_PROGRESS:
				inProgressEnabled = true;
				break;
			case FAILED:
				failedEnabled = true;
				break;
			case UPDATING:
				// This state can be set only by manager
				break;
			}
		} 
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(352, 146);
		shell.setText(getText());
		shell.setLayout(new GridLayout(2, false));
		
		
		
		Group grpState = new Group(shell, SWT.NONE);
		grpState.setText("State");
		grpState.setLayout(new FillLayout(SWT.VERTICAL));
		
		final Button btnAssigned = new Button(grpState, SWT.RADIO);
		btnAssigned.setText("Assigned");
		
		final Button btnInProgress = new Button(grpState, SWT.RADIO);
		btnInProgress.setText("In progress");
		
		final Button btnDone = new Button(grpState, SWT.RADIO);
		btnDone.setText("Done");
		
		final Button btnFailed = new Button(grpState, SWT.RADIO);
		btnFailed.setText("Failed");
		
		Group grpMessageoptional = new Group(shell, SWT.NONE);
		GridData gd_grpMessageoptional = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_grpMessageoptional.widthHint = 244;
		grpMessageoptional.setLayoutData(gd_grpMessageoptional);
		grpMessageoptional.setText("Message (optional)");
		grpMessageoptional.setLayout(new FillLayout(SWT.HORIZONTAL));
			
		text = new Text(grpMessageoptional, SWT.BORDER | SWT.MULTI);
		new Label(shell, SWT.NONE);
		
		Button btnSave = new Button(shell, SWT.NONE);
		btnSave.setText("Save");
		
		btnAssigned.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				text.setEnabled(false);
			}
		});
		
		SelectionAdapter adapt = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				text.setEnabled(true);
			}
		};
		
		btnDone.addSelectionListener(adapt);
		btnFailed.addSelectionListener(adapt);
		btnInProgress.addSelectionListener(adapt);
		
		btnAssigned.setEnabled(assignedEnabled);
		btnDone.setEnabled(doneEnabled);
		btnInProgress.setEnabled(inProgressEnabled);
		btnFailed.setEnabled(failedEnabled);
		
		state = task.getState().getId();
		text.setText(task.getState().getMessage());
		
		switch(state) {
		case ASSIGNED:
			btnAssigned.setSelection(true);
			break;
		case DONE:
			btnDone.setSelection(true);
			break;
		case IN_PROGRESS:
			btnInProgress.setSelection(true);
			break;
		case FAILED:
			btnFailed.setSelection(true);
			break;
		case UPDATING:
			break;
		}
		
		btnFailed.setSelection(true);
		
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (btnAssigned.getSelection()) {
					task.changeState(Task.TaskState.assigned());
				} else if (btnInProgress.getSelection()) {
					task.changeState(Task.TaskState.inProgress(text.getText()));
				} else if (btnDone.getSelection()) {
					task.changeState(Task.TaskState.done(text.getText()));
				} else if (btnFailed.getSelection()) {
					task.changeState(Task.TaskState.failed(text.getText()));
				}
			}
		});
		
		
		shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true));
	}
}
