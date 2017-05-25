package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import VO.RequestVO;
import server.Commands;
import server.LogUtils;

public class Network {
	
	Map<String, String> parameters;
	boolean debug;
	String logtag;
	boolean isSecureConnection = false;
	
	public Network() {
		
	}
	
	public Network(Map<String, String> parameters, boolean debug, String logtag, boolean isSecureConnection) {
		this.parameters = parameters;
		this.debug = debug;
		this.logtag = logtag;
		this.isSecureConnection = isSecureConnection;
	}

	public List request(RequestVO vo) {
		Socket socket = null;
		List responseList = new ArrayList<String>();
		try {

			// InetSocketAddress address = new
			// InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);
			InetSocketAddress address = new InetSocketAddress(parameters.get(Commands.host),
					Integer.parseInt(parameters.get(Commands.port)));
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
					case "subscribe":
						if(response.contains("response")){
							break l;
						}
					case "unsubscribe":
						if(response.contains("response")){
							break l;
						}
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
