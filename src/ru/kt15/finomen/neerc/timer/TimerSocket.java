package ru.kt15.finomen.neerc.timer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ru.kt15.finomen.neerc.core.Log;

public class TimerSocket implements Runnable {
	private final TimerWindow window;
	private final DatagramChannel channel;
	private final List<MembershipKey> memberKey;
	private final Thread worker;
	
	public TimerSocket(TimerWindow wnd, Map<String, Object> config) throws IOException {
		this.window = wnd;
		channel = DatagramChannel.open(StandardProtocolFamily.INET);
		channel.configureBlocking(true);
		channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
		channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		
		channel.bind(new InetSocketAddress((Integer)config.get("udp-port")));
		memberKey = new ArrayList<MembershipKey>();
		
		if (config.containsKey("multicast-group")) {
			for (NetworkInterface interf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				try {
					channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, interf);
					memberKey.add(channel.join(InetAddress.getByName((String)config.get("multicast-group")), interf));
					Log.writeInfo("Joined group " + config.get("multicast-group") + " on interface " + interf.getDisplayName() + "[" + interf.getName() + "]");
				} catch (IOException e)
				{
					Log.writeError("Failed to join group " + config.get("multicast-group") + " on interface " + interf.getDisplayName());
				}
			}
		}
		
		worker = new Thread(this);
		worker.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				ByteBuffer buf = ByteBuffer.allocate(512);
				/*SocketAddress remote = */channel.receive(buf);
				buf.flip();
				byte cmd = buf.get();
				switch (cmd) {
				case 0x01:
					int status = buf.get();
					long time = buf.getLong();
					long duration = buf.getLong();
					window.Sync(TimerStatus.getById(status), duration, duration - time);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//buf.put((byte) 0x01);
			//buf.put((byte) cl.getStatus());
			//buf.putLong(cl.getTime());
			//buf.putLong(cl.getLength());
			//buf.flip();
		}
	}
}
