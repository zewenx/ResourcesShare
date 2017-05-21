package VO;

import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.PrinterMessageFromOperator;

import com.sun.javafx.collections.MappingChange.Map;

import server.DataObject;
import server.SubscriptionHandler;

public class PublishVO extends RequestVO{
	private ResourceVO resource;
	
	//getters and setters
	public ResourceVO getResource() {
		return resource;
	}

	public void setResource(ResourceVO resource) {
		this.resource = resource;
	}

	@Override
	public List<String> execute(DataObject data) {
		System.out.println("65432");
		
		List<String> responseList = new ArrayList<String>();
		// Error Handling
		//if there is no resource, return error.
		if (getResource() == null) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("missing resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		//if the uri doesn't exist or is a file scheme
		ResourceVO resourceVO = getResource();
		String uri = resourceVO.getUri();
		if (uri == null || uri == "" || uri.startsWith("file://")) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("invalid resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		//if there already exists resource with same URI and channel, but different
		//user is not allowed
		if (!data.isShaveOrPublishAllowed(getResource())) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("cannot publish resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		//if owner field is equal to a single * character, return invalid resource error
		if (getResource().getOwner().equals("*")) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("invalid resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		//update hash-map and return a success message
		data.saveResource(getResource());
		System.out.println("12345");
		SubscriptionHandler.checkResource(this.resource);
		SuccessVO successVO = new SuccessVO();
		responseList.add(successVO.toJson());
		
		//check for subscriptions to notify
		SubscriptionHandler.checkResource(resource);
		return responseList;
	}
	
	
}
