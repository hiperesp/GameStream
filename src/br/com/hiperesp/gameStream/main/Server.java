package br.com.hiperesp.gameStream.main;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

import br.com.hiperesp.gameStream.cast.CastServer;
import br.com.hiperesp.gameStream.cast.ICastReceive;
import br.com.hiperesp.gameStream.cast.capture.IScreenCapture;
import br.com.hiperesp.gameStream.cast.capture.ScreenCapture;
import br.com.hiperesp.gameStream.cast.message.Message;

public class Server implements IScreenCapture, ICastReceive {
	
	private ScreenCapture screenCapture;
	private CastServer screenCast;
	private String encoding;
	
	public Server(int serverPort, String encoding, int bufferSize) throws AWTException, SocketException {
		screenCapture = ScreenCapture.create(this);
		screenCast = CastServer.create(serverPort, bufferSize, this);
		this.encoding = encoding;
	}

	public Server(int serverPort, String encoding) throws AWTException, SocketException {
		this(serverPort, encoding, 65467);
	}
	
	public void run() {
		new Thread() {
			@Override
			public void run() {
				screenCapture.startCasting();
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					screenCast.startListening();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					screenCast.startPing();
				} catch (InterruptedException|IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onImage(BufferedImage frame) {
		try {
			screenCast.broadcast(Message.create(frame, encoding));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceiveMessage(Message message) {

	}
	
	public static void main(String[] args) throws SocketException, AWTException {

		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Server> Insira a porta do servidor (geralmente 8090): ");
		int port = scanner.nextInt();
		System.out.println();
		
		System.out.print("Server> Insira o encoding da transmissão (geralmente png ou jpg): ");
		String encoding = scanner.next();
		System.out.println();
		
		scanner.close();
		
		Server server = new Server(port, encoding);
		server.run();
	}
}
