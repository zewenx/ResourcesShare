package EZShare;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.print.attribute.standard.PrinterMessageFromOperator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xpath.internal.operations.And;

import VO.AbstractVO;
import VO.ExchangeVO;
import VO.FetchVO;
import VO.RemoveVO;
import VO.RequestVO;
import VO.ResourceVO;
import VO.ServerVO;
import VO.ShareVO;
import VO.PublishVO;
import VO.QueryVO;
import VO.SpecialResourceVO;
import VO.SubscribeVO;
import client.ConnectionThread;
import client.Network;
import client.OptionInit;
import javafx.scene.effect.FloatMap;
import server.Commands;
import server.LogUtils;
import sun.management.counter.perf.PerfInstrumentation;
import sun.util.logging.resources.logging;

public class Client {

	Map<String, String> parameters = new HashMap<String, String>();
	Options options;
	boolean debug = false;
	boolean isSecureConnection = false;
	public static final String logtag = "Client.log";
	Network network;

	public Client() {


//		options.addOption(Commands.subscribe, false, "subscribe to query responses of the server");
//		options.addOption(Commands.unsubscribe, true, "unsubscribe from query responses of the server");

//		options.addOption(Commands.id, false, "subscription id");
		init();
	}

	private void init() {
		this.options = new Options();
		new OptionInit().initOptions(options);
		parameters.put(Commands.port, "3780");
		parameters.put(Commands.host, "127.0.0.1");
	}

	public static void main(String[] args) {
		new Client().start(args);
	}

	public void start(String[] args) {
		CommandLineParser parser = new DefaultParser();
		try {
			
			CommandLine commands = parser.parse(options, args);

			if (commands.hasOption(Commands.help)) {
				new HelpFormatter().printHelp("java -cp ezshare.jar EZShare.Client", options);
				return;
			}


			if (commands.hasOption(Commands.secure)) {
				this.isSecureConnection = true;
			}

			if (commands.hasOption(Commands.port)) {
				parameters.put(Commands.port, commands.getOptionValue(Commands.port));
			}

			if (commands.hasOption(Commands.host)) {
				parameters.put(Commands.host, commands.getOptionValue(Commands.host));
			}

			if (commands.hasOption(Commands.debug)) {
				debug = true;
			}
			
			network = new Network(parameters, debug, logtag,isSecureConnection);
			

			if (commands.hasOption(Commands.publish)) {
				publishCommand(commands);
			} else if(commands.hasOption(Commands.subscribe)) {
				subscribeCommand(commands);
			} else if(commands.hasOption(Commands.unsubscribe)) {
				unsubscribeCommand(commands);
			} else if (commands.hasOption(Commands.exchange)) {
				exchangeCommand(commands);
			} else if (commands.hasOption(Commands.fetch)) {
				fetchCommand(commands);
			} else if (commands.hasOption(Commands.query)) {
				queryCommand(commands);
			} else if (commands.hasOption(Commands.remove)) {
				removeCommand(commands);
			} else if (commands.hasOption(Commands.share)) {
				shareCommand(commands);
			} else {

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void subscribeCommand(CommandLine commands){
		SubscribeVO vo = new SubscribeVO();
		System.out.println("here");
		vo.setCommand("SUBSCRIBE");
		vo.setID(commands.getOptionValue(Commands.id));
		
		// sets values for data resources
				ResourceVO resourceVO = new ResourceVO();
				resourceVO.setChannel(commands.getOptionValue(Commands.channel));
				resourceVO.setDescription(commands.getOptionValue(Commands.description));
				resourceVO.setName(commands.getOptionValue(Commands.name));
				resourceVO.setOwner(commands.getOptionValue(Commands.owner));
				String tags = commands.getOptionValue(Commands.tags);
				ArrayList<String> taglist = new ArrayList<String>();
				if (tags != null & tags != "") {
					for (String string : tags.split(",")) {
						taglist.add(string);
					}
				}
				resourceVO.setTags(taglist);
				resourceVO.setUri(commands.getOptionValue(Commands.uri));
				resourceVO.setEzserver(null);
				vo.setResource(resourceVO);
				
				//setup connection
				commandLog("subscribing to ");
				subscribe(vo);

				
	}
	
	private void unsubscribeCommand(CommandLine commands){
		
	}
	
	private void publishCommand(CommandLine commands) {
		PublishVO vo = new PublishVO();
		vo.setCommand("PUBLISH");

		// sets values for data resources
		ResourceVO resourceVO = new ResourceVO();
		resourceVO.setChannel(commands.getOptionValue(Commands.channel));
		resourceVO.setDescription(commands.getOptionValue(Commands.description));
		resourceVO.setName(commands.getOptionValue(Commands.name));
		resourceVO.setOwner(commands.getOptionValue(Commands.owner));
		String tags = commands.getOptionValue(Commands.tags);
		ArrayList<String> taglist = new ArrayList<String>();
		if (tags != null & tags != "") {
			for (String string : tags.split(",")) {
				taglist.add(string);
			}
		}
		resourceVO.setTags(taglist);
		resourceVO.setUri(commands.getOptionValue(Commands.uri));
		resourceVO.setEzserver(null);
		vo.setResource(resourceVO);

		commandLog("publishing to ");
		// waits for response from server, returned as a list of strings
		List<String> responseList = network.request(vo);
		String response = responseList.get(0);
		System.out.println(response);
	}

	private void queryCommand(CommandLine commands) {
		QueryVO vo = new QueryVO();
		vo.setCommand("QUERY");

		// sets values for data resources
		ResourceVO resourceVO = new ResourceVO();
		resourceVO.setChannel(commands.getOptionValue(Commands.channel));
		resourceVO.setDescription(commands.getOptionValue(Commands.description));
		resourceVO.setName(commands.getOptionValue(Commands.name));
		resourceVO.setOwner(commands.getOptionValue(Commands.owner));
		String tags = commands.getOptionValue(Commands.tags);
		ArrayList<String> taglist = new ArrayList<String>();
		if (tags != null & tags != "") {
			for (String string : tags.split(",")) {
				taglist.add(string);
			}
		}
		resourceVO.setTags(taglist);
		resourceVO.setUri(commands.getOptionValue(Commands.uri));
		resourceVO.setEzserver(null);
		vo.setResourceTemplate(resourceVO);

		vo.setRelay(true);

		commandLog("Querying to ");
		List<String> responseList = network.request(vo);
		String response = "";
		for (String string : responseList) {
			response += string + '\n';
		}
		System.out.println(response);
	}

	private void fetchCommand(CommandLine commands) {
		FetchVO vo = new FetchVO();
		vo.setCommand("FETCH");
		ResourceVO resourceVO = new ResourceVO();
		resourceVO.setChannel(commands.getOptionValue(Commands.channel));
		resourceVO.setDescription(commands.getOptionValue(Commands.description));
		resourceVO.setName(commands.getOptionValue(Commands.name));
		resourceVO.setOwner(commands.getOptionValue(Commands.owner));
		String tags = commands.getOptionValue(Commands.tags);
		ArrayList<String> taglist = new ArrayList<String>();
		if (tags != null & tags != "") {
			for (String string : tags.split(",")) {
				taglist.add(string);
			}
		}
		resourceVO.setTags(taglist);
		resourceVO.setUri(commands.getOptionValue(Commands.uri));
		resourceVO.setEzserver(null);
		vo.setResource(resourceVO);

		commandLog("fetching to ");

		Socket socket = null;
		try {

			// InetSocketAddress address = new
			// InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);
			InetSocketAddress address = new InetSocketAddress(parameters.get(Commands.host),
					Integer.parseInt(parameters.get(Commands.port)));
			socket = new Socket();
			socket.connect(address);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			LogUtils.initLogger(logtag).log(" SEND: " + vo.toJson(), debug);
			out.writeUTF(vo.toJson());
			out.flush();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String response = in.readUTF();

			LogUtils.initLogger(logtag).log(" RECEIVED: " + response, debug);
			System.out.println(response);

			if (response.contains("error")) {
				System.out.println(response);
				return;
			}
			SpecialResourceVO specialResourceVO = new Gson().fromJson(in.readUTF(), SpecialResourceVO.class);
			LogUtils.initLogger(logtag).log(" RECEIVED: " + specialResourceVO.toJson(), debug);

			File file = new File(new URI(specialResourceVO.getUri()));
			String fileName = file.getName();
			File dataFile = new File(fileName);
			if (dataFile.exists()) {
				dataFile.delete();
			}
			dataFile.createNewFile();
			byte[] datas = new byte[(int) specialResourceVO.getResourceSize() + 1];
			int count = 0;
			while (count < specialResourceVO.getResourceSize()) {
				count += in.read(datas, count, (int) (specialResourceVO.getResourceSize()) - count);
			}
			FileUtils.writeByteArrayToFile(dataFile, datas);

			LogUtils.initLogger(logtag).log(" RECEIVED: " + in.readUTF(), debug);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void removeCommand(CommandLine commands) {
		RemoveVO vo = new RemoveVO();
		vo.setCommand("REMOVE");
		ResourceVO resourceVO = new ResourceVO();
		resourceVO.setChannel(commands.getOptionValue(Commands.channel));
		resourceVO.setDescription(commands.getOptionValue(Commands.description));
		resourceVO.setName(commands.getOptionValue(Commands.name));
		resourceVO.setOwner(commands.getOptionValue(Commands.owner));
		String tags = commands.getOptionValue(Commands.tags);
		ArrayList<String> taglist = new ArrayList<String>();
		if (tags != null & tags != "") {
			for (String string : tags.split(",")) {
				taglist.add(string);
			}
		}
		resourceVO.setTags(taglist);
		resourceVO.setUri(commands.getOptionValue(Commands.uri));
		resourceVO.setEzserver(null);
		vo.setResource(resourceVO);

		commandLog("removing to ");

		List<String> responseList = network.request(vo);
		String response = responseList.get(0);
		System.out.println(response);
	}

	private void shareCommand(CommandLine commands) {

		ShareVO vo = new ShareVO();
		vo.setCommand("SHARE");
		vo.setSecret(commands.getOptionValue(Commands.secret));
		ResourceVO resourceVO = new ResourceVO();
		resourceVO.setChannel(commands.getOptionValue(Commands.channel));
		resourceVO.setDescription(commands.getOptionValue(Commands.description));
		// resourceVO.setEzserver(commands.getOptionValue(Commands.er));
		resourceVO.setName(commands.getOptionValue(Commands.name));
		resourceVO.setOwner(commands.getOptionValue(Commands.owner));

		String tags = commands.getOptionValue(Commands.tags);
		ArrayList<String> taglist = new ArrayList<String>();
		if (tags != null & tags != "") {
			for (String string : tags.split(",")) {
				taglist.add(string);
			}
		}
		resourceVO.setTags(taglist);
		resourceVO.setUri(commands.getOptionValue(Commands.uri));
		resourceVO.setEzserver(null);
		vo.setResource(resourceVO);

		commandLog("sharing to ");

		List responseList =network.request(vo);
		String response = (String) responseList.get(0);
		System.out.println(response);
	}

	private void exchangeCommand(CommandLine commands) {
		String services = commands.getOptionValue(Commands.servers);
		ExchangeVO vo = new ExchangeVO();
		vo.setCommand("EXCHANGE");
		String[] serversArray = services.split(",");
		List<ServerVO> serverList = new ArrayList<ServerVO>();
		for (String server : serversArray) {
			ServerVO serverVO = new ServerVO();
			String[] serversData = server.split(":");
			serverVO.setHostname(serversData[0]);
			serverVO.setPort(Integer.parseInt(serversData[1]));
			serverList.add(serverVO);
		}
		vo.setServerList(serverList);
		List<String> responseList = network.request(vo);

		commandLog("exchanging to ");
		String response = responseList.get(0);
		System.out.println(response);
	}

	void commandLog(String commandInfo) {
		LogUtils.initLogger(logtag).log("setting debug on", debug);
		LogUtils.initLogger(logtag)
				.log(commandInfo + parameters.get(Commands.host) + ":" + parameters.get(Commands.port), debug);
	}

	//subscribe to the server
	void subscribe(RequestVO vo){
		Thread connectionThread = null;
		// InetSocketAddress address = new
		// InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);
		InetSocketAddress address = null;
		if (defaultHost) {
			connectionThread = new Thread(new ConnectionThread(vo, debug));
			
		} else {
			connectionThread = new Thread(new ConnectionThread(parameters.get(Commands.host), Integer.parseInt(parameters.get(Commands.port)),vo, debug));//parameters.get(Commands.host), Integer.parseInt(parameters.get(Commands.port)
		}
		connectionThread.start();
	}
	
	List request(RequestVO vo) {
		Socket socket = null;
		List responseList = new ArrayList<String>();
		try {

			// InetSocketAddress address = new
			// InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);
			InetSocketAddress address = null;
			if (defaultHost) {
				address = new InetSocketAddress("127.0.0.1", 8888);
			} else {
				address = new InetSocketAddress(parameters.get(Commands.host), Integer.parseInt(parameters.get(Commands.port)));
			}
			socket = new Socket();
			socket.setSoTimeout(600000);
			socket.connect(address);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			LogUtils.initLogger(logtag).log(" SEND: " + vo.toJson(), debug);
			out.writeUTF(vo.toJson());
			out.flush();

			l: while (true) {

				String response = "";
				try {
					response = in.readUTF();
				} catch (Exception e) {
					e.printStackTrace();
					break l;
				}
				if (response.length() > 0) {
					responseList.add(response);
				}
				if (response.length() > 0)
					switch (vo.getCommand().toLowerCase()) {
					case "publish":
					case "remove":
					case "exchange":
					case "share":
						if (response.contains("response")) {
							break l;
						}
						break;
					case "query":
					case "fetch":
						if (response.contains("resultSize") || response.contains("error")) {
							break l;
						}
						break;					
					}

			}
			for (Object str : responseList) {
				if (str instanceof String) {
					LogUtils.initLogger(logtag).log(" RECEIVED: " + (String) str, debug);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return responseList;
	}
}
