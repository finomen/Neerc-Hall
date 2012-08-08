package ru.kt15.finomen.neerc.core.net;

import com.google.protobuf.ByteString;

public interface AbstractConnection {
	
	/**
	 * @param destination destination node of this packet
	 * @param data packet binary data
	 * Send binary packet to given node
	 */
	void sendTo(Endpoint destination, ByteString data);
	/**
	 * @param source source node of this packet
	 * @param data binary packet data
	 * Callback called when packet received
	 */
	void onRecv(Endpoint source, ByteString data);
}
