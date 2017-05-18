package VO;

import java.util.List;

import server.DataObject;

public class SubscribeVO extends RequestVO{
	boolean relay;
	private ResourceVO resourceTemplate;
	
	public boolean isRelay() {
		return relay;
	}

	public void setRelay(boolean relay) {
		this.relay = relay;
	}
	public ResourceVO getResourceTemplate() {
		return resourceTemplate;
	}

	public void setResourceTemplate(ResourceVO resourceTemplate) {
		this.resourceTemplate = resourceTemplate;
	}
	@Override
	public List<String> execute(DataObject data) {
		// TODO Auto-generated method stub
		return null;
	}
}
