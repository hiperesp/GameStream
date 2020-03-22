package br.com.hiperesp.gameStream.cast.view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ScreenView {
	
	private JFrame screen;
	private Canvas canvas;
	
	public ScreenView() {
		screen = new JFrame();
		screen.setTitle("ScreenView");
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.setUndecorated(true);
		screen.setSize(1366, 768);
		screen.setLocation(1366, 0);
		screen.setLayout(null);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		init();
		screen.setVisible(true);
	}
	
	long lastNanoTime;

	private void init() {
		canvas = new Canvas();
		canvas.setBounds(0, 0, screen.getWidth(), screen.getHeight());
		screen.add(canvas);
		lastNanoTime = System.nanoTime();
	}

	public void setImage(BufferedImage newImage) {
		Graphics graphics = canvas.getGraphics();
		
		graphics.drawImage(newImage, 0, 0, screen.getWidth(), screen.getHeight(), null);
		long currentNanoTime = System.nanoTime();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, screen.getHeight()-10, 40, 20);
		graphics.setColor(Color.GREEN);
		graphics.drawString(1_000_000_000/(currentNanoTime-lastNanoTime)+" FPS", 0, screen.getHeight());
		lastNanoTime = currentNanoTime;
		graphics.dispose();
	}
}
