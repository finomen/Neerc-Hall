package ru.kt15.finomen.neerc.hall.desktop;

import java.util.Map;

import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;

import ru.kt15.finomen.neerc.core.LocaleManager;
import ru.kt15.finomen.neerc.core.Localized;
import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.core.SettingsManager;
import swing2swt.layout.BorderLayout;

public class SettingsDialog extends Dialog implements Localized {

	protected Object result;
	protected Shell shell;
	private LocaleManager localeManager;
	private TreeColumn trclmnName;
	private TreeColumn trclmnValue;
	private Tree tree;
	private TreeEditor editor;

	/**
	 * Create the dialog.
	 * 
	 * @param localeManager
	 * @param parent
	 * @param style
	 */
	public SettingsDialog(LocaleManager localeManager, Shell parent, int style) {
		super(parent, style);
		this.localeManager = localeManager;
	}

	/**
	 * Open the dialog.
	 * 
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
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new BorderLayout(0, 0));

		tree = new Tree(shell, SWT.BORDER);
		tree.setHeaderVisible(true);

		trclmnName = new TreeColumn(tree, SWT.NONE);
		trclmnName.setWidth(100);

		trclmnValue = new TreeColumn(tree, SWT.NONE);
		trclmnValue.setWidth(100);

		for (Map.Entry<String, Object> entry : SettingsManager.instance()
				.getTree().entrySet()) {
			String name = entry.getKey();
			TreeItem it = new TreeItem(tree, SWT.NONE);
			Object value = entry.getValue();

			if (value instanceof Map<?, ?>) {
				displaySubtree(it, name, (Map<String, Object>) value, name);
			} else if (value instanceof String) {
				displaySubtree(it, name, (String) value, name);
			} else if (value instanceof Integer) {
				displaySubtree(it, name, (Integer) value, name);
			} else if (value instanceof Boolean) {
				displaySubtree(it, name, (Boolean) value, name);
			}
		}

		editor = new TreeEditor(tree);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Add a key listener to the tree that listens for F2.
		// If F2 is pressed, we do the editing
		tree.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {

				if (event.keyCode == SWT.F2 && tree.getSelectionCount() == 1) {
					// Determine the item to edit
					final TreeItem item = tree.getSelection()[0];
					item.notifyListeners(SWT.Selection, null);
				}
			}
		});

		localeManager.addLocalizedObject(this);
	}

	private void displaySubtree(final TreeItem it, String name,
			final Boolean value, final String path) {
		// TODO Auto-generated method stub
		final String[] txt = new String[2];
		txt[0] = name;
		txt[1] = value ? localeManager.localize("yes") : localeManager
				.localize("no");
		it.setText(txt);

		it.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				final Combo editWidget = new Combo(tree, SWT.NONE);
				editWidget.add(localeManager.localize("yes"));
				editWidget.add(localeManager.localize("no"));
				editWidget.select(SettingsManager.instance().get(path, value) ? 0 : 1);

				editWidget.setFocus();
				editWidget.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent event) {
						SettingsManager.instance().set(path,
								editWidget.getSelectionIndex() == 0);
						txt[1] = SettingsManager.instance().get(path, value) ? localeManager
								.localize("yes") : localeManager.localize("no");
						it.setText(txt);
						editWidget.dispose();
					}
				});

				editWidget.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent event) {
						switch (event.keyCode) {
						case SWT.CR:
							SettingsManager.instance().set(path,
									editWidget.getSelectionIndex() == 0);
							txt[1] = SettingsManager.instance().get(path, value) ? localeManager
									.localize("yes") : localeManager.localize("no");
							it.setText(txt);
						case SWT.ESC:
							editWidget.dispose();
							break;
						}
					}
				});

				editor.setEditor(editWidget, it, 1);
			}
		});

	}

	private void displaySubtree(final TreeItem it, String name, final Integer value,
			final String path) {
		// TODO Auto-generated method stub
		final String[] txt = new String[2];
		txt[0] = name;
		txt[1] = value.toString();
		it.setText(txt);
		
		it.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				final Spinner editWidget = new Spinner(tree, SWT.NONE);
				editWidget.setMinimum(Integer.MIN_VALUE);
				editWidget.setMaximum(Integer.MAX_VALUE);
				editWidget.setSelection(SettingsManager.instance().get(path, value));

				editWidget.setFocus();
				editWidget.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent event) {
						SettingsManager.instance().set(path,
								editWidget.getSelection());
						txt[1] = SettingsManager.instance().get(path, value).toString();
						it.setText(txt);
						editWidget.dispose();
					}
				});

				editWidget.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent event) {
						switch (event.keyCode) {
						case SWT.CR:
							SettingsManager.instance().set(path,
									editWidget.getSelection());
							txt[1] = SettingsManager.instance().get(path, value).toString();
							it.setText(txt);
						case SWT.ESC:
							editWidget.dispose();
							break;
						}
					}
				});

				editor.setEditor(editWidget, it, 1);
			}
		});
	}

	private void displaySubtree(final TreeItem it, String name, final String value,
			final String path) {
		final String[] txt = new String[2];
		txt[0] = name;
		txt[1] = value.toString();
		it.setText(txt);
		
		it.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				final Text editWidget = new Text(tree, SWT.NONE);
				editWidget.setText(SettingsManager.instance().get(path, value));

				editWidget.setFocus();
				editWidget.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent event) {
						SettingsManager.instance().set(path,
								editWidget.getText());
						txt[1] = SettingsManager.instance().get(path, value);
						it.setText(txt);
						editWidget.dispose();
					}
				});

				editWidget.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent event) {
						switch (event.keyCode) {
						case SWT.CR:
							SettingsManager.instance().set(path,
									editWidget.getText());
							txt[1] = SettingsManager.instance().get(path, value);
							it.setText(txt);
						case SWT.ESC:
							editWidget.dispose();
							break;
						}
					}
				});

				editor.setEditor(editWidget, it, 1);
			}
		});
	}

	private void displaySubtree(TreeItem item, String treeName,
			Map<String, Object> subtree, String path) {

		item.setText(treeName);

		for (Map.Entry<String, Object> entry : subtree.entrySet()) {
			String name = entry.getKey();
			TreeItem it = new TreeItem(item, SWT.NONE);
			Object value = entry.getValue();

			if (value instanceof Map<?, ?>) {
				displaySubtree(it, name, (Map<String, Object>) value, path
						+ "." + name);
			} else if (value instanceof String) {
				displaySubtree(it, name, (String) value, path + "." + name);
			} else if (value instanceof Integer) {
				displaySubtree(it, name, (Integer) value, path + "." + name);
			} else if (value instanceof Boolean) {
				displaySubtree(it, name, (Boolean) value, path + "." + name);
			}
		}
	}

	@Override
	public void setLocaleStrings() {
		shell.setText(localeManager.localize("Settings"));
		trclmnName.setText(localeManager.localize("Name"));
		trclmnValue.setText(localeManager.localize("Value"));
	}

	@Override
	public boolean isDisposed() {
		// TODO Auto-generated method stub
		return false;
	}
}
