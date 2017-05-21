package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import VO.RequestVO;
import server.Commands;
import server.LogUtils;

public class SecureNetwork extends Network {
	public SecureNetwork(Map<String, String> parameters, boolean debug, String logtag, boolean isSecureConnection) {
		super(parameters, debug, logtag, isSecureConnection);
	}

	@Override
	public List request(RequestVO vo) {
		if (isSecureConnection) {
			return secureRequest(vo);
		} else {
			return super.request(vo);
		}
	}

	private List secureRequest(RequestVO vo) {
		
		System.setProperty("javax.net.ssl.trustStore", "clientKeyStore/myGreatName");
		
		List responseList = new ArrayList<String>();
		
		
		try {
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(parameters.get(Commands.host),Integer.parseInt(parameters.get(Commands.host)));

			LogUtils.initLogger(logtag).log(" SEND: " + vo.toJson(), debug);
			// Create buffered reader to read input from the console

			DataInputStream in = new DataInputStream(sslsocket.getInputStream());
			DataOutputStream out = new DataOutputStream(sslsocket.getOutputStream());
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return null;
	}
}
