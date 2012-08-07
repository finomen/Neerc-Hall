package ru.kt15.finomen.neerc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

class TableResizer implements Listener {
	private final Table table;
	private final int[] mask;

	TableResizer(Table table, int[] mask) {
		this.table = table;
		this.mask = mask;
		table.addListener(SWT.Resize, this);
		table.addListener(SWT.CHANGED, this);
	}

	@Override
	synchronized public void handleEvent(Event arg0) {
			Rectangle rect = table.getClientArea ();
			int sum = 0;
			int count = 0;
			int realMask[] = new int[table.getColumnCount()];
			
			for (int i = 0; i < Math.min(mask.length, table.getColumnCount()); ++i) {
				realMask[i] = mask[i];
			}
			
			if (mask.length < table.getColumnCount()) {
				for (int i = mask.length; i < table.getColumnCount(); ++i) {
					realMask[i] = mask[mask.length - 1];
				}
			}
			
			for (int i = 0; i < realMask.length; ++i) {
				if (realMask[i] == 0) {
					count++;
					//TODO: this content width calculation is ugly
					int cWidth = table.getColumns()[table.getColumnOrder()[i]].getText().length() * 5 + 10;
					for (TableItem row : table.getItems()) {
						cWidth = Math.max(cWidth,  row.getText(i).length() * 5 + 10);
						
					}
					
					sum += cWidth;
					realMask[i] = -cWidth;
				} else {
					sum += realMask[i];
				}
			}
			
			for (int i = 0; i < realMask.length; ++i) {
				if (realMask[i] <= 0) {
					table.getColumn(i).setWidth(-realMask[i] + ((rect.width > sum) ? (rect.width - sum) / count : 0));
				} else {
					table.getColumn(i).setWidth(realMask[i]);
				}
			}
			
	}
	
}