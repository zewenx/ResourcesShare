package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.net.SocketFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class Server {

	Map<String, String> parameters = new HashMap<String, String>();

	Options options;

	public Server() {
		this.options = new Options();
		parameters.put(Commands.port, "8888");
		parameters.put(Commands.advertisedhostname, "127.0.0.1");
		parameters.put(Commands.exchangeinterval, "600000");
		parameters.put(Commands.secret, "asdgasdfgasdfga");
		parameters.put(Commands.connectionintervallimit, "1000");
		parameters.put(Commands.debug, "N");

		options.addOption(Commands.advertisedhostname, true, "advertised hostname");
		options.addOption(Commands.connectionintervallimit, true, "connection interval limit in seconds");
		options.addOption(Commands.exchangeinterval, true, "exchange interval in seconds");
		options.addOption(Commands.port, true, "server port, an integer");
		options.addOption(Commands.secret, true, "secret");
		options.addOption(Commands.debug, false, "print debug information");
	}

	public static void main(String[] args) {
		new Server().start(args);
	}

	private void start(String[] args) {
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine commands = parser.parse(options, args);

			// Commands commandsObject = new Commands();
			// for(java.lang.reflect.Field field :
			// Commands.class.getDeclaredFields()){
			// String command = (String) field.get(commandsObject);
			// parameters.put(command, commands.getOptionValue(command));
			// }

			if (commands.hasOption(Commands.port)) {
				parameters.put(Commands.port, commands.getOptionValue(Commands.port));
			}
			if (commands.hasOption(Commands.advertisedhostname)) {
				parameters.put(Commands.advertisedhostname, commands.getOptionValue(Commands.advertisedhostname));
			}
			if (commands.hasOption(Commands.secret)) {
				parameters.put(Commands.secret, commands.getOptionValue(Commands.secret));
			}
			if (commands.hasOption(Commands.connectionintervallimit)) {
				parameters.put(Commands.connectionintervallimit,
						commands.getOptionValue(Commands.connectionintervallimit));
			}
			if (commands.hasOption(Commands.exchangeinterval)) {
				parameters.put(Commands.exchangeinterval, commands.getOptionValue(Commands.exchangeinterval));
			}
			if (commands.hasOption(Commands.debug)) {
				parameters.put(Commands.debug, "Y");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startServer();
	}

	private void startServer() {
		// TODO Auto-generated method stub

		try {
			ServerSocket listenSocket = new ServerSocket(Integer.parseInt(parameters.get(Commands.port)));
			while (true) {
				Socket clientSocket = listenSocket.accept();
				ConnectionThread connectionThread = new ConnectionThread(clientSocket);
				ThreadPoolManager.init().submitThread(connectionThread);
				Thread.sleep(Long.parseLong(parameters.get(Commands.connectionintervallimit)));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
