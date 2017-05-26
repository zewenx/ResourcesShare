package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import VO.RequestVO;
import VO.SpecialResourceVO;
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
	public void fetchRequest(RequestVO vo){
		Socket socket = null;
		try {

			// InetSocketAddress address = new
			// InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);
			InetSocketAddress address = new InetSocketAddress(parameters.get(Commands.host),
					Integer.parseInt(parameters.get(Commands.port)));
			socket = new Socket();
			socket.connect(address);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			LogUtils.initLogger(logtag).log(" SEND: " + vo.toJson(), debug);
			out.writeUTF(vo.toJson());
			out.flush();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String response = in.readUTF();

			LogUtils.initLogger(logtag).log(" RECEIVED: " + response, debug);
			System.out.println(response);

			if (response.contains("error")) {
				System.out.println(response);
				return;
			}
			SpecialResourceVO specialResourceVO = new Gson().fromJson(in.readUTF(), SpecialResourceVO.class);
			LogUtils.initLogger(logtag).log(" RECEIVED: " + specialResourceVO.toJson(), debug);

			File file = new File(new URI(specialResourceVO.getUri()));
			String fileName = file.getName();
			File dataFile = new File(fileName);
			if (dataFile.exists()) {
				dataFile.delete();
			}
			dataFile.createNewFile();
			byte[] datas = new byte[(int) specialResourceVO.getResourceSize() + 1];
			int count = 0;
			while (count < specialResourceVO.getResourceSize()) {
				count += in.read(datas, count, (int) (specialResourceVO.getResourceSize()) - count);
			}
			FileUtils.writeByteArrayToFile(dataFile, datas);

			LogUtils.initLogger(logtag).log(" RECEIVED: " + in.readUTF(), debug);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
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
	}
}
