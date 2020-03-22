package br.com.hiperesp.gameStream.main;

import java.awt.AWTException;
import java.io.IOException;
import java.net.SocketException;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		new Thread() {
			@Override
			public void run() {
				try {
					Server.main(args);
				} catch (SocketException | AWTException e) {
					e.printStackTrace();
				}
			}
		}.start();
		Thread.sleep(1000);
		new Thread() {
			@Override
			public void run() {
				try {
					Client.main(args);
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
