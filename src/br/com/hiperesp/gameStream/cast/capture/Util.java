package br.com.hiperesp.gameStream.cast.capture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Util {
	
	public static byte[] getImageByteArray(BufferedImage image, String encoding) throws IOException {
		long start = System.nanoTime();
		
		image = convertImage(image);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(image, encoding, outputStream);
		byte[] message = outputStream.toByteArray();
		
		System.out.println("Encoding time: "+(System.nanoTime()-start)/1_000_000+"ms");
		return message;
	}
	
	public static BufferedImage createImageFromByteArray(byte[] data) throws IOException {
		return ImageIO.read(new ByteArrayInputStream(data));
	}

	public static BufferedImage convertImage(BufferedImage originalImage) {
		/*
	    BufferedImage newImage = new BufferedImage(originalImage.getWidth(), 
	            originalImage.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
	    
	    Graphics graphics = newImage.getGraphics();
	    graphics.drawImage(originalImage, 0, 0, null);
	    graphics.dispose();
	    
	    return newImage;*/
		return originalImage;
	}
}
