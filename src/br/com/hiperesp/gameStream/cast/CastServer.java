package br.com.hiperesp.gameStream.cast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import br.com.hiperesp.gameStream.cast.message.Message;
import br.com.hiperesp.gameStream.cast.message.MessageBuilder;
import br.com.hiperesp.gameStream.cast.protocol.IProtocol.Header;

public class CastServer {
	
	private DatagramSocket datagramServer;
	private ArrayList<ClientInfo> clientList;
	private int bufferSize;
	private ICastReceive screenCastCallback;
	private boolean running;
	private double pingKey;
	private long pingNanos;
	
	private CastServer(int port, int bufferSize, ICastReceive screenCastCallback) throws SocketException {
		this.bufferSize = bufferSize;
		datagramServer = new DatagramSocket(port);
		clientList = new ArrayList<ClientInfo>();
		pingKey = 0;
		this.screenCastCallback = screenCastCallback;
	}
	
	public static CastServer create(int port, int bufferSize, ICastReceive screenCastCallback) throws SocketException {
		return new CastServer(port, bufferSize, screenCastCallback);
	}
	
	public void startPing() throws InterruptedException, IOException {
		while(running) {
			pingKey = Math.random();
			pingNanos = System.nanoTime();
			broadcast(Message.create(Header.PING));
			Thread.sleep(10000);
			for (int i=0; i<clientList.size(); i++) {
				ClientInfo client = clientList.get(i);
				if(!client.isOnline(pingKey)) {
					System.out.println("Server> "+client+" left.");
					send(client, Message.create(Header.GOODBYE));
					clientList.remove(client);
					i--;
				}
			}
			System.out.println("Server> "+clientList.size()+" users online.");
		}
	}

	public void startListening() throws IOException {
		running = true;
		System.out.println("Server> Listening on port "+datagramServer.getLocalPort()+" [UDP]");
		while(running) {
			byte[] buffer = new byte[bufferSize];
			DatagramPacket packet = new DatagramPacket(buffer, bufferSize);
			datagramServer.receive(packet);
			parseReceivedPacket(packet);
		}
	}
	
	private void parseReceivedPacket(DatagramPacket packet) throws IOException {
		for (ClientInfo client : clientList) {
			if(client.is(packet.getAddress(), packet.getPort())) {
				MessageBuilder messageBuilder = client.getMessageBuilder();
				messageBuilder.append(packet.getData());
				if(messageBuilder.isDone()) {
					onReceiveMessage(client);
					client.resetMessageBuilder();
				}
				return;
			}				
		}
		ClientInfo client = new ClientInfo(packet.getAddress(), packet.getPort(), pingKey);
		System.out.println("Server> "+client+" joined.");
		MessageBuilder messageBuilder = client.getMessageBuilder();
		messageBuilder.append(packet.getData());
		if(messageBuilder.isDone()) {
			authenticate(client);
		}
	}

	public void stop() {
		running = false;
	}
	
	private void authenticate(ClientInfo client) throws IOException {
		if(client.getMessageBuilder().build().is(Header.AUTHENTICATE)) {
			this.clientList.add(client);
			send(client, Message.create(Header.AUTHENTICATED));
			System.out.println("Server> "+client+" authenticated.");
		} else {
			send(client, Message.create(Header.UNAUTHENTICATED));
			System.out.println("Server> "+client+" is not authenticated.");
		}
	}
	
	private void onReceiveMessage(ClientInfo client) throws IOException {
		Message message = client.getMessageBuilder().build();
		if(message.is(Header.PONG)) {
			long pongNanos = System.nanoTime();
			long clientPingNanos = pongNanos-pingNanos;
			client.setPing(clientPingNanos);
			client.setOnline(pingKey);
			System.out.println("Server> "+client+"'s ping: "+clientPingNanos+"nanos ("+clientPingNanos/1_000_000+"ms)");
			send(client, Message.create(Header.PONG_OK));
			return;
		}
		screenCastCallback.onReceiveMessage(message);
	}
	
	private void send(DatagramPacket[] packet) throws IOException {
		for(int i=0; i<packet.length; i++) {
			datagramServer.send(packet[i]);
		}
	}
	
	private void send(ClientInfo client, byte[][] messageBytes) throws IOException {
		send(client.createMessage(messageBytes));
	}
	
	public void send(ClientInfo client, Message message) throws IOException {
		byte[][] messageBytes = message.getMessageList(bufferSize);
		send(client, messageBytes);
	}
	
	public void broadcast(Message message) throws IOException {
		byte[][] messageBytes = message.getMessageList(bufferSize);
		for (ClientInfo client : clientList) {
			send(client, messageBytes);
		}
	}
}
