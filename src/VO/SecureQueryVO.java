package VO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.google.gson.Gson;

import server.Commands;
import server.DataObject;
import server.LogUtils;

public class SecureQueryVO extends QueryVO {

	@Override
	public List<String> execute(DataObject data) {
		/*
		 * (The template channel equals (case sensitive) the resource channel
		 * AND If the template contains an owner that is not "", then the
		 * candidate owner must equal it (case sensitive) AND Any tags present
		 * in the template also are present in the candidate (case insensitive)
		 * AND If the template contains a URI then the candidate URI matches
		 * (case sensitive) AND (The candidate name contains the template name
		 * as a substring (for non "" template name) OR The candidate
		 * description contains the template description as a substring (for non
		 * "" template descriptions) OR The template description and name are
		 * both ""))
		 * 
		 */
		Collection<ResourceVO> list = data.getResourceMap().values();
		List<ResourceVO> resultList = new ArrayList<ResourceVO>();
		/*steps through hash map values, if it finds a conflicting result with the query
		resource template continues the loop, otherwise adds resource to result list
		*/
		for (ResourceVO vo : list) {
			if (!resourceTemplate.getChannel().equals("")) {
				if (!vo.getChannel().equals(resourceTemplate.getChannel())) {
					continue;
				}
			}
			if (!resourceTemplate.getOwner().equals("")) {
				if (!vo.getOwner().equals(resourceTemplate.getOwner())) {
					continue;
				}
			}
			if (resourceTemplate.getUri() != null && !resourceTemplate.getUri().equals("")) {
				if (!vo.getUri().equals(resourceTemplate.getUri())) {
					continue;
				}
			}
			if (resourceTemplate.getTags() != null || resourceTemplate.getTags().size() > 0) {
				Set<String> tagResource = new HashSet<String>();
				Set<String> tagTemp = new HashSet<String>();
				tagResource.addAll(resourceTemplate.getTags());
				tagTemp.addAll(vo.getTags());
				if (!tagTemp.containsAll(tagResource)) {
					continue;
				}
			}
		
			if (resourceTemplate.getName() != "" &&resourceTemplate.getDescription() != "") {
				if (!(vo.getName().contains(resourceTemplate.getName())&& vo.getDescription().contains(resourceTemplate.getDescription()))) {
					continue;
				}
			}else{
				if (resourceTemplate.getName() == "" && !vo.getDescription().contains(resourceTemplate.getDescription())) {
					continue;
				}
				
				if (resourceTemplate.getDescription() == "" && !vo.getName().contains(resourceTemplate.getName())) {
					continue;
				}
			}
			

			resultList.add(vo);

		}
		// System.out.println(data.toString());
		SuccessVO successVO = new SuccessVO();
		List<String> responseList = new ArrayList<String>();
		responseList.add(successVO.toJson());
		for (ResourceVO resourceVO : resultList) {
			responseList.add(resourceVO.toJson());
		}
		if(data.getSecureServerList().size()>0&& relay==true){
			for(ServerVO serverVO : data.getSecureServerList()){
				this.setRelay(false);
				List tempList = request(this, serverVO.getHostname(), serverVO.getPort());
				
				responseList.addAll(tempList);
			}
		}
		
		responseList.add(new ResultSizeVO().setResultSize("" +(responseList.size()-1)).toJson());
		return responseList;
	}
	
	List request(RequestVO vo,String host,int port) {
		List responseList = new ArrayList<String>();
		DataInputStream in = null;
		DataOutputStream out=null;
		SSLSocket sslsocket = null;
		try {

			// InetSocketAddress address = new
			// InetSocketAddress("sunrise.cis.unimelb.edu.au", 3780);
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			sslsocket = (SSLSocket) sslsocketfactory.createSocket(host,port);

			 in = new DataInputStream(sslsocket.getInputStream());
			 out = new DataOutputStream(sslsocket.getOutputStream());
//			LogUtils.initLogger("Server.log").log(" SEND: " + vo.toJson(), false);
			out.writeUTF(vo.toJson());
			out.flush();


l:			while (true) {
				
				String response = in.readUTF();
				
				
				switch(vo.getCommand().toLowerCase()){
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
					if (response.contains("resultSize")||response.contains("error")) {
						break l;
					}
				}
				
				if (response.contains("response")||response.contains("resultSize")) {
					continue;
				}
				ResourceVO resourceVO = new Gson().fromJson(response, ResourceVO.class);
				resourceVO.setOwner("*");
				responseList.add(resourceVO.toJson());
				
			}
			for (Object str : responseList) {
				if (str instanceof String) {
					LogUtils.initLogger("Server.log").log(" RECEIVED: " + (String) str, false);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					
					in.close();
				}
				if (out != null) {
					
					out.close();
				}
				if (sslsocket != null) {
					
					sslsocket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return responseList;
	}

}
