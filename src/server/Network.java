package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Network implements Runnable{
	HashMap<String, Long> clientIP = new HashMap<>();
	public Map<String, String> parameters;
	String logtag="";
	
	
	public Network(Map<String, String> parameters, String logtag) {
		this.parameters = parameters;
		this.logtag = logtag;
	}

	@Override
	public void run() {
		try {
			ServerSocket listenSocket = new ServerSocket(Integer.parseInt(parameters.get(Commands.port)));
			new Thread(new InteractionThread(parameters.get(Commands.exchangeinterval), parameters.get(Commands.debug).equals("Y"))).start();
			while (true) {
				Socket clientSocket = listenSocket.accept();
				
				String ip = clientSocket.getInetAddress().getHostAddress();
				Long currentTime = System.currentTimeMillis();
				if (clientIP.containsKey(ip)) {
					if (currentTime - clientIP.get(ip) < 1000*Long.parseLong(parameters.get(Commands.connectionintervallimit))) {
						LogUtils.initLogger(logtag).log("reject a connection from " +ip, true);
						continue;
					}
				}
				clientIP.put(ip, currentTime);

				
				ConnectionThread connectionThread = new ConnectionThread(clientSocket);
				ThreadPoolManager.init().submitThread(connectionThread);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
