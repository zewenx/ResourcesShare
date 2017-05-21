package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SecureNetwork implements Runnable {
	HashMap<String, Long> clientIP = new HashMap<>();
	public static Map<String, String> parameters;
	String logtag = "";

	public SecureNetwork(Map<String, String> parameters, String logtag) {
		this.parameters = parameters;
		this.logtag = logtag;
	}

	@Override
	public void run() {
		try {
			// Create SSL server socket
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory
					.createServerSocket(Integer.parseInt(parameters.get(Commands.sport)));

			// TODO
			new Thread(new InteractionThread(parameters.get(Commands.exchangeinterval),
					parameters.get(Commands.debug).equals("Y"))).start();
			// Accept client connection
			while (true) {
				SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();

				String ip = sslsocket.getInetAddress().getHostAddress();
				Long currentTime = System.currentTimeMillis();
				if (clientIP.containsKey(ip)) {
					if (currentTime - clientIP.get(ip) < 1000
							* Long.parseLong(parameters.get(Commands.connectionintervallimit))) {
						LogUtils.initLogger(logtag).log("reject a connection from " + ip, true);
						continue;
					}
				}
				clientIP.put(ip, currentTime);

				ConnectionThread connectionThread = new ConnectionThread(sslsocket);
				ThreadPoolManager.init().submitThread(connectionThread);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

}
