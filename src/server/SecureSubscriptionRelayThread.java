package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.google.gson.Gson;

import VO.QueryVO;
import VO.RequestVO;
import VO.ResourceVO;
import VO.ServerVO;
import VO.SubscribeVO;

public class SecureSubscriptionRelayThread implements Runnable {
	private InetSocketAddress address;
	private RequestVO vo;
	private boolean debug = false;
	public SecureSubscriptionRelayThread(ServerVO server, RequestVO vo,boolean debug){
		this.address = new InetSocketAddress(server.getHostname(), server.getPort());
		this.vo = vo;
		this.debug = debug;
	}
	public void run() {
		SSLSocket sslsocket = null;
		try {
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			sslsocket = (SSLSocket) sslsocketfactory.createSocket(address.getHostName(), address.getPort());

			DataInputStream in = new DataInputStream(sslsocket.getInputStream());
			DataOutputStream out = new DataOutputStream(sslsocket.getOutputStream());
			((SubscribeVO)vo).setRelay(false);
			LogUtils.initLogger(EZShare.Server.logtag).log(" SEND: " + vo.toJson(), debug);
			out.writeUTF(vo.toJson());
			out.flush();
			l:while(true){
				String response = "";
				try {
					response = in.readUTF();
				} catch (Exception e) {
					e.printStackTrace();
					break l;
				}
				if (response.length() > 0) {
					//Do stuff
					((SubscribeVO)vo).sendResource(new Gson().fromJson(response, ResourceVO.class));
					if(response.contains("resultSize")||response.contains("error")) {
						break l;
					}					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				sslsocket.close();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
}


