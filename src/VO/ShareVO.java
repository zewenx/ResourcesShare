package VO;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import server.DataObject;

public class ShareVO extends PublishVO{
	private String secret;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	@Override
	public List<String> execute(DataObject data) {
		List<String> responseList = new ArrayList<String>();
		
		if (secret == null || secret == "") {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("missing resource and (or) secret");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		if (!data.getSecret().equals(secret)) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("incorrect secret");
			responseList.add(vo.toJson());
			return responseList;
		}
		

		if (getResource().getOwner().equals("*")) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("invalid resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		ResourceVO resourceVO = getResource();
		String uri = resourceVO.getUri();
		if (uri == null || uri == "" || !uri.startsWith("file://")) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("invalid resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		URI fileURI = URI.create(uri);
		File file = new File(fileURI);
		if ((!file.exists())||(!file.canRead())) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("invalid resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		if (!data.isShaveOrPublishAllowed(resourceVO)) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("cannot share resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		data.saveResource(resourceVO);
		SuccessVO successVO = new SuccessVO();
		responseList.add(successVO.toJson());
		return responseList;
		
	}
	
}
