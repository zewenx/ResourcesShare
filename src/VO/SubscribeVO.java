package VO;

import java.util.ArrayList;
import java.util.List;

import server.DataObject;

public class SubscribeVO extends RequestVO{
	boolean relay;
	private ResourceVO resourceTemplate;
	private String id;
	
	public boolean isRelay() {
		return relay;
	}
	
	public void setID(String id){
		this.id = id;
	}
	public boolean isResourceSame(ResourceVO resource)
	{
		return resourceTemplate.equals(resource);
	}
	public void setRelay(boolean relay) {
		this.relay = relay;
	}
	public ResourceVO getResourceTemplate() {
		return resourceTemplate;
	}
	public String getId() {
		return id;
	}

	public void setResource(ResourceVO resourceTemplate) {
		this.resourceTemplate = resourceTemplate;
	}
	@Override
	public List<String> execute(DataObject data) {
		List<String> responseList = new ArrayList<String>();
		//Error Handling
		if(id.equals(null)||id.equals("")){
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("missing id");
			responseList.add(vo.toJson());
			return responseList;
		}
		if(data.isSubIdinUse(id)){
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("id currently in use");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		//Process
		data.addSubscriber(id, this);
		SuccessVO successVO = new SuccessVO();
		responseList.add(successVO.toJson());
		return responseList;
		
	}
}
