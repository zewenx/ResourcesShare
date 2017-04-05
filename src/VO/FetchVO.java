package VO;

import java.util.List;

import server.DataObject;

public class FetchVO extends RequestVO{
	private ResourceVO resourceTemplate;

	public ResourceVO getResource() {
		return resourceTemplate;
	}

	public void setResource(ResourceVO resource) {
		this.resourceTemplate = resource;
	}

	@Override
	public List<String> execute(DataObject data) {
		// TODO Auto-generated method stub
		return null;
	}
}
