package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;

import VO.PublishVO;
import VO.QueryVO;
import VO.ResourceVO;

public class Client {
	public static void main(String[] args) {
		Socket socket =null;
		try {
			InetSocketAddress address = new InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);
			socket = new Socket();
			socket.connect(address);
			DataInputStream in = new DataInputStream( socket.getInputStream());
			DataOutputStream out =new DataOutputStream( socket.getOutputStream());
			PublishVO vo = new PublishVO();
			vo.setCommand("PUBLISH");
			ResourceVO resourceVO = new ResourceVO();
//			resourceVO.setDescription("google address");
//			resourceVO.setName("website");
//			resourceVO.setUri("http://www.google.com");
			
			QueryVO queryVO = new QueryVO();
			queryVO.setCommand("QUERY");
			queryVO.setResourceTemplate(resourceVO);
			queryVO.setRelay(true);
			vo.setResource(resourceVO);
			System.out.println(queryVO.toJson());
			out.writeUTF(queryVO.toJson());
			out.flush();
//			while(in.available()>0)
			Thread.sleep(50);
			while (in.available()>0) {
				System.out.println(in.available());
				System.out.println(in.readUTF());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
