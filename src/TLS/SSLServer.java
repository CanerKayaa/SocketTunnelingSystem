package TLS;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

public class SSLServer {

	public static void main(final String[] args) throws IOException, Exception {

		final ConfigHandler configHandler = new ConfigHandler("config.txt");
		final int listenPortOfServerTunnel = Integer.parseInt(configHandler.get("DestinationPort"));
		final int serverTunnelDestinationPort = Integer.parseInt(configHandler.get("ServerTunnelDestinationPort"));
		final String serverTunnelDestinationIP = configHandler.get("ServerTunnelDestinationIP");

		final ServerSocketFactory ssf = SSLServer.getServerSocketFactory("TLS");
		final ServerSocket ss = ssf.createServerSocket(listenPortOfServerTunnel);// destination port

		if (configHandler.get("Protocol").equals("UDP")) {

			final DatagramSocket sendDatagramSocket = new DatagramSocket();

			final Socket fromSocket = ss.accept();

			new TrayIconClass(listenPortOfServerTunnel, serverTunnelDestinationPort, true,
					configHandler.get("Protocol"));

			// There is same logic of routines as in SSLClient
			new Thread(new UDPFromSSLToDatagramSocket(fromSocket, sendDatagramSocket, serverTunnelDestinationIP,
					serverTunnelDestinationPort).setIsServer(false)).start();

			new Thread(new UDPFromDatagramToSSLSocket(sendDatagramSocket, fromSocket).setIsServer(false)).start();

		} else {
			while (true) {

				final Socket fromSocket = ss.accept();

				final Socket toSocket = new Socket(serverTunnelDestinationIP, serverTunnelDestinationPort);

				new TrayIconClass(listenPortOfServerTunnel, serverTunnelDestinationPort, true,
						configHandler.get("Protocol"));

				new Thread(new TCPSocketTransferHandler(fromSocket, toSocket)).start();

				new Thread(new TCPSocketTransferHandler(toSocket, fromSocket)).start();

			}
		}

	}

	private static ServerSocketFactory getServerSocketFactory(final String type) {
		if (type.equals("TLS")) {
			SSLServerSocketFactory ssf = null;
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

				ssf = ctx.getServerSocketFactory();
				return ssf;
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} else {
			return ServerSocketFactory.getDefault();
		}
		return null;
	}
}
