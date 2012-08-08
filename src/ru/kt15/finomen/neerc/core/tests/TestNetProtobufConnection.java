package ru.kt15.finomen.neerc.core.tests;

import static org.junit.Assert.*;

import java.lang.ref.Reference;

import org.junit.Test;

import com.google.protobuf.ByteString;

import ru.kt15.finomen.neerc.core.net.Endpoint;
import ru.kt15.finomen.neerc.core.net.ProtobufConnection;
import ru.kt15.finomen.neerc.core.net.proto.Net;
import ru.kt15.finomen.neerc.core.net.proto.Net.ProtobufPacket;

public class TestNetProtobufConnection {
	private String testString;
	@Test
	public void testOnRecv() {
		testString = "";
		ProtobufConnection pc = new ProtobufConnection() {
			@Override
			public void sendTo(Endpoint destination, ByteString data) {			
			}
			
			@Override
			public <MessageType> void onRecv(Endpoint source, MessageType message) {
				testString = message.getClass().getCanonicalName();
				Net.ProtobufPacket p = (ProtobufPacket) message;
				assertEquals("type", p.getType());
				assertEquals("group", p.getGroup());
				assertEquals("data", p.getData().toStringUtf8());
			}
		};
	
		Net.ProtobufPacket p = Net.ProtobufPacket.newBuilder().setType("type").setGroup("group").setData(ByteString.copyFromUtf8("data")).build();
			
		pc.onRecv(null, Net.ProtobufPacket.newBuilder()
		.setGroup(Net.class.getCanonicalName())
		.setType(Net.ProtobufPacket.class.getSimpleName())
		.setData(p.toByteString())
		.build().toByteString());
		
		assertEquals(Net.ProtobufPacket.class.getCanonicalName(), testString);
	}
	
	@Test
	public void testSendRecv() {
		testString = "";
		ProtobufConnection pc = new ProtobufConnection() {
			@Override
			public void sendTo(Endpoint destination, ByteString data) {		
				onRecv(destination, data);
			}
			
			@Override
			public <MessageType> void onRecv(Endpoint source, MessageType message) {
				testString = message.getClass().getCanonicalName();
				Net.ProtobufPacket p = (ProtobufPacket) message;
				assertEquals("type", p.getType());
				assertEquals("group", p.getGroup());
				assertEquals("data", p.getData().toStringUtf8());
			}
		};
	
		Net.ProtobufPacket p = Net.ProtobufPacket.newBuilder().setType("type").setGroup("group").setData(ByteString.copyFromUtf8("data")).build();
			
		pc.sendTo(null, p);
		
		assertEquals(Net.ProtobufPacket.class.getCanonicalName(), testString);
	}

}
