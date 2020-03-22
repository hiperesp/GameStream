package br.com.hiperesp.gameStream.cast.protocol;

public interface IProtocol {
	
	public static enum Header {
		PING, PONG, PONG_OK, AUTHENTICATE, AUTHENTICATED, UNAUTHENTICATED, GOODBYE,

		DATA_TEXT, DATA_IMAGE,
		
		CHUNK_START, CHUNK_MIDDLE, CHUNK_END, CHUNK_SINGLE
	}
	
	public static final int DATA_BYTE_SIZE = 1;
	public static final Protocol[] DATA = new Protocol[] {
			
			new Protocol(Header.DATA_TEXT,		new byte[] {0x00}),
			new Protocol(Header.DATA_IMAGE,		new byte[] {0x01}),

			new Protocol(Header.GOODBYE, 		new byte[] {0x70}),
			new Protocol(Header.PING,			new byte[] {0x71}),
			new Protocol(Header.PONG,			new byte[] {0x72}),
			new Protocol(Header.PONG_OK,		new byte[] {0x73}),
			new Protocol(Header.AUTHENTICATE,	new byte[] {0x74}),
			new Protocol(Header.AUTHENTICATED,	new byte[] {0x75}),
			new Protocol(Header.UNAUTHENTICATED,new byte[] {0x76}),
	};
	public static final int CHUNK_BYTE_SIZE = 1;
	public static final Protocol[] CHUNK = new Protocol[] {
			new Protocol(Header.CHUNK_SINGLE,	new byte[] {0x00}),
			new Protocol(Header.CHUNK_START,	new byte[] {0x01}),
			new Protocol(Header.CHUNK_MIDDLE,	new byte[] {0x02}),
			new Protocol(Header.CHUNK_END,		new byte[] {0x03}),
	};
	public static final int MESSAGE_LENGTH_BYTE_SIZE = 2;
}
