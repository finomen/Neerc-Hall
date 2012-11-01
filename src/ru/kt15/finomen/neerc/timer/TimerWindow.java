package ru.kt15.finomen.neerc.timer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.yaml.snakeyaml.Yaml;

import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.core.SynchronizedTime;


public class TimerWindow {
	private final Shell shell;
	private long duration;
	private SynchronizedTime clock = new SynchronizedTime(0, true);
	private TimerStatus status; 
	private final Map<String, String> colors;
	private final Map<String, String> fonts;
	private final Map<String, String> backgrounds;
	private final Map<String, String> timeFmt;
	private final Map<String, Object> colorMap;
	private final TimerSocket socket;
	private final PCMS2ClientSocket clientSocket;
	
	@SuppressWarnings("unchecked")
	TimerWindow() throws IOException {
		Yaml yaml = new Yaml();
		Log.writeInfo("Loading config...");
		Map<String, Object> data = (Map<String, Object>) yaml
				.load(new FileReader(new File("timer/config.yaml")));
		Log.writeInfo("Loading color map...");
		colorMap = (Map<String, Object>) yaml.load(new FileReader(new File("timer/colors.yaml")));
		Log.writeInfo("All files loaded, starting...");
		
		
		colors = (Map<String, String>)data.get("colorScheme");
		fonts = (Map<String, String>)data.get("fontScheme");
		timeFmt = (Map<String, String>)data.get("timeFormat");
		backgrounds = (Map<String, String>)data.get("backgrounds");
				
		duration = 5 * 60 * 60 * 1000;
		status = TimerStatus.BEFORE;
		
		shell = new Shell();
			
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					if (shell.isDisposed())
						return;
					shell.getDisplay().asyncExec(new Runnable() {@Override
						public void run() {
							repaint();
						}
					});
					
					try {
						synchronized(this) {
							wait(500);
						}
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}).start();
		
		if (data.get("network-type").equals("client")) {
			socket = null;
			clientSocket = new PCMS2ClientSocket(this, (Map<String, Object>)data.get("network"));
		} else if (data.get("network-type").equals("udp")) {
			socket = new TimerSocket(this, (Map<String, Object>)data.get("network"));
			clientSocket = null;
		} else {
			socket = null;
			clientSocket = null;
			Log.writeError("Invalid network-type " + (String)data.get("network-type"));
			shell.close();
		}
			
	}
	
	private long strToTime(String s) {
		long res = 0;
		
		String[] parts = s.split(":");
		for (String part : parts) {
			res *= 60;
			res += Integer.parseInt(part);
		}
		
		
		
		return res;
	}
	
	public void Sync(final TimerStatus Status, final long Duration, final long Remaining) {
		shell.getDisplay().asyncExec(new Runnable() {@Override
			public void run() {
				duration = Duration;
				clock.sync(duration - Remaining);
				status = Status;
							
				if (status != TimerStatus.RUNNING) {
					clock.freeze();
				} else {
					clock.resume();
				}				
			}
		});
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TimerWindow window = new TimerWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getNearest(Map<String, String> map, long cTime, String def) {
		String result = def;
		for (String key : map.keySet()) {
			if (strToTime(key) < cTime) {
				break;
			}
			
			result = map.get(key);
		}
		
		return result;
	}
	
	private String currentTimeFmt = null;
	private String currentBackground = null;
	private String currentColor = null;
	private String currentFont = null;
	private Color fontColor = null;
	private int textLeft = 0;
	private int textTop = 0;
	private Font font = null;
	private String lastTimeString = "";
	
	private String formatTime(long cTime, String timeFmt) {
		String[] tfParts = timeFmt.split(":");
		String[] tfMask = {"", "%d", "%02d"};
		String text = "";
		String delim = "";
		
		long hours = cTime / 3600;
		cTime %= 3600;
		long minutes = cTime / 60;
		cTime %= 60;
		long seconds = cTime;
		
		for (String part : tfParts) {
			long val = 0;
			switch(part.charAt(0)) {
			case 'H':
				val = hours;
				break;
			case 'M':
				val = minutes;
				break;
			case 'S':
				val = seconds;
				break;
			}
			
			text = text + delim + String.format(tfMask[part.length()], val);
			delim = ":";
		}
		
		return text;
	}

	private void repaint() {
		long cTime = clock.get() / 1000;
		String timeFmt = getNearest(this.timeFmt, cTime, "H:MM:SS");		
		String background = getNearest(this.backgrounds, cTime, "resources/background.jpg");
		String color = getNearest(this.colors, cTime, "Salmon");
		String font = getNearest(this.fonts, cTime, "Calibri");
		
		boolean layoutValid = true;
		layoutValid &= timeFmt.equals(currentTimeFmt);
		layoutValid &= background.equals(currentBackground);
		layoutValid &= color.equals(currentColor);
		layoutValid &= font.equals(currentFont);
		
		if (!layoutValid) {
			rearrange();
		} else {
			String newTimeString = formatTime(cTime, currentTimeFmt);
			if (newTimeString.length() != lastTimeString.length()) {
				rearrange();
			}
			
			lastTimeString = newTimeString;
		}
		
		
		
		
		int width = shell.getBounds().width;
		int height = shell.getBounds().height;
		Image back = new Image(shell.getDisplay(), background);
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(back, 0, 0, back.getBounds().width,
				back.getBounds().height, 0, 0, width, height);
		gc.setTextAntialias(SWT.ON);
		gc.setForeground(fontColor);
		gc.setFont(this.font);
		gc.drawText(lastTimeString, textLeft, textTop,
				SWT.DRAW_TRANSPARENT);
		gc.dispose();
		back.dispose(); // don't forget about me!
		shell.setBackgroundImage(scaled);
		
	}
	
	private void rearrange() {
		long cTime = clock.get() / 1000;
		String timeFmt = currentTimeFmt = getNearest(this.timeFmt, cTime, "H:MM:SS");		
		currentBackground = getNearest(this.backgrounds, cTime, "resources/background.jpg");
		String color = currentColor = getNearest(this.colors, cTime, "Salmon");
		String font = currentFont = getNearest(this.fonts, cTime, "Calibri");

		Object[] colorDef = null;
		if (colorMap.containsKey(color))
			colorDef = ((ArrayList<?>) colorMap.get(color)).toArray();
		else {
			colorDef = new Object[3];
			colorDef[0] = Integer.parseInt(color.substring(0, 2), 16);
			colorDef[1] = Integer.parseInt(color.substring(2, 4), 16);
			colorDef[2] = Integer.parseInt(color.substring(4, 6), 16);
		}
		
		fontColor = new Color(shell.getDisplay(), (Integer)colorDef[0], (Integer)colorDef[1], (Integer)colorDef[2]);
				
		int width = shell.getBounds().width;
		int height = shell.getBounds().height;

		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);

		int fontSize = height;
		int step = height / 2;
		int widthOverflow = 0;
		int heightOverflow = 0;
		width -= 30;
		height -= 30;
		
		String timeStr = formatTime(cTime, timeFmt);
		
		Log.writeDebug("Rearrange on " + timeStr);

		do {
			shell.getDisplay().loadFont("resources/" + font + ".ttf");
			Font cfont = new Font(shell.getDisplay(), font, fontSize,
					SWT.BOLD);
			gc.setFont(cfont);
			this.font = cfont;
			Point p = gc.textExtent(timeStr);
			widthOverflow = width - p.x;
			heightOverflow = height - p.y;
			
			if (widthOverflow > 0 && heightOverflow > 0)
				fontSize += step;
			else if (widthOverflow < 0 || heightOverflow < 0)
				fontSize -= step;
			else
				break;
			step /= 2;
		} while (step > 0);

		Point p = gc.textExtent(timeStr);
		
		Log.writeDebug("Text width: " + p.x + " of " + shell.getBounds().width);
		Log.writeDebug("Text height: " + p.y + " of " + shell.getBounds().height);
		
		textLeft = (shell.getBounds().width - p.x) / 2;
		textTop = (shell.getBounds().height - p.y) / 2 + 30;
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		shell.setFullScreen(true);
		repaint();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		socket.stop();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell.setText("Neerc timer");

	}
}
