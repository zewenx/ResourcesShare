package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import javax.net.ssl.SSLSocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.corba.se.spi.ior.Writeable;

import EZShare.Server;
import VO.RequestVO;
import netscape.javascript.JSObject;

public class ConnectionThread implements Runnable {

	private Socket mSocket;

	private DataInputStream in;
	private DataOutputStream out;

	public ConnectionThread(Socket socket) {
		this.mSocket = socket;
		try {
			in = new DataInputStream(mSocket.getInputStream());
			out = new DataOutputStream(mSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void run() {
		try {
			String data = in.readUTF();

			LogUtils.initLogger(Server.logtag).log(" RECEIVED: " + data, Server.debug);
			CommandExecutor commandExecutor = CommandExecutor.init();
			commandExecutor.setInputOutputStream(in, out);

			List responseData = null;
			/*
			 * if(data.toLowerCase().contains("unsubscribe")){ synchronized
			 * (this) { responseData = commandExecutor.submit(data, false); } }
			 * else if(data.toLowerCase().contains("subscribe")){ responseData =
			 * commandExecutor.submit(data, false); } else{ synchronized (this)
			 * { responseData = commandExecutor.submit(data, false); } //TODO
			 * SECURITY STUFF }
			 */
			if (data.toLowerCase().contains("subscribe")&&!data.toLowerCase().contains("unsubscribe")) {
				responseData = commandExecutor.submitSubcribe(data, mSocket instanceof SSLSocket);
			} else {
				responseData = commandExecutor.submit(data, mSocket instanceof SSLSocket); // TODO
			}
			for (Object o : responseData) {
				if (o instanceof String) {
					out.writeUTF((String) o);
					LogUtils.initLogger(Server.logtag).log(" SEND: " + (String) o, Server.debug);
				} else {
					out.write((byte[]) o);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}