package ru.kt15.finomen.neerc.hall;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;

public class About extends Dialog implements Localized {

	protected Object result;
	protected Shell shlAbout;
	private LocaleManager localeManager;
	private Label lblVersion;
	private Label lbla;
	private Label lblAuthor;
	private Label lblNikolayFilchenko;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public About(LocaleManager localeManager, Shell parent, int style) {
		super(parent, style);
		this.localeManager = localeManager;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlAbout.open();
		shlAbout.layout();
		Display display = getParent().getDisplay();
		while (!shlAbout.isDisposed()) {
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
		shlAbout = new Shell(getParent(), getStyle());
		shlAbout.setSize(154, 73);
		shlAbout.setLayout(new GridLayout(2, false));
		
		lblVersion = new Label(shlAbout, SWT.NONE);
		
		lbla = new Label(shlAbout, SWT.NONE);
		
		lblAuthor = new Label(shlAbout, SWT.NONE);
		
		lblNikolayFilchenko = new Label(shlAbout, SWT.NONE);
		
		localeManager.addLocalizedObject(this);
	}

	@Override
	public void setLocaleStrings() {
		shlAbout.setText(localeManager.localize("About"));
		lblVersion.setText(localeManager.localize("Version"));
		lbla.setText(localeManager.localize("0.0a"));
		lblAuthor.setText(localeManager.localize("Author"));
		lblNikolayFilchenko.setText(localeManager.localize("Nikolay Filchenko"));
	}

	@Override
	public boolean isDisposed() {
		// TODO Auto-generated method stub
		return false;
	}

}
