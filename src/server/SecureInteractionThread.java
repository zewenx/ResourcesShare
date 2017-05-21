package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import EZShare.Server;
import VO.ExchangeVO;
import VO.ServerVO;

public class SecureInteractionThread implements Runnable {

	int exchangeInterval = 600000;
	boolean debug = false;

	public SecureInteractionThread(String time, boolean debug) {
		exchangeInterval = Integer.parseInt(time);
		this.debug = debug;
	}

	@Override
	public void run() {
		List<ServerVO> serverList = CommandExecutor.init().getDataObject().getSecureServerList();

		ExchangeVO vo = new ExchangeVO();
		vo.setCommand("EXCHANGE");

		vo.setServerList(serverList);

		Socket socket = null;

		while (true) {
			if (serverList.size() == 0) {
				try {
					Thread.sleep(exchangeInterval);
					continue;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			int num = (int) (Math.random() * serverList.size());
			ServerVO serverVO = serverList.get(num);
			String host = serverVO.getHostname();
			int port = serverVO.getPort();

			LogUtils.initLogger(Server.logtag).log("exchanging to " + host + ":" + port, debug);

			List<String> responseList = new ArrayList<String>();
			try {

				// InetSocketAddress address = new
				// InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);

				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host,port);

				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				LogUtils.initLogger(Server.logtag).log(" SEND: " +vo.toJson(), debug);
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
				for (String str : responseList) {
					LogUtils.initLogger(Server.logtag).log(" RECEIVED: " +str, debug);
				}
			} catch (IOException e) {
				serverList.remove(serverVO);
				LogUtils.initLogger(Server.logtag).log("exchanging to " + host + ":" + port + " failed!", debug);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(exchangeInterval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
