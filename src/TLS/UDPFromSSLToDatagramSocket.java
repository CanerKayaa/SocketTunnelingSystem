package TLS;

import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class UDPFromSSLToDatagramSocket implements Runnable {
	Socket fromSocket;
	DatagramSocket datagramSocket;
	String destinationIP;
	int listenPort;
	int lastSequenceNumber = 0;
	DataInputStream inFromData;
	DatagramPacket sendingPacket;
	Queue<UDPPacket> packetQueue = new PriorityQueue<>();
	boolean isServer = true;
	ConcurrentHashMap<Integer, DatagramPacket> packetAddressMap;

	public UDPFromSSLToDatagramSocket(final Socket fromSocket, final DatagramSocket datagramSocket,
			final String destinationIP, final int listenPort) throws Exception {
		this.fromSocket = fromSocket;
		this.datagramSocket = datagramSocket;
		this.destinationIP = destinationIP;
		this.listenPort = listenPort;
		this.packetAddressMap = null;
		inFromData = new DataInputStream(fromSocket.getInputStream());
	}

	public UDPFromSSLToDatagramSocket setIsServer(final boolean isServer) {
		this.isServer = isServer;
		return this;
	}

	public UDPFromSSLToDatagramSocket setPacketAdressMap(
			final ConcurrentHashMap<Integer, DatagramPacket> packetAddressMap) {
		this.packetAddressMap = packetAddressMap;
		return this;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {

				final byte[] receivingData = new byte[1024 * 1024 * 2];

				int data;
				int i = 0;

				while ((data = inFromData.read()) != -1) {

					receivingData[i++] = (byte) data;

					// End of the UDP packet.
					if (i > 1 && receivingData[i - 1] == (byte) '~' && receivingData[i - 2] == (byte) '~') {

						System.out.println(
								"Packet came from SSL to DatagramSocket " + new String(receivingData, 0, i) + "\n");

						final String[] splitId = new String(receivingData, 0, i).split("#sq:");
						if (splitId.length == 2) {

							final String sendData = splitId[0];
							String seqNumberString = splitId[1];

							if (seqNumberString != null) {
								seqNumberString = seqNumberString.substring(0, seqNumberString.length() - 2);
								final int sequenceNumber = Integer.parseInt(seqNumberString);

								final byte[] forwardData = sendData.getBytes();

								// There is no unorder issue.
								if (lastSequenceNumber + 1 == sequenceNumber) {
									sendingPacket = createDatagramPacket(forwardData, sequenceNumber);

									datagramSocket.send(sendingPacket);

									System.out.println("Packet send to appropriate DatagramSocket : "
											+ new String(sendingPacket.getData()) + "\n");
									lastSequenceNumber++;
								} else {
									// There is unorder issue. Buffered unorder packets in Priotiy Queue.
									packetQueue.add(new UDPPacket(sequenceNumber, forwardData));
								}
								i = 0;
								if (!packetQueue.isEmpty()) {
									int index = packetQueue.peek().getSequenceNumber();
									while (index == lastSequenceNumber + 1) {
										final UDPPacket packet = packetQueue.poll();
										datagramSocket.send(
												createDatagramPacket(packet.getMessage(), packet.getSequenceNumber()));
										lastSequenceNumber = packet.getSequenceNumber();

										if (!packetQueue.isEmpty()) {
											index = packetQueue.peek().getSequenceNumber();
										}
									}
								}
							}
						}
					}
				}

			} catch (final Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

	private DatagramPacket createDatagramPacket(final byte[] data, final int sequenceNumber) {
		DatagramPacket datagramPacket = null;
		try {
			if (!this.isServer) {
				datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(destinationIP),
						listenPort);
			} else {

				if (packetAddressMap.containsKey(sequenceNumber)) {
					final DatagramPacket packet = packetAddressMap.remove(sequenceNumber);
					datagramPacket = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
				}
			}
		} catch (final Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return datagramPacket;
	}

}
