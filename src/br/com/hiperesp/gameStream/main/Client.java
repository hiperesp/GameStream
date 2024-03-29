package br.com.hiperesp.gameStream.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import br.com.hiperesp.gameStream.cast.CastClient;
import br.com.hiperesp.gameStream.cast.ICastReceive;
import br.com.hiperesp.gameStream.cast.message.Message;
import br.com.hiperesp.gameStream.cast.protocol.IProtocol.Header;
import br.com.hiperesp.gameStream.cast.view.ScreenView;

public class Client implements ICastReceive {
	
	private CastClient castClient;
	private ScreenView screenView;
	
	public Client(String serverIp, int serverPort, int bufferSize) throws SocketException, UnknownHostException {
		castClient = CastClient.create(InetAddress.getByName(serverIp), serverPort, bufferSize, this);
		screenView = new ScreenView();
	}
	public Client(String serverIp, int serverPort) throws SocketException, UnknownHostException {
		this(serverIp, serverPort, 65467);
	}
	
	public void run() throws IOException, InterruptedException {
		new Thread() {
			@Override
			public void run() {
				try {
					castClient.startListening();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		Thread.sleep(1000);
		new Thread() {
			@Override
			public void run() {
				try {
					castClient.authenticate();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	@Override
	public void onReceiveMessage(Message message) {
		if(message.is(Header.DATA_IMAGE)) {
			try {
				screenView.setImage(message.getImageMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Client> Insira o endere�o do servidor (geralmente 127.0.0.1): ");
		String serverAddress = scanner.next();
		System.out.println();
		
		System.out.print("Client> Insira a porta do servidor (geralmente 8090): ");
		int port = scanner.nextInt();
		System.out.println();
		
		scanner.close();
		
		Client client = new Client(serverAddress, port);
		client.run();
		
	}

}
