package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.sun.org.apache.xpath.internal.operations.And;

import VO.AbstractVO;
import VO.ExchangeVO;
import VO.ResourceVO;
import VO.ServerVO;
import VO.ShareVO;
import server.Commands;
import server.LogUtils;

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
	}

	public static void main(String[] args) {
		new Client().start(args);
	}

	public void start(String[] args) {
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine commands = parser.parse(options, args);

			if (commands.hasOption(Commands.port) && commands.hasOption(Commands.host)) {
				defaultHost = false;
				parameters.put(Commands.port, commands.getOptionValue(Commands.port));
				parameters.put(Commands.host, commands.getOptionValue(Commands.host));
			}

			if (commands.hasOption(Commands.debug)) {
				debug = true;
			}

			if (commands.hasOption(Commands.publish)) {

			} else if (commands.hasOption(Commands.exchange)) {
				exchangeCommand(commands);
			} else if (commands.hasOption(Commands.fetch)) {

			} else if (commands.hasOption(Commands.query)) {

			} else if (commands.hasOption(Commands.remove)) {

			} else if (commands.hasOption(Commands.share)) {
				shareCommand(commands);
			} else {

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		if (debug) {
			LogUtils.initLogger().log("setting debug on", debug);
		}
		commandLog("sharing to ");
		
		List<String> responseList = request(vo);
		String response = responseList.get(0);
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
			serverVO.setPort(serversData[1]);
			serverList.add(serverVO);
		}
		vo.setServerList(serverList);
		List<String> responseList = request(vo);
		
		commandLog("exchanging to ");
		String response = responseList.get(0);
		System.out.println(response);
	}

	void commandLog(String commandInfo){
		if (defaultHost) {
			LogUtils.initLogger().log(commandInfo + "localhost:8888", debug);
		}else{
			LogUtils.initLogger().log(commandInfo + parameters.get(Commands.host)+":"+parameters.get(Commands.port), debug);
		}
	}
	
	List<String> request(AbstractVO vo) {
		Socket socket = null;
		List<String> responseList = new ArrayList<String>();
		try {

			// InetSocketAddress address = new InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);
			InetSocketAddress address = null;
			if (defaultHost) {
				address = new InetSocketAddress("127.0.0.1", 8888);
			} else {
				address = new InetSocketAddress(parameters.get(Commands.host),Integer.parseInt(parameters.get(Commands.port)));
			}
			socket = new Socket();
			socket.connect(address);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			LogUtils.initLogger().log(vo.toJson(), debug);
			out.writeUTF(vo.toJson());
			out.flush();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (in.available() > 0) {
				System.out.println(in.available());
				responseList.add(in.readUTF());
			}
			for (String str : responseList) {
				LogUtils.initLogger().log(str, debug);
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
