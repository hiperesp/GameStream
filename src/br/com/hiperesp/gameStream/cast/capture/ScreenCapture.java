package br.com.hiperesp.gameStream.cast.capture;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class ScreenCapture {

	private GraphicsDevice graphicsDevice;
	private Robot robot;
	private IScreenCapture screenCaptureCallback;
	private boolean running = false;
	private Rectangle bounds;

	private ScreenCapture(GraphicsDevice screen, IScreenCapture screenCaptureCallback) throws AWTException {
		graphicsDevice = screen;
		this.screenCaptureCallback = screenCaptureCallback;
		robot = new Robot(screen);
		int revScale = 1;
		bounds = new Rectangle(
				  graphicsDevice.getDisplayMode().getWidth()/2-graphicsDevice.getDisplayMode().getWidth()/revScale/2
				, graphicsDevice.getDisplayMode().getHeight()/2-graphicsDevice.getDisplayMode().getHeight()/revScale/2
				, graphicsDevice.getDisplayMode().getWidth()/revScale
				, graphicsDevice.getDisplayMode().getHeight()/revScale);
	}
	
	public static ScreenCapture create(IScreenCapture screenCaptureCallback) throws AWTException {
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
		return create(graphicsDevice, screenCaptureCallback);
	}
	public static ScreenCapture create(GraphicsDevice screen, IScreenCapture screenCaptureCallback) throws AWTException {
		ScreenCapture screenCapture = new ScreenCapture(screen, screenCaptureCallback);
		return screenCapture;
	}
	public static ScreenCapture create(int screen, IScreenCapture screenCaptureCallback) throws AWTException {
		GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice graphicsDevice = graphicsDevices[screen];
		return create(graphicsDevice, screenCaptureCallback);
	}
	
	private BufferedImage captureImage() {
		return robot.createScreenCapture(bounds);
	}
	
	public void startCasting() {
		running = true;
		while(running) {
			BufferedImage frame = captureImage();
			screenCaptureCallback.onImage(frame);
		}
	}
	
	public void stop() {
		running = false;
	}
}
