package ru.kt15.finomen.neerc.hall.desktop;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.List;

import ru.kt15.finomen.neerc.core.LocaleManager;
import ru.kt15.finomen.neerc.core.Localized;
import ru.kt15.finomen.neerc.hall.Task;
import ru.kt15.finomen.neerc.hall.Task.TaskPerformer;
import ru.kt15.finomen.neerc.hall.Task.TaskType;
import ru.kt15.finomen.neerc.hall.TaskManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class NewTask extends Dialog implements Localized {

	protected Object result;
	protected Shell shlCreateTask;
	private Text text;
	private LocaleManager localeManager;
	private Button btnRadioButton_1;
	private Button btnRadioButton_2;
	private Button btnRadioButton_3;
	private Button btnAdd;
	private Label lblText;
	private Group grpType;
	private Button btnRadioButton;
	private Group grpPerformers;
	private TaskManager taskManager;

	/**
	 * Create the dialog.
	 * @param localeManager 
	 * @param parent
	 * @param style
	 */
	public NewTask(LocaleManager localeManager, TaskManager taskManager, Shell parent, int style) {
		super(parent, style);
		this.localeManager = localeManager;
		this.taskManager = taskManager;
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlCreateTask.open();
		shlCreateTask.layout();
		Display display = getParent().getDisplay();
		while (!shlCreateTask.isDisposed()) {
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
		shlCreateTask = new Shell(getParent(), getStyle());
		shlCreateTask.setSize(450, 171);
		shlCreateTask.setLayout(new GridLayout(3, false));
		
		grpType = new Group(shlCreateTask, SWT.NONE);
		grpType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		grpType.setLayout(new GridLayout(1, false));
		
		btnRadioButton = new Button(grpType, SWT.RADIO);
		btnRadioButton.setSelection(true);	
		btnRadioButton_1 = new Button(grpType, SWT.RADIO);
		btnRadioButton_2 = new Button(grpType, SWT.RADIO);
		btnRadioButton_3 = new Button(grpType, SWT.RADIO);
		
		grpPerformers = new Group(shlCreateTask, SWT.NONE);
		grpPerformers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpPerformers.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		final List list = new List(grpPerformers, SWT.MULTI);
		
		btnAdd = new Button(shlCreateTask, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String message = text.getText();
				Task.TaskType type = TaskType.EXTENDED;
				
				if (btnRadioButton.getSelection()) {
					type = TaskType.TODO;
				} else if (btnRadioButton_1.getSelection()) {
					type = TaskType.CONFIRM;
				} else if (btnRadioButton_2.getSelection()) {
					type = TaskType.QUESTION;
				} else if (btnRadioButton_3.getSelection()) {
					type = TaskType.OKFAIL;
				}
				
				TaskPerformer[] performers = new TaskPerformer[list.getSelectionCount()];
				int i = 0;
				for (String name : list.getSelection()) {
					performers[i++] = new TaskPerformer(name);
				}
				
				Task task = new Task(taskManager, message, performers, type);
				taskManager.newTask(task);
				shlCreateTask.close();
				
			}
		});
		
		lblText = new Label(shlCreateTask, SWT.NONE);
		lblText.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		text = new Text(shlCreateTask, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		localeManager.addLocalizedObject(this);
		
		for (TaskPerformer p : taskManager.getPerformers()) {
			list.add(p.getName());
		}
	}

	@Override
	public void setLocaleStrings() {
		shlCreateTask.setText(localeManager.localize("Create task"));
		grpType.setText(localeManager.localize("Type"));
		btnRadioButton.setText(localeManager.localize("TODO"));
		btnRadioButton_1.setText(localeManager.localize("Confirm"));
		btnRadioButton_2.setText(localeManager.localize("Question"));
		btnRadioButton_3.setText(localeManager.localize("OKFAIL"));
		grpPerformers.setText(localeManager.localize("Performers"));
		btnAdd.setText(localeManager.localize("Add"));
		lblText.setText(localeManager.localize("Text:"));
	}

	@Override
	public boolean isDisposed() {
		// TODO Auto-generated method stub
		return false;
	}
}
