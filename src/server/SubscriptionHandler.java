package server;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.omg.CORBA.PUBLIC_MEMBER;

import VO.ResourceVO;
import VO.SubscribeVO;


public class SubscriptionHandler {
	static HashMap <String,SubscribeVO> subMap = new HashMap<String,SubscribeVO>();
	public static void checkResource(ResourceVO resourceTemplate)
	{
		for(SubscribeVO vo : subMap.values())
		{
			if (!resourceTemplate.getChannel().equals("")) {
				if (!vo.getResourceTemplate().getChannel().equals(resourceTemplate.getChannel())) {
					continue;
				}
			}
			if (!resourceTemplate.getOwner().equals("")) {
				if (!vo.getResourceTemplate().getOwner().equals(resourceTemplate.getOwner())) {
					continue;
				}
			}
			if (resourceTemplate.getUri() != null && !resourceTemplate.getUri().equals("")) {
				if (!vo.getResourceTemplate().getUri().equals(resourceTemplate.getUri())) {
					continue;
				}
			}
			if (resourceTemplate.getTags() != null || resourceTemplate.getTags().size() > 0) {
				Set<String> tagResource = new HashSet<String>();
				Set<String> tagTemp = new HashSet<String>();
				tagResource.addAll(resourceTemplate.getTags());
				tagTemp.addAll(vo.getResourceTemplate().getTags());
				if (!tagTemp.containsAll(tagResource)) {
					continue;
				}
			}
		
			if (resourceTemplate.getName() != "" &&resourceTemplate.getDescription() != "") {
				if (!(vo.getResourceTemplate().getName().contains(resourceTemplate.getName())&& vo.getResourceTemplate().getDescription().contains(resourceTemplate.getDescription()))) {
					continue;
				}
			}else{
				if (resourceTemplate.getName() == "" && !vo.getResourceTemplate().getDescription().contains(resourceTemplate.getDescription())) {
					continue;
				}
				
				if (resourceTemplate.getDescription() == "" && !vo.getResourceTemplate().getName().contains(resourceTemplate.getName())) {
					continue;
				}
			}
			
			//send response to client!!
			
			
		}
		
	}
	public void addSubscription(SubscribeVO sub)
	{
		subMap.put(sub.getId(), sub);
	}
}
