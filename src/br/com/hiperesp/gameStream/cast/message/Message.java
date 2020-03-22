package br.com.hiperesp.gameStream.cast.message;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import br.com.hiperesp.gameStream.cast.capture.Util;
import br.com.hiperesp.gameStream.cast.protocol.IProtocol;
import br.com.hiperesp.gameStream.cast.protocol.Protocol;

public class Message implements IProtocol {
	
	private Header type;
	private byte[] message;
	
	private Message(Header type, byte[] data) {
		this.type = type;
		this.message = data;
	}
	
	public static Message create(Header type, byte[] message) {
		return new Message(type, message);
	}
	
	public static Message create(Header type, byte[][] message) {
		MessageBuilder messageBuilder = new MessageBuilder();
		for (byte[] messageChunk : message) {
			messageBuilder.append(messageChunk);
		}
		return messageBuilder.build();
	}
	
	public static Message create(Header type) {
		return Message.create(type, new byte[] {});
	}
	
	public static Message create(BufferedImage image, String encoding) throws IOException {
		return create(Header.DATA_IMAGE, Util.getImageByteArray(image, encoding));
	}
	
	public byte[][] getMessageList(int bufferSize) {
		int headersLength = DATA_BYTE_SIZE+CHUNK_BYTE_SIZE+MESSAGE_LENGTH_BYTE_SIZE;
		int chunkCoundNeed = (int)Math.ceil((double)(message.length)/(bufferSize-headersLength));
		if(chunkCoundNeed==0) {
			chunkCoundNeed+=1;
		}
		byte[][] newMessage = new byte[chunkCoundNeed][];
		byte[] dataHeader = Protocol.getProtocolDataHeader(type);
		for(int i=0; i<newMessage.length; i++) {
			newMessage[i] = new byte[bufferSize];
			byte[] chunkHeader;
			if(newMessage.length-1==i) { //ultimo ou único
				if(i==0) { //único
					chunkHeader = Protocol.getProtocolChunkHeader(Header.CHUNK_SINGLE);
				} else { //último
					chunkHeader = Protocol.getProtocolChunkHeader(Header.CHUNK_END);
				}
			} else if(i>0) { //meio
				chunkHeader = Protocol.getProtocolChunkHeader(Header.CHUNK_MIDDLE);
			} else { //primeiro
				chunkHeader = Protocol.getProtocolChunkHeader(Header.CHUNK_START);
			}
			
			int newMessagePosition = 0;
			
			System.arraycopy(dataHeader, 0, newMessage[i], newMessagePosition, DATA_BYTE_SIZE);
			newMessagePosition+= DATA_BYTE_SIZE;
			
			System.arraycopy(chunkHeader, 0, newMessage[i], newMessagePosition, CHUNK_BYTE_SIZE);
			newMessagePosition+= CHUNK_BYTE_SIZE;
			
			int currentBufferDataSize = bufferSize-headersLength;
			if(newMessage.length-1==i) {
				currentBufferDataSize = message.length%currentBufferDataSize;
			}
			byte[] messageLength = ByteBuffer.allocate(MESSAGE_LENGTH_BYTE_SIZE).putChar((char)currentBufferDataSize).array();
			System.arraycopy(messageLength, 0, newMessage[i], newMessagePosition, MESSAGE_LENGTH_BYTE_SIZE);
			newMessagePosition+= MESSAGE_LENGTH_BYTE_SIZE;

			System.arraycopy(message, i*(bufferSize-headersLength), newMessage[i], newMessagePosition, currentBufferDataSize);
		}
		return newMessage;
	}
	
	public byte[] getRawMessage() {
		return this.message;
	}
	
	public boolean is(Header header) {
		return type==header;
	}
	
	public BufferedImage getImageMessage() throws IOException {
		return Util.createImageFromByteArray(message);
	}
}
