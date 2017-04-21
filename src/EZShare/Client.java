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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.And;

import VO.AbstractVO;
import VO.ExchangeVO;
import VO.FetchVO;
import VO.RemoveVO;
import VO.ResourceVO;
import VO.ServerVO;
import VO.ShareVO;
import VO.PublishVO;
import VO.SpecialResourceVO;
import javafx.scene.effect.FloatMap;
import server.Commands;
import server.LogUtils;
import sun.util.logging.resources.logging;

public class Client {

	// -channel <arg> channel
	// -debug print debug information
	// -description <arg> resource description
	// -exchange exchange server list with server
	// -fetch fetch resources from server
	// -host <arg> server host, a domain name or IP address
	// -name <arg> resource name
	// -owner <arg> owner
	// -port <arg> server port, an integer
	// -publish publish resource on servers
	// -query query for resources from server
	// -remove remove resource from server
	// -secret <arg> secret
	// -servers <arg> server list, host1:port1,host2:port2,...
	// -share share resource on server
	// -tags <arg> resource tags, tag1,tag2,tag3,...
	// -uri <arg> resource URI

	Map<String, String> parameters = new HashMap<String, String>();
	Options options;
	boolean debug = false;
	boolean defaultHost = true;
	public static final String logtag = "Client.log";

	public Client() {
		this.options = new Options();

		options.addOption(Commands.channel, true, "channel");
		options.addOption(Commands.debug, false, "print debug information");
		options.addOption(Commands.description, true, "resource description");
		options.addOption(Commands.exchange, false, "exchange server list with server");
		options.addOption(Commands.secret, true, "secret");
		options.addOption(Commands.port, true, "server port, an integer");
		options.addOption(Commands.fetch, false, "fetch resources from server");
		options.addOption(Commands.host, true, "server host, a domain name or IP address");
		options.addOption(Commands.name, true, "resource name");
		options.addOption(Commands.owner, true, "owner");
		options.addOption(Commands.publish, false, "publish resource on server");
		options.addOption(Commands.query, false, "query for resources from server");
		options.addOption(Commands.remove, false, "remove resource from server");
		options.addOption(Commands.servers, true, "server list, host1:port1,host2:port2,...");
		options.addOption(Commands.share, false, "share resource on server");
		options.addOption(Commands.tags, true, "resource tags, tag1,tag2,tag3,...");
		options.addOption(Commands.uri, true, "resource URI");

		options.addOption(Commands.help, false, "help");
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

			if (commands.hasOption(Commands.port) && commands.hasOption(Commands.host)) {
				defaultHost = false;
				parameters.put(Commands.port, commands.getOptionValue(Commands.port));
				parameters.put(Commands.host, commands.getOptionValue(Commands.host));
			}

			if (commands.hasOption(Commands.debug)) {
				debug = true;
			}

			if (commands.hasOption(Commands.publish)) {
				publishCommand(commands);
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
		List<String> responseList = request(vo);
		String response = responseList.get(0);
		System.out.println(response);
	}
	
	private void queryCommand(CommandLine commands){
		
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
			InetSocketAddress address = null;
			if (defaultHost) {
				address = new InetSocketAddress("127.0.0.1", 8888);
			} else {
				address = new InetSocketAddress(parameters.get(Commands.host), Integer.parseInt(parameters.get(Commands.port)));
			}
			socket = new Socket();
			socket.connect(address);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			LogUtils.initLogger(logtag).log(" SEND: "+vo.toJson(), debug);
			out.writeUTF(vo.toJson());
			out.flush();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String response = in.readUTF();
			
			LogUtils.initLogger(logtag).log(" RECEIVED: "+ response, debug);
			
			if (response.contains("error")) {
				System.out.println(response);
				return;
			}
			SpecialResourceVO specialResourceVO = new Gson().fromJson(in.readUTF(), SpecialResourceVO.class);
			LogUtils.initLogger(logtag).log(" RECEIVED: "+ specialResourceVO.toJson(), debug);

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

			LogUtils.initLogger(logtag).log(" RECEIVED: "+ in.readUTF(), debug);
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

		List<String> responseList = request(vo);
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

		List responseList = request(vo);
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
		List<String> responseList = request(vo);

		commandLog("exchanging to ");
		String response = responseList.get(0);
		System.out.println(response);
	}

	void commandLog(String commandInfo) {
		LogUtils.initLogger(logtag).log("setting debug on", debug);
		if (defaultHost) {
			LogUtils.initLogger(logtag).log(commandInfo + "localhost:8888", debug);
		} else {
			LogUtils.initLogger(logtag).log(commandInfo + parameters.get(Commands.host) + ":" + parameters.get(Commands.port), debug);
		}
	}

	List request(AbstractVO vo) {
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

			while (in.available() > 0) {
				responseList.add(in.readUTF());
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
