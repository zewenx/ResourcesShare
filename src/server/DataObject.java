package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import EZShare.Server;
import VO.ResourceVO;
import VO.ServerVO;
import VO.SubscribeVO;

public class DataObject {

	// key = Owner + Channel + URI value = Resource
	Map<String, ResourceVO> dataList0 = new HashMap<String, ResourceVO>();

	// key = Channel + URI value = Resource
	Map<String, ResourceVO> dataList1 = new HashMap<String, ResourceVO>();

	// Subscriber List
	Map<String, SubscribeVO> subList = new HashMap<String, SubscribeVO>();
	

	// ServerList
	List<ServerVO> serverList = new ArrayList<ServerVO>();
	
	// secure severlist
	List<ServerVO> secureServerList = new ArrayList<ServerVO>();

	// add to sub hash map
	public void addSubscriber(String id, SubscribeVO data){
		subList.put(id, data);
	}
	
	// remove sub from hash map
	public void removeSubscriber(String id){
		subList.remove(id);
	}
	
	// checks if key exists in subscriber hash map
	public boolean isSubIdinUse(String id){
		return subList.containsKey(id);
	}
	// secret only for share command
	private String secret = "";
	
	/**
	 * Sets secret string for SHARE command
	 * @param secret secret key stored for the share command
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	/**
	 * Gets the resource Hash map to be accessed
	 * @return Returns data Hash map
	 */
	public Map<String, ResourceVO> getResourceMap() {
		return dataList0;
	}
	
	/**
	 * Removes key and resource from data hash map
	 * @param vo ResourceVO to be removed
	 */
	public void removeResource(ResourceVO vo) {
		dataList0.remove(vo.getOwner() + vo.getChannel() + vo.getUri());
		dataList1.remove(vo.getChannel() + vo.getUri());
	}
	
	/**
	 * Checks to see if a ResourceVO exists in the hash map
	 * @param vo Resource VO to be checked
	 * @return  True - if resource exists, otherwise False
	 */
	public boolean isResourceExisted(ResourceVO vo) {
		return dataList0.containsKey(vo.getOwner() + vo.getChannel() + vo.getUri());
	}

	/**
	 * Saves a resource into the hash map
	 * @param vo ResourceVO to be saved into the map
	 */
	public void saveResource(ResourceVO vo) {
		vo.setEzserver(Server.parameters.get(Commands.advertisedhostname) + ":" + Server.parameters.get(Commands.port));

		dataList0.put(vo.getOwner() + vo.getChannel() + vo.getUri(), vo);
		dataList1.put(vo.getChannel() + vo.getUri(), vo);
	}

	/**
	 * Checks to see if a resource already exists on the sever which contains the same channel
	 * and uri buy different user
	 * @param vo ResourceVO to be checked
	 * @return True if allowed, otherwise False
	 */
	public boolean isShaveOrPublishAllowed(ResourceVO vo) {
		String key0 = vo.getOwner() + vo.getChannel() + vo.getUri();
		String key1 = vo.getChannel() + vo.getUri();

		if (dataList1.containsKey(key1) && !dataList0.containsKey(key0)) {
			return false;
		}
		return true;
	}

	/**
	 * gets stored secret string
	 * @return Secret String
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * checks server list to see if server already exists in the server list 
	 * @param vo The ServerVO to be checked
	 * @return True if server already exists on list, otherwise False
	 */
	public boolean isServerAlreadyExisted(ServerVO vo) {
		for (ServerVO serverVO : serverList) {
			if (serverVO.getHostname().equals(vo.getHostname()) && serverVO.getPort()== vo.getPort()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks secure server list to see if server already exists in the server list 
	 * @param vo The ServerVO to be checked
	 * @return True if server already exists on list, otherwise False
	 */
	public boolean isSecureServerAlreadyExisted(ServerVO vo) {
		for (ServerVO serverVO : secureServerList) {
			if (serverVO.getHostname().equals(vo.getHostname()) && serverVO.getPort()== vo.getPort()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * adds server to the server list
	 * @param serverVO serverVO to be added
	 */
	public void addServer(ServerVO serverVO) {
		serverList.add(serverVO);
	}
	
	/**
	 * adds server to the secure server list
	 * @param serverVO serverVO to be added
	 */
	public void addServer2SecureList(ServerVO serverVO) {
		secureServerList.add(serverVO);
	}
	
	/**
	 * gets server list 
	 * @return the server list
	 */
	public List<ServerVO> getServerList (){
		return serverList;
	}
	
	/**
	 * gets secure server list 
	 * @return the server list
	 */
	public List<ServerVO> getSecureServerList (){
		return secureServerList;
	}
	
	public String toString(){
		String data = "";
		for (ResourceVO resource : dataList0.values()) {
			data += resource.getUri() + "\n";
		}
		return data;
	}
}
