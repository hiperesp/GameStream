package br.com.hiperesp.gameStream.cast;

import br.com.hiperesp.gameStream.cast.message.Message;

public interface ICastReceive {
	public void onReceiveMessage(Message message);
}
