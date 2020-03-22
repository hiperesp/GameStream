package br.com.hiperesp.gameStream.cast.message;

import java.nio.ByteBuffer;

import br.com.hiperesp.gameStream.cast.protocol.IProtocol;
import br.com.hiperesp.gameStream.cast.protocol.Protocol;

public class MessageBuilder implements IProtocol {

	private byte[] currentMessageType;
	private byte[][] currentMessage;
	private int fullMessageSize;
	private boolean ready = false;
	
	public MessageBuilder() {}
	
	public void append(byte[] data) {
		byte[] typeHeader = new byte[DATA_BYTE_SIZE];
		byte[] chunkHeader = new byte[CHUNK_BYTE_SIZE];
		byte[] messageLengthByte = new byte[MESSAGE_LENGTH_BYTE_SIZE];
		
		int messagePosition = 0;
		
		System.arraycopy(data, messagePosition, typeHeader, 0, DATA_BYTE_SIZE);
		messagePosition+= DATA_BYTE_SIZE;
		
		System.arraycopy(data, messagePosition, chunkHeader, 0, CHUNK_BYTE_SIZE);
		messagePosition+= CHUNK_BYTE_SIZE;
		
		System.arraycopy(data, messagePosition, messageLengthByte, 0, MESSAGE_LENGTH_BYTE_SIZE);
		messagePosition+= MESSAGE_LENGTH_BYTE_SIZE;
		int messageLength = (int)ByteBuffer.wrap(messageLengthByte).getChar();
		byte[] message = new byte[messageLength];
		
		System.arraycopy(data, messagePosition, message, 0, messageLength);
		
		boolean isSingle = Protocol.compareChunkHeader(Header.CHUNK_SINGLE, chunkHeader);
		boolean isEnd = Protocol.compareChunkHeader(Header.CHUNK_END, chunkHeader);
		if(isSingle||Protocol.compareChunkHeader(Header.CHUNK_START, chunkHeader)) {
			currentMessage = new byte[1][];
			currentMessage[0] = message;
			currentMessageType = typeHeader;
		} else if(isEnd||Protocol.compareChunkHeader(Header.CHUNK_MIDDLE, chunkHeader)) {
			if(currentMessage==null) {
				return;
			}
			byte[][] newCurrentMessage = new byte[currentMessage.length+1][];
			for(int i=0; i<currentMessage.length; i++) {
				newCurrentMessage[i] = currentMessage[i];
			}
			newCurrentMessage[currentMessage.length] = message;
			currentMessage = newCurrentMessage;
		}
		fullMessageSize+= message.length;
		if(isSingle||isEnd) {
			ready = true;
		}
	}
	
	public boolean isDone() {
		return ready;
	}
	
	public Message build() {
		byte[] message = new byte[fullMessageSize];
		int lastMessageChunkIndex = 0;
		for(int i=0; i<currentMessage.length; i++) {
			System.arraycopy(currentMessage[i], 0, message, lastMessageChunkIndex, currentMessage[i].length);
			lastMessageChunkIndex+= currentMessage[i].length;
		}
		return Message.create(Protocol.getProtocolDataHeader(currentMessageType), message);
	}
	
}
