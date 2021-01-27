package TLS;

import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class TrayIconClass {
	public TrayIcon trayIconClient;
	public TrayIcon trayIconServer;

	public TrayIconClass(final int fromPort, final int toPort, final boolean isServer, final String connectionType) {
		// TODO Auto-generated constructor stub
		if (isServer) {
			showServer(fromPort, toPort, connectionType);
		} else {
			showClient(fromPort, toPort, connectionType);
		}

	}

	public void showClient(final int fromPort, final int toPort, final String connectionType) {
		if (!SystemTray.isSupported()) {
			System.exit(0);

		}
		trayIconClient = new TrayIcon(createIcon("/TLS/Transport-Tunnel-icon.png", "Icon"));

		trayIconClient.setToolTip("Socket Tunnel Client");

		final SystemTray tray = SystemTray.getSystemTray();

		final PopupMenu menu = new PopupMenu();

		final MenuItem aboutItem = new MenuItem("About");

		final MenuItem exitItem = new MenuItem("Exit");

		final MenuItem systemInfoItem = new MenuItem("Client Tunnel Info");

		final Menu connectionTypeItem = new Menu("Connection Type");

		final MenuItem connectionItem = new Menu(connectionType);

		menu.add(connectionTypeItem);
		connectionTypeItem.add(connectionItem);

		menu.addSeparator();

		menu.add(systemInfoItem);

		menu.addSeparator();

		menu.add(aboutItem);

		menu.addSeparator();

		menu.add(exitItem);

		trayIconClient.setPopupMenu(menu);

		try {
			tray.add(trayIconClient);
		} catch (final Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// TODO Auto-generated method stub
				final String about = "Developed by Caner Kaya\n20160702062";
				JOptionPane.showMessageDialog(null, about);
			}
		});

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				tray.remove(trayIconClient);
				System.exit(0);
			}
		});

		systemInfoItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// TODO Auto-generated method stub
				final String systemInfo = "CLIENT TUNNEL INFO\nIncoming from port : " + fromPort + " "
						+ "\nOutgoing to port : " + toPort;
				JOptionPane.showMessageDialog(null, systemInfo);
			}
		});

	}

	public void showServer(final int fromPort, final int toPort, final String connectionType) {
		if (!SystemTray.isSupported()) {
			System.exit(0);
		}

		trayIconServer = new TrayIcon(createIcon("/TLS/Transport-Tunnel-icon.png", "Icon"));
		trayIconServer.setToolTip("Socket Tunnel Server");
		final SystemTray tray = SystemTray.getSystemTray();

		final PopupMenu menu = new PopupMenu();

		final MenuItem aboutItem = new MenuItem("About");

		final MenuItem exitItem = new MenuItem("Exit");

		final MenuItem systemInfoItem = new MenuItem("Server Tunnel Info");

		final Menu connectionTypeItem = new Menu("Connection Type");

		final MenuItem connectionItem = new Menu(connectionType);

		menu.add(connectionTypeItem);
		connectionTypeItem.add(connectionItem);
		menu.addSeparator();

		menu.add(systemInfoItem);

		menu.addSeparator();

		menu.add(aboutItem);

		menu.addSeparator();

		menu.add(exitItem);

		trayIconServer.setPopupMenu(menu);
		try {
			tray.add(trayIconServer);
		} catch (final Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// TODO Auto-generated method stub
				final String about = "Developed by Caner Kaya\n20160702062";
				JOptionPane.showMessageDialog(null, about);
			}
		});

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				tray.remove(trayIconClient);
				System.exit(0);
			}
		});

		systemInfoItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// TODO Auto-generated method stub
				final String systemInfo = "SERVER TUNNEL INFO\nIncoming from port : " + fromPort + " "
						+ "\nOutgoing to port : " + toPort;
				JOptionPane.showMessageDialog(null, systemInfo);
			}
		});
	}

	protected static Image createIcon(final String path, final String desc) {
		final URL imageURL = TrayIconClass.class.getResource(path);
		return (new ImageIcon(imageURL, desc)).getImage();
	}

}
