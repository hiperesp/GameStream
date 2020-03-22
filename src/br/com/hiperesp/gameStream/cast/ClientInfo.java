package br.com.hiperesp.gameStream.cast;

import java.net.DatagramPacket;
import java.net.InetAddress;

import br.com.hiperesp.gameStream.cast.message.MessageBuilder;

public class ClientInfo {

	private InetAddress address;
	private int port;
	private double pingKey;
	private MessageBuilder messageBuilder;
	private long pingNanos;
	
	public ClientInfo(InetAddress address, int port, double pingKey) {
		this.address = address;
		this.port = port;
		this.pingKey = pingKey;
		resetMessageBuilder();
	}
	
	public boolean isOnline(double pingKey) {
		return this.pingKey==pingKey;
	}
	
	public void setOnline(double pingKey) {
		this.pingKey = pingKey;
	}
	
	public DatagramPacket[] createMessage(byte[][] message) {
		DatagramPacket[] messagePackets = new DatagramPacket[message.length];
		for(int i=0; i<message.length; i++) {
			messagePackets[i] = new DatagramPacket(message[i], message[i].length, address, port);
		}
		return messagePackets;
	}
	
	public boolean is(InetAddress address, int port) {
		return this.port==port&&this.address.equals(address);
	}
	
	public MessageBuilder getMessageBuilder() {
		return messageBuilder;
	}
	
	public void resetMessageBuilder() {
		messageBuilder = new MessageBuilder();
	}
	
	public long getPing() {
		return pingNanos;
	}
	
	public void setPing(long nanos) {
		pingNanos = nanos;
	}
	
	public String toString() {
		return this.address.getHostAddress()+":"+this.port;
	}

}
