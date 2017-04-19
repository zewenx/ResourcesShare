package VO;

import java.util.ArrayList;
import java.util.List;

import server.DataObject;

public class RemoveVO extends PublishVO {
	@Override
	public List<String> execute(DataObject data) {
		List<String> responseList = new ArrayList<String>();

		if (data.isResourceExisted(getResource())) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("cannot remove resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		if (getResource() == null) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("missing resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		data.removeResource(getResource());;
		SuccessVO successVO = new SuccessVO();
		responseList.add(successVO.toJson());
		return responseList;
	}
}
