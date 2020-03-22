package br.com.hiperesp.gameStream.cast;

import java.net.DatagramPacket;
import java.net.InetAddress;

import br.com.hiperesp.gameStream.cast.message.Message;
import br.com.hiperesp.gameStream.cast.message.MessageBuilder;

public class ServerInfo {

	private InetAddress address;
	private int port;
	private MessageBuilder messageBuilder;
	
	public ServerInfo(InetAddress address, int port) {
		this.address = address;
		this.port = port;
		resetMessageBuilder();
	}
	
	public DatagramPacket[] createMessage(Message message, int bufferSize) {
		byte[][] messageBytes = message.getMessageList(bufferSize);
		DatagramPacket[] messagePackets = new DatagramPacket[messageBytes.length];
		for(int i=0; i<messageBytes.length; i++) {
			messagePackets[i] = new DatagramPacket(messageBytes[i], messageBytes[i].length, address, port);
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

}
