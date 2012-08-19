package ru.kt15.finomen.neerc.core.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.core.net.proto.Net;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public abstract class ProtobufConnection implements AbstractConnection {
	private Set<Object> handlers;
	
	public ProtobufConnection() {
		handlers = new HashSet();
		handlers.add(this);
	}
	
	public <MessageType extends GeneratedMessage> void sendTo(Endpoint destination, MessageType message) {
		sendTo(destination, 
				Net.ProtobufPacket.newBuilder()
					.setGroup(message.getClass().getEnclosingClass().getCanonicalName())
					.setType(message.getClass().getSimpleName())
					.setData(message.toByteString())
					.build().toByteString());
	}
	
	public void AddHandler(Object handler) {		
		handlers.add(handler);
	}
	
	public void RemoveHandler(Object handler) {
		handlers.remove(handler);
	}
	
	@Override
	public void onRecv(Endpoint source, ByteString data) {
		try {
			Net.ProtobufPacket packet = Net.ProtobufPacket.parseFrom(data);
			Class<?> packageClass = Class.forName(packet.getGroup());
			packageClass = Class.forName(packet.getGroup() + "$" + packet.getType());
			Method method = packageClass.getMethod("parseFrom", ByteString.class);
			Object obj = method.invoke(null, packet.getData());
			
			boolean handled = false;
			for (Object handler : handlers) {
				try {
					Method onRecv = handler.getClass().getMethod("handlePacket", Endpoint.class, packageClass);
					onRecv.invoke(handler, source, packageClass.cast(obj));
					handled = true;
				} catch(NoSuchMethodException e) {
					//FIXME: bad logic based on exceptions
				} catch(IllegalAccessException e) {
					Log.writeError("Bad handler for " + packageClass.getCanonicalName());
				}

			}
			if (!handled) {
				Log.writeError("Unhandled packet " + packageClass.getCanonicalName());
			}
			
		} catch (InvalidProtocolBufferException | NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Log.writeError("Bad packet");
		}
	}
}
