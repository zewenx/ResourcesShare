package VO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import server.DataObject;

public class ExchangeVO extends RequestVO {
	List<ServerVO> serverList;

	public List<ServerVO> getServerList() {
		return serverList;
	}

	public void setServerList(List<ServerVO> serverList) {
		this.serverList = serverList;
	}

	@Override
	public List<String> execute(DataObject data) {

		List<String> responseList = new ArrayList<String>();
		
		if (serverList == null || serverList.size()==0) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("missing or invalid server list");
			responseList.add(vo.toJson());
			return responseList;
		}
		for (ServerVO serverVO : serverList) {
			if (serverVO.getHostname() == null ||serverVO.getHostname()==""||serverVO.getPort()==null||serverVO.getPort()=="") {
				ErrorVO vo = new ErrorVO();
				vo.setErrorMessage("missing or invalid server list");
				responseList.add(vo.toJson());
				return responseList;
			}
		}
		
		for (ServerVO serverVO : serverList) {
			if (!data.isServerAlreadyExisted(serverVO)) {
				data.addServer(serverVO);
			}
		}
		SuccessVO successVO = new SuccessVO();
		responseList.add(successVO.toJson());
		return responseList;
	}
	
}
