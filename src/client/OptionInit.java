package client;

import org.apache.commons.cli.Options;
import server.Commands;


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
// -secure for secure connection

public class OptionInit {
	public void initOptions(Options options){
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
		options.addOption(Commands.secure, true, "secure connecton");
		options.addOption(Commands.subscribe, false, "subscribe to query responses of the server");
		options.addOption(Commands.unsubscribe, false, "unsubscribe from query responses of the server");

		options.addOption(Commands.id, true, "subscription id");
		options.addOption(Commands.help, false, "help");
	}
}
