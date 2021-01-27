package TLS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ConfigHandler extends HashMap<String, String> {
	String file;

	public ConfigHandler(final String file) throws IOException {
		this.file = file;
		readConfig(file);
	}

	private void readConfig(final String file) throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;

		new StringBuilder();
		System.getProperty("line.separator");

		try {

			while ((line = reader.readLine()) != null) {
				final String[] splitLine = line.split("=");
				put(splitLine[0], splitLine[1]);
			}

		} finally {
			reader.close();
		}
	}

}
