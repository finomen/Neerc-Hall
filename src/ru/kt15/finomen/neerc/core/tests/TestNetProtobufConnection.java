package ru.kt15.finomen.neerc.core.tests;

import static org.junit.Assert.*;

import java.lang.ref.Reference;
import java.util.Date;

import org.junit.Test;

import com.google.protobuf.ByteString;

import ru.kt15.finomen.neerc.core.Log;
import ru.kt15.finomen.neerc.core.net.Endpoint;
import ru.kt15.finomen.neerc.core.net.ProtobufConnection;
import ru.kt15.finomen.neerc.core.net.proto.Net;
import ru.kt15.finomen.neerc.core.net.proto.Net.ProtobufPacket;

public class TestNetProtobufConnection {
	private String testString;
	
	public class TestOnRecv extends ProtobufConnection {
		@Override
		public void sendTo(Endpoint destination, ByteString data) {			
		}
		
		public void handlePacket(Endpoint source, Net.ProtobufPacket message) {
			testString = message.getClass().getCanonicalName();
			Net.ProtobufPacket p = (ProtobufPacket) message;
			assertEquals("type", p.getType());
			assertEquals("group", p.getGroup());
			assertEquals("data", p.getData().toStringUtf8());
		}
	}
	
	@Test
	public void testOnRecv() {
		testString = "";
		ProtobufConnection pc = new TestOnRecv();
	
		Net.ProtobufPacket p = Net.ProtobufPacket.newBuilder().setType("type").setGroup("group").setData(ByteString.copyFromUtf8("data")).build();
			
		pc.onRecv(null, Net.ProtobufPacket.newBuilder()
		.setGroup(Net.class.getCanonicalName())
		.setType(Net.ProtobufPacket.class.getSimpleName())
		.setData(p.toByteString())
		.build().toByteString());
		
		assertEquals(Net.ProtobufPacket.class.getCanonicalName(), testString);
	}
	
	public class TestSendRecv extends ProtobufConnection {
		@Override
		public void sendTo(Endpoint destination, ByteString data) {		
			onRecv(destination, data);
		}
		
		public void handlePacket(Endpoint source, Net.ProtobufPacket message) {
			testString = message.getClass().getCanonicalName();
			Net.ProtobufPacket p = (ProtobufPacket) message;
			assertEquals("type", p.getType());
			assertEquals("group", p.getGroup());
			assertEquals("data", p.getData().toStringUtf8());
		}
	}
	
	@Test
	public void testSendRecv() {
		testString = "";
		ProtobufConnection pc = new TestSendRecv();
	
		Net.ProtobufPacket p = Net.ProtobufPacket.newBuilder().setType("type").setGroup("group").setData(ByteString.copyFromUtf8("data")).build();
			
		pc.sendTo(null, p);
		
		assertEquals(Net.ProtobufPacket.class.getCanonicalName(), testString);
	}
	
	public class TestNoHandler extends ProtobufConnection {
		@Override
		public void sendTo(Endpoint destination, ByteString data) {		
			onRecv(destination, data);
		}
	}
	
	@Test
	public void testNoHandler() {
		testString = "";
		ProtobufConnection pc = new TestNoHandler();
		Log.setImpl(new Log() {
			
			@Override
			protected void writeInfo(Date time, String s) {		
			}
			
			@Override
			protected void writeError(Date time, String s) {
				testString = s;
			}
			
			@Override
			protected void writeDebug(Date time, String s) {				
			}
		});
	
		Net.ProtobufPacket p = Net.ProtobufPacket.newBuilder().setType("type").setGroup("group").setData(ByteString.copyFromUtf8("data")).build();
			
		pc.sendTo(null, p);
		
		assertEquals("Unhandled packet " + Net.ProtobufPacket.class.getCanonicalName(), testString);
	}
	
	@Test
	public void testBadHandler() {
		testString = "";
		ProtobufConnection pc = new ProtobufConnection() {
			
			public void handlePacket(Endpoint source, Net.ProtobufPacket message) {
				testString = "Success";
			}
			
			@Override
			public void sendTo(Endpoint destination, ByteString data) {
				onRecv(destination, data);
			}
		};
		Log.setImpl(new Log() {
			
			@Override
			protected void writeInfo(Date time, String s) {		
			}
			
			@Override
			protected void writeError(Date time, String s) {
				testString = testString + s;
			}
			
			@Override
			protected void writeDebug(Date time, String s) {				
			}
		});
	
		Net.ProtobufPacket p = Net.ProtobufPacket.newBuilder().setType("type").setGroup("group").setData(ByteString.copyFromUtf8("data")).build();
			
		pc.sendTo(null, p);
		
		assertEquals("Bad handler for " + Net.ProtobufPacket.class.getCanonicalName() +
				"Unhandled packet " + Net.ProtobufPacket.class.getCanonicalName(), testString);
	}
	
	@Test
	public void testBadProtobuf() {
		testString = "";
		ProtobufConnection pc = new ProtobufConnection() {
			
			public void handlePacket(Endpoint source, Net.ProtobufPacket message) {
				testString = "Success";
			}
			
			@Override
			public void sendTo(Endpoint destination, ByteString data) {
				onRecv(destination, ByteString.copyFromUtf8(("Bad" + data.toStringUtf8())));
			}
		};
		Log.setImpl(new Log() {
			
			@Override
			protected void writeInfo(Date time, String s) {		
			}
			
			@Override
			protected void writeError(Date time, String s) {
				testString = s;
			}
			
			@Override
			protected void writeDebug(Date time, String s) {				
			}
		});
	
		Net.ProtobufPacket p = Net.ProtobufPacket.newBuilder().setType("type").setGroup("group").setData(ByteString.copyFromUtf8("data")).build();
			
		pc.sendTo(null, p);
		
		assertEquals("Bad packet", testString);
	}

}
