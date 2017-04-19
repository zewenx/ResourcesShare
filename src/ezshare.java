import client.Client;
import server.Server;

public class ezshare {
	public static void main(String[] args) {
		if (args[1].equals("EZShare.Client")) {
			new Client().start(args);
		}else if(args[1].equals("EZShare.Server")){
			new Server().start(args);
		}
	}
}
