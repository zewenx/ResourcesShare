package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.sun.jndi.cosnaming.IiopUrl.Address;
import com.sun.security.ntlm.Client;

import VO.RequestVO;
import server.LogUtils;
public class ConnectionThread implements Runnable{
	private InetSocketAddress address;
	private Socket socket = null;
	private RequestVO vo;
	private boolean debug;
	
	
	public ConnectionThread() {
	}

	public ConnectionThread(String host, int port, RequestVO vo,boolean debug){
		this.address = new InetSocketAddress(host,port);
		this.vo = vo;
		this.debug = debug;
	}
	
	public RequestVO getVO(){
		return vo;
	}
	
	public void run() {
		try{
			socket = new Socket();
			socket.setKeepAlive(true);
			socket.setSoTimeout(600000);
			socket.connect(address);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			LogUtils.initLogger(EZShare.Client.logtag).log(" SEND: " + vo.toJson(), debug);
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
					System.out.println(response);
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
				socket.close();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	} 
}

