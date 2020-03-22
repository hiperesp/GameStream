package br.com.hiperesp.gameStream.main;

import java.awt.AWTException;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws InterruptedException, AWTException, IOException {
		Server.main(args);
		Thread.sleep(1000);
		Client.main(args);
	}
}
