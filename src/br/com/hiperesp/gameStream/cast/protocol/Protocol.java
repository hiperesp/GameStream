package br.com.hiperesp.gameStream.cast.protocol;

public class Protocol implements IProtocol {
	
	private Header key;
	private byte[] value;
	
	public Protocol(Header key, byte[] value) {
		this.key = key;
		this.value = value;
	}
	
	public boolean compareKey(Header key) {
		return this.key==key;
	}
	
	public boolean compareValue(byte[] value) {
		return isByteArrayEquals(this.value, value);
	}

	public Header getKey() {
		return this.key;
	}
	public byte[] getValue() {
		return this.value;
	}
	
	public static boolean isByteArrayEquals(byte[] bArr1, byte[] bArr2) {
		if(bArr1.length!=bArr2.length) {
			return false;
		}
		for(int i=0; i<bArr1.length; i++) {
			if(bArr1[i]!=bArr2[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean compareChunkHeader(Header header, byte[] dataArray) {
		byte[] headerArray = Protocol.getProtocolChunkHeader(header);
		if(headerArray.length!=dataArray.length) {
			return false;
		}
		for(int i=0; i<headerArray.length; i++) {
			if(headerArray[i]!=dataArray[i]) {
				return false;
			}
		}
		return true;
	}
	public static Header getProtocolDataHeader(byte[] data) {
		for(int i=0; i<DATA.length; i++) {
			if(DATA[i].compareValue(data)) {
				return DATA[i].getKey();
			}
		}
		return null;
	}
	public static byte[] getProtocolChunkHeader(Header chunk) {
		for(int i=0; i<CHUNK.length; i++) {
			if(CHUNK[i].compareKey(chunk)) {
				return CHUNK[i].getValue();
			}
		}
		return null;
	}
	public static byte[] getProtocolDataHeader(Header data) {
		for(int i=0; i<DATA.length; i++) {
			if(DATA[i].compareKey(data)) {
				return DATA[i].getValue();
			}
		}
		return null;
	}
	/* 
	public static Header getProtocolChunkHeader(byte[]chunk) {
		for(int i=0; i<CHUNK.length; i++) {
			if(CHUNK[i].compareValue(chunk)) {
				return CHUNK[i].getKey();
			}
		}
		return null;
	}
	public static boolean compareDataHeader(Header header, byte[] dataArray) {
		byte[] headerArray = getProtocolDataHeader(header);
		if(headerArray.length!=dataArray.length) {
			return false;
		}
		for(int i=0; i<headerArray.length; i++) {
			if(headerArray[i]!=dataArray[i]) {
				return false;
			}
		}
		return true;
	}
	 */
	
}
