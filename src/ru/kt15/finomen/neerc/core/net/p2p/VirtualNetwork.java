package ru.kt15.finomen.neerc.core.net.p2p;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MultiHashMap;

import com.google.protobuf.ByteString;

import ru.kt15.finomen.neerc.core.net.Endpoint;
import ru.kt15.finomen.neerc.core.net.ProtobufConnection;

public class VirtualNetwork extends ProtobufConnection{
	private static final UUID broadcastUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private final P2POptions options;
	private final DatagramChannel udpChannel;
	final private List<MembershipKey> multicastKey;
	private final Map<UUID, SocketChannel> streamChannels;
	private final ServerSocketChannel streamServer;
	private final Map userIds;
	private final Map<UUID, SocketAddress> netEndpoints;
	private final Map<UUID, String> nodeNames;
	private final UUID networkId; //FIXME: use GUID
	
	public VirtualNetwork(P2POptions op) throws IOException {
		options = op;		
		if (options.isUseIpV6()) {
			udpChannel = DatagramChannel.open(StandardProtocolFamily.INET6);
		} else {
			udpChannel = DatagramChannel.open(StandardProtocolFamily.INET);
		}
		
		udpChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		
		
		if (options.isUseMulticast()) {
			multicastKey = new ArrayList<MembershipKey>();
			InetAddress group = InetAddress.getByName(options.getMulticastGroupIP());
			for(NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) { 
				udpChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, iface);
				multicastKey.add(udpChannel.join(group, iface));
			}
 		} else {
 			multicastKey = null;
 		}
		
		streamChannels = new HashMap<UUID, SocketChannel>();
		
		if (options.isAllowTCPRelay()) {
			streamServer = ServerSocketChannel.open();
			streamServer.bind(new InetSocketAddress(options.getTCPPort()));
		} else {
			streamServer = null;
		}
				
		userIds = new MultiHashMap();
		netEndpoints = new HashMap<UUID, SocketAddress>();
		nodeNames = new HashMap<UUID, String>();
		
		networkId = UUID.randomUUID();
	}
	
	@Override
	public void sendTo(Endpoint destination, ByteString data) {
		if (destination.getId().equals("*")) {
			broadcast(broadcastUUID, data);
			return;
		}
		
		Collection<UUID> nodeIds = (Collection<UUID>)userIds.get(destination.getId());
		
		if (nodeIds.isEmpty()) {
			//TODO: throw
			return;
		}
		
		for (UUID nodeId : nodeIds) {			
			SocketChannel stream = getStream(nodeId);
			
			if (stream != null) {
				sendTo(stream, nodeId, data);
				return;
			}
			
			SocketAddress dest = netEndpoints.get(nodeId);
						
			if (dest != null) {
				sendTo(dest, nodeId, data);
				return;
			}
			
			broadcast(nodeId, data);
		}
		
	}
	
	private void broadcast(UUID destination, ByteString data) {
		//TODO:
	}
	
	private void sendTo(SocketAddress destination, UUID destId, ByteString data) {
		
	}
	
	private void sendTo(SocketChannel destination, UUID destId, ByteString data) {
		
	}
 
	private SocketChannel getStream(UUID id) {
		SocketChannel ch = streamChannels.get(id);
		if (ch == null) {
			return null;
		}
		
		if (ch.isConnected()) {
			return ch;
		}
		
		streamChannels.remove(id);
		return null;
	}
}
