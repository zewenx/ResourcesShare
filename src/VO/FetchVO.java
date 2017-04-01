package VO;

public class FetchVO extends RequestVO{
	private ResourceVO resourceTemplate;

	public ResourceVO getResource() {
		return resourceTemplate;
	}

	public void setResource(ResourceVO resource) {
		this.resourceTemplate = resource;
	}
}
