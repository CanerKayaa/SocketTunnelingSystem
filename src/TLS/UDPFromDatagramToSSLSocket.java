package TLS;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class UDPFromDatagramToSSLSocket implements Runnable {
	String receivedData;
	DatagramSocket sslDatagramSocket;
	DatagramPacket inputPacket;
	Socket sslSocket;
	DataOutputStream outToData;
	int sequenceNumber;
	boolean isServer = true;
	ConcurrentHashMap<Integer, DatagramPacket> packetAddressMap;

	public UDPFromDatagramToSSLSocket(final DatagramSocket sslDatagramSocket, final Socket sslSocket)
			throws IOException {
		// TODO Auto-generated constructor stub
		this.sslDatagramSocket = sslDatagramSocket;
		this.sslSocket = sslSocket;
		outToData = new DataOutputStream(sslSocket.getOutputStream());
		this.isServer = true;
		packetAddressMap = null;
	}

	public UDPFromDatagramToSSLSocket setIsServer(final boolean isServer) {
		this.isServer = isServer;
		return this;
	}

	public UDPFromDatagramToSSLSocket setPacketAdressMap(
			final ConcurrentHashMap<Integer, DatagramPacket> packetAddressMap) {
		this.packetAddressMap = packetAddressMap;
		return this;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		sequenceNumber = 1;
		while (true) {
			try {
				/*
				 * I need to add sequence number to UDP Packets My sequence number protocol is
				 * #sq:'sequenceNumber'~~ at the end of the packets.
				 */
				final byte[] receivingDataBuffer = new byte[1024 * 1024 * 2]; // 2 MB
				inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);

				sslDatagramSocket.receive(inputPacket);

				receivedData = new String(inputPacket.getData(), inputPacket.getOffset(), inputPacket.getLength());

				System.out.println("Packet came from Datagram to SSLSocket : " + receivedData + "\n");

				receivedData = receivedData + "#sq:" + sequenceNumber + "~~";

				if (isServer && null != packetAddressMap) {
					this.packetAddressMap.put(sequenceNumber, inputPacket);
				}

				sequenceNumber++;

				outToData.write(receivedData.getBytes());

				outToData.flush();

				System.out.println("Seq.Num is added to packet. Sent to SSLSocket: " + receivedData + "\n");

			} catch (final Exception e) {
				// TODO: handle exception
			}
		}

	}
}
