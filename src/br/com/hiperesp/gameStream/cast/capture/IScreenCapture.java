package br.com.hiperesp.gameStream.cast.capture;

import java.awt.image.BufferedImage;

public interface IScreenCapture {
	public abstract void onImage(BufferedImage image);
}
