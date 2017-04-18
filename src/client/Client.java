package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.NonWritableChannelException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import VO.AbstractVO;
import VO.ExchangeVO;
import VO.PublishVO;
import VO.QueryVO;
import VO.ResourceVO;
import VO.ServerVO;
import jdk.nashorn.internal.ir.RuntimeNode.Request;
import server.Commands;
import server.Server;

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

	void start(String[] args) {
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine commands = parser.parse(options, args);
			
			if (commands.hasOption(Commands.port) && commands.hasOption(Commands.host)) {
				parameters.put(Commands.port, commands.getOptionValue(Commands.port));
				parameters.put(Commands.host, commands.getOptionValue(Commands.host));
			} else {
				System.out.println("host or port is missing");
				return;
			}
			
			if (commands.hasOption(Commands.debug)) {
				parameters.put(Commands.debug, "Y");
			}else{
				parameters.put(Commands.debug, "N");
			}
			
			if (commands.hasOption(Commands.publish)) {
				
			} else if (commands.hasOption(Commands.exchange)) {
				exchangeCommand(commands);
			} else if (commands.hasOption(Commands.fetch)) {

			} else if (commands.hasOption(Commands.query)) {

			} else if (commands.hasOption(Commands.remove)) {

			} else if (commands.hasOption(Commands.share)) {

			}else {
				
			}

			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void exchangeCommand(CommandLine commands) {
		String services = commands.getOptionValue(Commands.servers);
		ExchangeVO vo = new ExchangeVO();
		vo.setCommand("EXCHANGE");
		String[] serversArray = services.split(",");
		List<ServerVO> serverList = new ArrayList<ServerVO>();
		for(String server : serversArray){
			ServerVO serverVO = new ServerVO();
			String[] serversData = server.split(":");
			serverVO.setHostname(serversData[0]);
			serverVO.setPort(serversData[1]);
			serverList.add(serverVO);
		}
		vo.setServerList(serverList);
		List<String> responseList = request(vo);
		String response = responseList.get(0);
		System.out.println(response);
	}

	List<String> request(AbstractVO vo) {
		Socket socket = null;
		List<String> responseList = new ArrayList<String>();
		try {
			
			InetSocketAddress address = new InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);
			socket = new Socket();
			socket.connect(address);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			System.out.println(vo.toJson());
			out.writeUTF(vo.toJson());
			out.flush();
			while (in.available() > 0) {
				System.out.println(in.available());
				responseList.add(in.readUTF());
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
