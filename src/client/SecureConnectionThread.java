package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.sun.jndi.cosnaming.IiopUrl.Address;
import com.sun.security.ntlm.Client;

import VO.RequestVO;
import server.Commands;
import server.LogUtils;

public class SecureConnectionThread  extends ConnectionThread{
	private InetSocketAddress address;
	private RequestVO vo;
	private boolean debug;

	public SecureConnectionThread(String host, int port, RequestVO vo, boolean debug) {
		this.address = new InetSocketAddress(host, port);
		this.vo = vo;
		this.debug = debug;
	}

	public RequestVO getVO() {
		return vo;
	}

	public void run() {
		SSLSocket sslsocket = null;
		try {
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			sslsocket = (SSLSocket) sslsocketfactory.createSocket(address.getHostName(), address.getPort());

			DataInputStream in = new DataInputStream(sslsocket.getInputStream());
			DataOutputStream out = new DataOutputStream(sslsocket.getOutputStream());
			LogUtils.initLogger(EZShare.Client.logtag).log(" SEND: " + vo.toJson(), debug);
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
				if (response.length() > 0 && vo.getCommand().toLowerCase().equals("subscribe")) {
					System.out.println(response);
					if(response.contains("resultSize")||response.contains("error")) {
						break l;
					}					
				}
				if (response.length() > 0 && vo.getCommand().toLowerCase().equals("unsubscribe")){
					System.out.println(response);
					if(response.contains("response")){
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
