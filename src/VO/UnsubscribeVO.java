package VO;

import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.bind.v2.model.core.ID;

import server.DataObject;
import server.SubscriptionHandler;

public class UnsubscribeVO extends RequestVO {
	private String id = "";

	public void setId(String id){
		this.id = id;
	}

	@Override
	public List<String> execute(DataObject data) {
		List<String> responseList = new ArrayList<String>();
		
		//error handelling
		if(id.equals(null)||id.equals(""))
		{
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("Missing ID");
			responseList.add(vo.toJson());
			return responseList;
		}
		//process
		if(SubscriptionHandler.unsubscribe(id))
		{
			SuccessVO successVO = new SuccessVO();
			responseList.add(successVO.toJson());
			return responseList;
		}
		else{
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("Invalid ID");
			responseList.add(vo.toJson());
			return responseList;
		}
	}

}
