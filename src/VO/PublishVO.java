package VO;

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
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
