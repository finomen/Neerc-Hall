package ru.kt15.finomen.neerc.timer;

import java.net.MalformedURLException;
import java.util.Map;

import pcms2.services.client.AuthorizationFailedException;
import pcms2.services.client.LoginData;
import pcms2.services.client.LoginDataService;
import pcms2.services.site.Clock;

import com.caucho.hessian.client.HessianProxyFactory;


public class PCMS2ClientSocket {
	private final int updateInterval;
	private final String url, login, password;
	private final TimerWindow window;

	private class Worker implements Runnable {

		@Override
		public void run() {
			HessianProxyFactory factory = new HessianProxyFactory();
			factory.setConnectTimeout(2000);
			while (true) {
				try {
					LoginDataService ldsvc = (LoginDataService) factory.create(
							LoginDataService.class, url);
					LoginData ld = ldsvc.getLoginData(login, password);
					if (ld != null) {
						Clock clock = ld.getClock();

						window.Sync(TimerStatus.getById(clock.getStatus()), clock.getLength(), clock.getLength() - clock.getTime());						
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (AuthorizationFailedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(updateInterval);
				} catch (InterruptedException e) {
					return;
				}
			}
		}

	}

	public PCMS2ClientSocket(TimerWindow wnd, Map<String, Object> config) {
		this.window = wnd;
		this.url = (String) config.get("host");
		this.login = (String) config.get("login");
		this.password = (String) config.get("password");
		this.updateInterval = (Integer)config.get("updateInterval");
		new Thread(new Worker()).start();
	}
}