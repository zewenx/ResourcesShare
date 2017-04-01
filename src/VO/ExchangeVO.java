package VO;

import java.util.List;

public class ExchangeVO extends RequestVO {
	List<ServerVO> serverList;

	public List<ServerVO> getServerList() {
		return serverList;
	}

	public void setServerList(List<ServerVO> serverList) {
		this.serverList = serverList;
	}
	
}
