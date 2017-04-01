package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import VO.PublishVO;
import VO.ResourceVO;

public class Client {
	public static void main(String[] args) {
		Socket socket =null;
		try {
			socket = new Socket("127.0.0.1",8888);
			DataInputStream in = new DataInputStream( socket.getInputStream());
			DataOutputStream out =new DataOutputStream( socket.getOutputStream());
			PublishVO vo = new PublishVO();
			vo.setCommand("publish");
			ResourceVO resourceVO = new ResourceVO();
			resourceVO.setDescription("google address");
			resourceVO.setName("website");
			resourceVO.setUri("http://www.google.com");
			vo.setResource(resourceVO);
			out.writeUTF(vo.toJson());
			out.flush();
			System.out.println(in.readUTF());
		} catch (IOException e) {
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
