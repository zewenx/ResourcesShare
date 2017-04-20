package VO;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import server.DataObject;
import sun.awt.RepaintArea;

public class FetchVO extends RequestVO{
	private ResourceVO resourceTemplate;

	public ResourceVO getResource() {
		return resourceTemplate;
	}

	public void setResource(ResourceVO resource) {
		this.resourceTemplate = resource;
	}

	@Override
	public List execute(DataObject data) {
		
		List responseList = new ArrayList<String>();

		if (!data.isResourceExisted(getResource())) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("invalid resourceTemplate");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		
		if (getResource() == null) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("missing resourceTemplate");
			responseList.add(vo.toJson());
			return responseList;
		}

		if (getResource().getOwner().equals("*")) {
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("invalid resource");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		try {
			File file = new File(new URI(getResource().getUri()));
			SuccessVO successVO = new SuccessVO();
			responseList.add(successVO.toJson());
			
			SpecialResourceVO specialResourceVO = new SpecialResourceVO();
			specialResourceVO.setChannel(getResource().getChannel());
			specialResourceVO.setDescription(getResource().getDescription());
			specialResourceVO.setEzserver(getResource().getEzserver());
			specialResourceVO.setName(getResource().getName());
			specialResourceVO.setOwner(getResource().getOwner());
			specialResourceVO.setResourceSize(file.length());
			specialResourceVO.setTags(getResource().getTags());
			specialResourceVO.setUri(getResource().getUri());
			responseList.add(specialResourceVO.toJson());
			
			byte[] dataFile = FileUtils.readFileToByteArray(file);
			responseList.add(dataFile);
			
			responseList.add(new ResultSizeVO().setResultSize("1").toJson());
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		return responseList;
	}
}
