package VO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import server.DataObject;

public class SecureExchangeVO extends ExchangeVO {

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
			if (serverVO.getHostname() == null ||serverVO.getHostname()==""||serverVO.getPort() == 0) {
				ErrorVO vo = new ErrorVO();
				vo.setErrorMessage("missing or invalid server list");
				responseList.add(vo.toJson());
				return responseList;
			}
		}
		
		for (ServerVO serverVO : serverList) {
			if (!data.isSecureServerAlreadyExisted(serverVO)) {
				data.addServer2SecureList(serverVO);
			}
		}
		SuccessVO successVO = new SuccessVO();
		responseList.add(successVO.toJson());
		return responseList;
	}
	
}
