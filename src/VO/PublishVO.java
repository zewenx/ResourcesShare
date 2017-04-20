package VO;

import java.util.ArrayList;
import java.util.List;

import server.DataObject;

public class PublishVO extends RequestVO{
	private ResourceVO resource;

	public ResourceVO getResource() {
		return resource;
	}

	public void setResource(ResourceVO resource) {
		this.resource = resource;
	}

	@Override
	public List<String> execute(DataObject data) {
		
		List<String> responseList = new ArrayList<String>();
		// Error Handling
		/*
		 * The URI must be present, must be absolute and cannot be a file scheme.
Publishing a resource with the same primary key as an existing resource simply overwrites the
existing resource.

String values must not contain the "\0" character, nor start or end with whitespace. The server may
silently remove such characters or may consider the resource invalid if such things are found (this is
the same for all commands).

The Owner field must not be the single character "*". The resource is invalid in this case. (This is the
same for all commands.)
		 */
		//if there is no resource, return error.
		if (getResource() == null) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("missing resource");
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

		//if the string is invalid
		if (getResource().getOwner().equals("*")) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("invalid resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		//
		data.saveResource(getResource());
		SuccessVO successVO = new SuccessVO();
		responseList.add(successVO.toJson());
		return responseList;
	}
	
	
}
