package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.google.gson.Gson;

import VO.QueryVO;
import VO.RequestVO;
import VO.ResourceVO;
import VO.ServerVO;
import VO.SubscribeVO;

public class SubscriptionRelayThread implements Runnable {
	private InetSocketAddress address;
	private Socket socket = null;
	private RequestVO vo;
	private boolean debug = false;
	public SubscriptionRelayThread(ServerVO server, RequestVO vo,boolean debug){
		this.address = new InetSocketAddress(server.getHostname(), server.getPort());
		this.vo = vo;
		this.debug = debug;
	}
	
	public void run() {
		try{
			socket = new Socket();
			socket.connect(address);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
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
				socket.close();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
}


