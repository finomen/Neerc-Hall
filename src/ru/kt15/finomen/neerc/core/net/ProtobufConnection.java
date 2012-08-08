package ru.kt15.finomen.neerc.core.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ru.kt15.finomen.neerc.core.net.proto.Net;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public abstract class ProtobufConnection implements AbstractConnection {
	public <MessageType extends GeneratedMessage> void sendTo(Endpoint destination, MessageType message) {
		sendTo(destination, 
				Net.ProtobufPacket.newBuilder()
					.setGroup(message.getClass().getEnclosingClass().getCanonicalName())
					.setType(message.getClass().getSimpleName())
					.setData(message.toByteString())
					.build().toByteString());
	}
	
	public abstract <MessageType> void onRecv(Endpoint source, MessageType message);
	
	@Override
	public void onRecv(Endpoint source, ByteString data) {
		try {
			Net.ProtobufPacket packet = Net.ProtobufPacket.parseFrom(data);
			Class<?> packageClass = Class.forName(packet.getGroup());
			packageClass = Class.forName(packet.getGroup() + "$" + packet.getType());
			Method method = packageClass.getMethod("parseFrom", ByteString.class);
			Object obj = method.invoke(null, packet.getData());
			onRecv(source, packageClass.cast(obj));			
		} catch (InvalidProtocolBufferException | NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
