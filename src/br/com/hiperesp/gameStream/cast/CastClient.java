package br.com.hiperesp.gameStream.cast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import br.com.hiperesp.gameStream.cast.message.Message;
import br.com.hiperesp.gameStream.cast.message.MessageBuilder;
import br.com.hiperesp.gameStream.cast.protocol.IProtocol.Header;

public class CastClient {
	
	private DatagramSocket datagramServer;
	private ServerInfo server;
	private int bufferSize;
	private ICastReceive screenCastCallback;
	private boolean running;
	@SuppressWarnings("unused")
	private long pongNanos;
	
	private CastClient(InetAddress serverAddress, int serverPort, int bufferSize, ICastReceive screenCastCallback) throws SocketException {
		datagramServer = new DatagramSocket();
		this.bufferSize = bufferSize;
		server = new ServerInfo(serverAddress, serverPort);
		this.screenCastCallback = screenCastCallback;
	}
	
	public static CastClient create(InetAddress serverAddress, int port, int bufferSize, ICastReceive screenCastCallback) throws SocketException {
		return new CastClient(serverAddress, port, bufferSize, screenCastCallback);
	}
		
	public void startListening() throws IOException {
		running = true;
		while(running) {
			byte[] buffer = new byte[bufferSize];
			DatagramPacket packet = new DatagramPacket(buffer, bufferSize);
			datagramServer.receive(packet);
			if(server.is(packet.getAddress(), packet.getPort())) {
				MessageBuilder messageBuilder = server.getMessageBuilder();
				messageBuilder.append(buffer);
				if(messageBuilder.isDone()) {
					onReceiveMessage(server);
					server.resetMessageBuilder();
				}
			}
		}
	}

	public void authenticate() throws IOException {
		send(Message.create(Header.AUTHENTICATE));
	}

	public void stop() {
		running = false;
	}
	
	private void onReceiveMessage(ServerInfo server) throws IOException {
		Message message = server.getMessageBuilder().build();
		if(message.is(Header.PING)) {
			send(Message.create(Header.PONG));
			pongNanos = System.nanoTime();
			return;
		}
		if(message.is(Header.PONG_OK)) {
			long pongConfirmationNanos = System.nanoTime();
			//System.out.println("Client> ping: "+(pongConfirmationNanos-pongNanos)+"nanos ("+(pongConfirmationNanos-pongNanos)/1_000_000+"ms)");
			pongNanos = pongConfirmationNanos;
			return;
		}
		if(message.is(Header.AUTHENTICATED)) {
			System.out.println("Client> Authenticated.");
			return;
		}
		if(message.is(Header.UNAUTHENTICATED)) {
			System.out.println("Client> Can't authenticate.");
			System.exit(0);
			return;
		}
		if(message.is(Header.GOODBYE)) {
			System.out.println("Client> Kicked from server.");
			System.exit(0);
			return;
		}
		screenCastCallback.onReceiveMessage(message);
	}
	
	private void send(DatagramPacket[] packet) throws IOException {
		for(int i=0; i<packet.length; i++) {
			datagramServer.send(packet[i]);
		}
	}
	
	public void send(Message message) throws IOException {
		send(server.createMessage(message, bufferSize));
	}
}
