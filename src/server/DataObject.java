package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import EZShare.Server;
import VO.ResourceVO;
import VO.ServerVO;

public class DataObject {

	// key = Owner + Channel + URI value = Resource
	Map<String, ResourceVO> dataList0 = new HashMap<String, ResourceVO>();

	// key = Channel + URI value = Resource
	Map<String, ResourceVO> dataList1 = new HashMap<String, ResourceVO>();

	// ServerList
	List<ServerVO> serverList = new ArrayList<ServerVO>();

	// secret only for share command
	private String secret = "";

	public void setSecret(String secret) {
		this.secret = secret;
	}
	public Collection<ResourceVO> getResourceMap()
	{
		return dataList0.values();
	}
	
	public void removeResource(ResourceVO vo) {
		dataList0.remove(vo.getOwner() + vo.getChannel() + vo.getUri());
		dataList1.remove(vo.getChannel() + vo.getUri());
	}

	public boolean isResourceExisted(ResourceVO vo) {
		return dataList0.containsKey(vo.getOwner() + vo.getChannel() + vo.getUri());
	}

	public void saveResource(ResourceVO vo) {
		vo.setEzserver(Server.parameters.get(Commands.host) + ":" + Server.parameters.get(Commands.host));

		dataList0.put(vo.getOwner() + vo.getChannel() + vo.getUri(), vo);
		dataList1.put(vo.getChannel() + vo.getUri(), vo);
	}

	public boolean isShaveOrPublishAllowed(ResourceVO vo) {
		String key0 = vo.getOwner() + vo.getChannel() + vo.getUri();
		String key1 = vo.getChannel() + vo.getUri();

		if (dataList1.containsKey(key1) && !dataList0.containsKey(key0)) {
			return false;
		}
		return true;
	}

	public String getSecret() {
		return secret;
	}

	public boolean isServerAlreadyExisted(ServerVO vo) {
		for (ServerVO serverVO : serverList) {
			if (serverVO.getHostname() == vo.getHostname() && serverVO.getPort() == vo.getPort()) {
				return true;
			}
		}
		return false;
	}

	public void addServer(ServerVO serverVO) {
		serverList.add(serverVO);
	}
}
