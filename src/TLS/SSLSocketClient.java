package TLS;

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketClient {

	public static void main(final String[] args) throws Exception {

		final Properties systemProps = System.getProperties();

		systemProps.put("javax.net.ssl.trustStore", "keystore.ImportKey");

		final ConfigHandler configHandler = new ConfigHandler("config.txt");
		final int listenPort = Integer.parseInt(configHandler.get("ListenPort"));
		final int destinationPort = Integer.parseInt(configHandler.get("DestinationPort"));
		final String destinationIP = configHandler.get("DestinationIP");

		System.out.println(configHandler.get("Protocol") + " packets will be transmit.");

		if (configHandler.get("Protocol").equals("UDP")) {

			final DatagramSocket sslClientDatagramSocket = new DatagramSocket(listenPort,
					InetAddress.getByName("localhost"));

			final SSLSocketFactory factory = SSLSocketClient.getSSLSocketFactory("TLS");

			final SSLSocket sslSocket = (SSLSocket) factory.createSocket(destinationIP, destinationPort);

			sslSocket.startHandshake(); // Handshake of SSL tunnel peers.
			System.out.println("HANDSHAKE is completed between tunnels.");

			// We need to buffer datagramPackets and its seq. numbers.
			final ConcurrentHashMap<Integer, DatagramPacket> packetAddressMap = new ConcurrentHashMap<Integer, DatagramPacket>();

			new TrayIconClass(listenPort, destinationPort, false, configHandler.get("Protocol"));

			// A thread routine for packet which incoming from datagramSocket and outgoing
			// to SSL Socket.
			new Thread(new UDPFromDatagramToSSLSocket(sslClientDatagramSocket, sslSocket).setIsServer(true)
					.setPacketAdressMap(packetAddressMap)).start();

			// A thread routine for packet which incoming from SSL Socket and outgoing to
			// DatagramSocket.
			new Thread(new UDPFromSSLToDatagramSocket(sslSocket, sslClientDatagramSocket, destinationIP, listenPort)
					.setIsServer(true).setPacketAdressMap(packetAddressMap)).start();

		} else {

			final ServerSocket welcomeSocket = new ServerSocket(listenPort); // listen port

			while (true) {

				final Socket socket = welcomeSocket.accept();
				final SSLSocketFactory factory = SSLSocketClient.getSSLSocketFactory("TLS");
				final SSLSocket sslSocket = (SSLSocket) factory.createSocket(destinationIP, destinationPort);

				sslSocket.startHandshake();
				System.out.println("HANDSHAKE is completed between tunnels.");

				new TrayIconClass(listenPort, destinationPort, false, configHandler.get("Protocol"));

				// One thread routine is enough for incoming and outgoing packets in TCP.
				new Thread(new TCPSocketTransferHandler(socket, sslSocket)).start();

				new Thread(new TCPSocketTransferHandler(sslSocket, socket)).start();

			}
		}

	}

	public static SSLSocketFactory getSSLSocketFactory(final String type) {
		if (type.equals("TLS")) {
			SocketFactory ssf = null;
			try {
				SSLContext ctx;
				KeyManagerFactory kmf;
				KeyStore ks;
				final char[] passphrase = "importkey".toCharArray();

				ctx = SSLContext.getInstance("TLS");
				kmf = KeyManagerFactory.getInstance("SunX509");
				ks = KeyStore.getInstance("JKS");

				ks.load(new FileInputStream("keystore.ImportKey"), passphrase);
				kmf.init(ks, passphrase);

				ctx.init(kmf.getKeyManagers(), null, null);

				ssf = ctx.getSocketFactory();
				return (SSLSocketFactory) ssf;
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			return (SSLSocketFactory) SSLSocketFactory.getDefault();
		}
		return null;
	}
}
