package ru.kt15.finomen.neerc.timer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
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
import ru.kt15.finomen.neerc.core.net.Endpoint;
import ru.kt15.finomen.neerc.core.net.TCPServerConnection;
import ru.kt15.finomen.neerc.core.net.proto.PCMS;
import ru.kt15.finomen.neerc.core.net.proto.PCMS.TimerStatus;

public class TimerWindow {
	private final Shell shell;
	private final TCPServerConnection connection;
	private long duration;
	private long remaining;
	private TimerStatus status;
	private boolean frozen;
	private final SynchronizedTime time;
	private final Map<String, String> colors;
	private final Map<String, String> backgrounds;
	private final Map<String, String> timeFmt;
	private final Map<String, Object> colorMap;
	
	@SuppressWarnings("unchecked")
	TimerWindow() throws FileNotFoundException {
		Yaml yaml = new Yaml();
		Log.writeInfo("Loading config...");
		Map<String, Object> data = (Map<String, Object>) yaml
				.load(new FileReader(new File("timer/config.yaml")));
		Map<String, Object> network = (Map<String, Object>) data.get("network");
		Log.writeInfo("Loading color map...");
		colorMap = (Map<String, Object>) yaml.load(new FileReader(new File("timer/colors.yaml")));
		Log.writeInfo("All files loaded, starting...");
		
		connection = new TCPServerConnection(
				(String) network.get("ServerHost"),
				(Integer) network.get("ServerPort"));
		
		colors = (Map<String, String>)data.get("colorScheme");
		timeFmt = (Map<String, String>)data.get("timeFormat");
		backgrounds = (Map<String, String>)data.get("timeFormat");
		
		duration = 5 * 60 * 60 * 1000;
		remaining = duration;
		status = PCMS.TimerStatus.BEFORE;
		frozen = false;
		time = new SynchronizedTime(remaining, false);//true);
		
		shell = new Shell();
		
		connection.AddHandler(this);
		connection.sendTo(
				new Endpoint("PCMS"),
				PCMS.RegisterListener.newBuilder().setInterval(500)
						.addEvents(PCMS.EventType.TIMER_UPDATE).build());
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					if (shell.isDisposed())
						return;
					shell.getDisplay().asyncExec(new Runnable() {@Override
						public void run() {
								updateBackground();
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

	public void HandlePacket(final PCMS.TimerUpdate upd) {
		shell.getDisplay().asyncExec(new Runnable() {@Override
		public void run() {
			duration = upd.getContestDuration();
			remaining = upd.getRemainingTime(); //TODO: smart clock
			status = upd.getStatus();
						
			if (status != TimerStatus.RUNNING) {
				time.freeze();
			} else {
				time.resume();
			}
			
			time.sync(remaining);
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

	public void updateBackground() {
		long cTime = time.get() / 1000;
		String timeFmt = "H:MM:SS";
		
		for (String key : this.timeFmt.keySet()) {
			if (strToTime(key) < cTime) {
				break;
			}
			
			timeFmt = this.timeFmt.get(key);
		}
		
		String background = "resources/background.jpg";
		for (String key : this.backgrounds.keySet()) {
			if (strToTime(key) < cTime) {
				break;
			}
			
			background = this.backgrounds.get(key);
		}

		String color = "Salmon";
		for (String key : this.colors.keySet()) {
			if (strToTime(key) < cTime) {
				break;
			}
			
			color = this.colors.get(key);
		}

		Object[] colorDef = null;
		if (colorMap.containsKey(color))
			colorDef = ((ArrayList<?>) colorMap.get(color)).toArray();
		else {
			colorDef = new Object[3];
			colorDef[0] = Integer.parseInt(color.substring(0, 2), 16);
			colorDef[1] = Integer.parseInt(color.substring(2, 4), 16);
			colorDef[2] = Integer.parseInt(color.substring(4, 6), 16);
		}
		
		Color fontColor = new Color(shell.getDisplay(), (Integer)colorDef[0], (Integer)colorDef[1], (Integer)colorDef[2]);
				
		long hours = cTime / 3600;
		cTime %= 3600;
		long minutes = cTime / 60;
		cTime %= 60;
		long seconds = cTime;

		String[] tfParts = timeFmt.split(":");
		timeFmt = "%d:%02d:%02d";
		
		if (tfParts.length == 3) {
			String[] tfMask = {"", "%d", "%02d"};
			timeFmt = tfMask[tfParts[0].length()] + ":" + tfMask[tfParts[1].length()] + ":" + tfMask[tfParts[2].length()]; 
		} else if (tfParts.length == 3) {
			String[] tfMask = {"", "%d", "%02d"};
			timeFmt = tfMask[tfParts[0].length()] + ":" + tfMask[tfParts[1].length()]; 
		}else if (tfParts.length == 3) {
			String[] tfMask = {"", "%d", "%02d"};
			timeFmt = tfMask[tfParts[0].length()]; 
		} 
		
		String text = String.format(timeFmt, hours, minutes, seconds);
		
		
		int width = shell.getBounds().width;
		int height = shell.getBounds().height;
		Image back = new Image(shell.getDisplay(), background);
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(back, 0, 0, back.getBounds().width,
				back.getBounds().height, 0, 0, width, height);

		int fontSize = height;
		int step = height / 2;
		int widthOverflow = 0;
		width -= 10;

		do {
			shell.getDisplay().loadFont("resources/calibri.ttf");
			Font calibri = new Font(shell.getDisplay(), "Calibri", fontSize,
					SWT.BOLD);
			gc.setFont(calibri);
			Point p = gc.textExtent(text);
			widthOverflow = width - p.x;

			if (widthOverflow > 0)
				fontSize += step;
			else if (widthOverflow < 0)
				fontSize -= step;
			else
				break;
			step /= 2;
		} while (step > 0);

		Point p = gc.textExtent(text);

		gc.setTextAntialias(SWT.ON);
		gc.setForeground(fontColor);
		gc.drawText(text, (width - p.x) / 2, (height - p.y) / 2,
				SWT.DRAW_TRANSPARENT);
		gc.dispose();
		back.dispose(); // don't forget about me!
		shell.setBackgroundImage(scaled);

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
		updateBackground();

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
		shell.setText("Neerc timer");

	}
}
