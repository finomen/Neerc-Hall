package ru.kt15.finomen.neerc;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.TabFolder;

public class MainWindow {

	protected Shell shell;
	private TaskWindow taskTab;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
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
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(570, 474);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		CTabFolder tabFolder = new CTabFolder(shell, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmTasks = new CTabItem(tabFolder, SWT.NONE);
		tbtmTasks.setText("Tasks");
		
		taskTab = new TaskWindow(tabFolder, SWT.BORDER);
		tbtmTasks.setControl(taskTab);

	}
}
