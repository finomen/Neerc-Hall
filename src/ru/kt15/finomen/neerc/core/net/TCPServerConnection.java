package ru.kt15.finomen.neerc.core.net;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import ru.kt15.finomen.neerc.core.net.proto.Net;

import com.google.protobuf.ByteString;

public class TCPServerConnection extends ProtobufConnection {
	private /*final*/ SocketChannel channel;
	private /*final*/ Queue<ByteString> sendQueue;
	private /*final*/ Queue<ByteString> rcvQueue;
	private SelectionKey serverKey;

	public TCPServerConnection(String serverHost, int serverPort) {
		
	}

	@Override
	public void sendTo(Endpoint destination, ByteString data) {
		ByteString netPacket = Net.NetMessage.newBuilder()
				.setDestinationId(destination.getId()).setData(data).build()
				.toByteString();
		synchronized (this) {
			//sendQueue.add(netPacket);
		
			if (serverKey != null) {
				serverKey.interestOps(SelectionKey.OP_WRITE);
			}
		}
			
	}

}
