package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

	@Override
	public void run() {
		try {
			String data = in.readUTF();
			LogUtils.initLogger(Server.logtag).log(" SEND: "+data, Server.debug);
			List responseData = CommandExecutor.init().submit(data);
			for (Object o : responseData) {
				if (o instanceof String) {
					out.writeUTF((String) o);
					LogUtils.initLogger(Server.logtag).log(" RECEIVED: "+(String) o, Server.debug);
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
