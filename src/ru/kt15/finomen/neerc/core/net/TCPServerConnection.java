package ru.kt15.finomen.neerc.core.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import ru.kt15.finomen.neerc.core.Core;
import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.core.net.proto.Net;

import com.google.protobuf.ByteString;

public class TCPServerConnection extends ProtobufConnection implements Runnable {
	private/* final */Queue<ByteString> sendQueue;
	private/* final */Queue<ByteString> rcvQueue;
	private SelectionKey serverKey;
	private static final long INITIAL_RECONNECT_INTERVAL = 200; // 200 ms.
	private static final long MAXIMUM_RECONNECT_INTERVAL = 10000; // 10 sec.
	private static final int READ_BUFFER_SIZE = 0x100000;
	private static final int WRITE_BUFFER_SIZE = 0x100000;

	private long reconnectInterval = INITIAL_RECONNECT_INTERVAL;

	private ByteBuffer readBuf = ByteBuffer.allocateDirect(READ_BUFFER_SIZE); // 1Mb
	private ByteBuffer writeBuf = ByteBuffer.allocateDirect(WRITE_BUFFER_SIZE); // 1Mb

	private final Thread thread = new Thread(this);
	private final Thread evalutor;
	private SocketAddress address;

	private Selector selector;
	private SocketChannel channel;

	private final AtomicBoolean connected = new AtomicBoolean(false);

	private AtomicLong bytesOut = new AtomicLong(0L);
	private AtomicLong bytesIn = new AtomicLong(0L);

	public TCPServerConnection(String serverHost, int serverPort) {
		address = new InetSocketAddress(serverHost, serverPort);
		thread.start();
		evalutor = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					ByteString bs;
					synchronized (rcvQueue) {
						while (rcvQueue.isEmpty()) {
							try {
								rcvQueue.wait();
							} catch (InterruptedException e) {
								return;
							}
						}
						
						bs = rcvQueue.poll();
					}
					
					onRecv(null, bs);
				}
			}
		});
		evalutor.start();
	}

	public void stop() throws IOException, InterruptedException {
		thread.interrupt();
		evalutor.interrupt();
		selector.wakeup();
	}

	@Override
	public boolean isConnected() {
		return connected.get();
	}

	public void HandlePacket(Endpoint endpoint, Net.NetMessage packet) {
		onRecv(new Endpoint(packet.getSourceId()), packet.getData());
	}

	@Override
	public void sendTo(Endpoint destination, ByteString data) {
		ByteString netPacket = Net.NetMessage.newBuilder()
				.setDestinationId(destination.getId())
				.setSourceId(Core.getId()).setData(data).build().toByteString();
		synchronized (sendQueue) {
			sendQueue.add(netPacket);
		}

		SelectionKey key = channel.keyFor(selector);
		key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
		selector.wakeup();

	}

	private void configureChannel(SocketChannel channel) throws IOException {
		channel.configureBlocking(false);
		channel.socket().setSendBufferSize(WRITE_BUFFER_SIZE); // 1Mb
		channel.socket().setReceiveBufferSize(READ_BUFFER_SIZE); // 1Mb
		channel.socket().setKeepAlive(true);
		channel.socket().setReuseAddress(true);
		channel.socket().setSoLinger(false, 0);
		channel.socket().setSoTimeout(0);
		channel.socket().setTcpNoDelay(true);
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) { // reconnection loop
				try {
					selector = Selector.open();
					channel = SocketChannel.open();
					configureChannel(channel);

					channel.connect(address);
					channel.register(selector, SelectionKey.OP_CONNECT);

					while (!thread.isInterrupted() && channel.isOpen()) { // events
																			// multiplexing
																			// loop
						if (selector.select() > 0)
							processSelectedKeys(selector.selectedKeys());
					}
				} catch (Exception e) {
					Log.writeError("exception: " + e.getLocalizedMessage());
				} finally {
					connected.set(false);
					// onDisconnected();
					writeBuf.clear();
					readBuf.clear();
					if (channel != null)
						channel.close();
					if (selector != null)
						selector.close();
					Log.writeInfo("connection closed");
				}

				try {
					Thread.sleep(reconnectInterval);
					if (reconnectInterval < MAXIMUM_RECONNECT_INTERVAL)
						reconnectInterval *= 2;
					Log.writeInfo("reconnecting to " + address);
				} catch (InterruptedException e) {
					break;
				}
			}
		} catch (Exception e) {
			Log.writeError("unrecoverable error " + e.getLocalizedMessage());
		}
	}

	private void processSelectedKeys(Set<SelectionKey> keys) throws Exception {
		Iterator<SelectionKey> itr = keys.iterator();
		while (itr.hasNext()) {
			SelectionKey key = itr.next();
			if (key.isReadable())
				processRead(key);
			if (key.isWritable())
				processWrite(key);
			if (key.isConnectable())
				processConnect(key);
			if (key.isAcceptable())
				;
			itr.remove();
		}
	}

	private void processConnect(SelectionKey key) throws Exception {
		SocketChannel ch = (SocketChannel) key.channel();
		if (ch.finishConnect()) {
			Log.writeInfo("connected to " + address);
			key.interestOps(key.interestOps() ^ SelectionKey.OP_CONNECT);
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			reconnectInterval = INITIAL_RECONNECT_INTERVAL;
			connected.set(true);
			// onConnected();
		}
	}

	private void processRead(SelectionKey key) throws Exception {
		ReadableByteChannel ch = (ReadableByteChannel) key.channel();

		int bytesOp = 0, bytesTotal = 0;
		while (readBuf.hasRemaining() && (bytesOp = ch.read(readBuf)) > 0)
			bytesTotal += bytesOp;

		if (bytesTotal > 0) {
			readBuf.flip();
			ByteString bStr = ByteString.copyFrom(readBuf);
			synchronized (rcvQueue) {
				rcvQueue.add(bStr);
				rcvQueue.notifyAll();
			}
			readBuf.compact();
		} else if (bytesOp == -1) {
			Log.writeInfo("peer closed read channel");
			ch.close();
		}

		bytesIn.addAndGet(bytesTotal);
	}

	private void processWrite(SelectionKey key) throws IOException {
		WritableByteChannel ch = (WritableByteChannel) key.channel();
		synchronized (writeBuf) {
			writeBuf.flip();

			int bytesOp = 0, bytesTotal = 0;
			while (writeBuf.hasRemaining()
					&& (bytesOp = ch.write(writeBuf)) > 0)
				bytesTotal += bytesOp;

			bytesOut.addAndGet(bytesTotal);

			if (writeBuf.remaining() == 0) {
				synchronized (sendQueue) {
					if (sendQueue.isEmpty()) {
						key.interestOps(key.interestOps()
								^ SelectionKey.OP_WRITE);
					} else {
						writeBuf.put(sendQueue.poll().asReadOnlyByteBuffer());
					}
				}

			}

			if (bytesTotal > 0)
				writeBuf.notify();
			else if (bytesOp == -1) {
				Log.writeInfo("peer closed write channel");
				ch.close();
			}

			writeBuf.compact();
		}
	}

}
