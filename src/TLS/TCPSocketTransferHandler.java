package TLS;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TCPSocketTransferHandler implements Runnable {
	Socket fromSocket;
	Socket toSocket;
	DataInputStream inFromData;
	DataOutputStream outToData;

	public TCPSocketTransferHandler(final Socket fromSocket, final Socket toSocket) throws Exception {
		this.fromSocket = fromSocket;
		this.toSocket = toSocket;

		inFromData = new DataInputStream(fromSocket.getInputStream());
		outToData = new DataOutputStream(toSocket.getOutputStream());

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			int data;
			// Send the incoming packet as it is to the corresponding socket.
			while ((data = inFromData.read()) != -1) {
				outToData.write(data);
				outToData.flush();
			}
		} catch (final Exception e) {
			// TODO: handle exception
		}
	}

}
