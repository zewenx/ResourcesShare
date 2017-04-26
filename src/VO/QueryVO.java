package VO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.DataObject;

public class QueryVO extends RequestVO {
	private boolean relay;
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
		/*
		 * (The template channel equals (case sensitive) the resource channel
		 * AND If the template contains an owner that is not "", then the
		 * candidate owner must equal it (case sensitive) AND Any tags present
		 * in the template also are present in the candidate (case insensitive)
		 * AND If the template contains a URI then the candidate URI matches
		 * (case sensitive) AND (The candidate name contains the template name
		 * as a substring (for non "" template name) OR The candidate
		 * description contains the template description as a substring (for non
		 * "" template descriptions) OR The template description and name are
		 * both ""))
		 * 
		 */
		Collection<ResourceVO> list = data.getResourceMap().values();
		List<ResourceVO> resultList = new ArrayList<ResourceVO>();
		for (ResourceVO vo : list) {
			if (resourceTemplate.getChannel() != "") {
				if (!vo.getChannel().equals(resourceTemplate.getChannel())) {
					continue;
				}
			}
			if (resourceTemplate.getOwner() != "") {
				if (!vo.getOwner().equals(resourceTemplate.getOwner())) {
					continue;
				}
			}
			if (resourceTemplate.getUri() != null && !resourceTemplate.getUri().equals("")) {
				if (!vo.getUri().equals(resourceTemplate.getUri())) {
					continue;
				}
			}
			if (resourceTemplate.getTags() != null || resourceTemplate.getTags().size() > 0) {
				Set<String> tagResource = new HashSet<String>();
				Set<String> tagTemp = new HashSet<String>();
				tagResource.addAll(resourceTemplate.getTags());
				tagTemp.addAll(vo.getTags());
				if (!tagTemp.containsAll(tagResource)) {
					continue;
				}
			}

			// TODO
			if (resourceTemplate.getName() != "" &&resourceTemplate.getDescription() != "") {
				if (!vo.getName().contains(resourceTemplate.getName())&& !vo.getDescription().contains(resourceTemplate.getDescription())) {
					continue;
				}
			}
			

			vo.setOwner("*");
			resultList.add(vo);

		}
		// System.out.println(data.toString());
		SuccessVO successVO = new SuccessVO();
		List<String> responseList = new ArrayList<String>();
		responseList.add(successVO.toJson());
		for (ResourceVO resourceVO : resultList) {
			responseList.add(resourceVO.toJson());
		}
		responseList.add(new ResultSizeVO().setResultSize("" + resultList.size()).toJson());
		return responseList;
	}

}
