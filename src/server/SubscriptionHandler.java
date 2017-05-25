package server;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omg.CORBA.PUBLIC_MEMBER;

import VO.ResourceVO;
import VO.SubscribeVO;


public class SubscriptionHandler {
	private static Map<String, SubscribeVO> subMap = DataObject.getSublist();
	public static void checkResource(ResourceVO resourceTemplate)
	{
		System.out.println("Subhandle 1");
		for(SubscribeVO vo : subMap.values())
		{
			System.out.println("Subhandle 2");
			if (!vo.getResourceTemplate().getChannel().equals("")) {
				if (!vo.getResourceTemplate().getChannel().equals(resourceTemplate.getChannel())) {
					continue;
				}
			}
			System.out.println("Subhandle 3");
			if (!vo.getResourceTemplate().getOwner().equals("")) {
				if (!vo.getResourceTemplate().getOwner().equals(resourceTemplate.getOwner())) {
					continue;
				}
			}
			System.out.println("Subhandle 4");
			if (!vo.getResourceTemplate().equals(null)&& !vo.getResourceTemplate().getUri().equals("")) {
				System.out.println(resourceTemplate.getUri());
				System.out.println(vo.getResourceTemplate().getUri());
				if (!vo.getResourceTemplate().getUri().equals(resourceTemplate.getUri())) {
					continue;
				}
			}
			System.out.println("Subhandle 5");
			if (vo.getResourceTemplate().getTags() != null || vo.getResourceTemplate().getTags().size() > 0) {
				Set<String> tagResource = new HashSet<String>();
				Set<String> tagTemp = new HashSet<String>();
				tagResource.addAll(vo.getResourceTemplate().getTags());
				tagTemp.addAll(resourceTemplate.getTags());
				if (!tagTemp.containsAll(tagResource)) {
					continue;
				}
			}
			System.out.println("Subhandle 6");
			if (vo.getResourceTemplate().getName() != "" &&vo.getResourceTemplate().getDescription() != "") {
				if (!(resourceTemplate.getName().contains(vo.getResourceTemplate().getName())&& resourceTemplate.getDescription().contains(vo.getResourceTemplate().getDescription()))) {
					continue;
				}
			}else{
				if (vo.getResourceTemplate().getName() == "" && !vo.getResourceTemplate().getDescription().contains(resourceTemplate.getDescription())) {
					continue;
				}
				
				if (vo.getResourceTemplate().getDescription() == "" && !resourceTemplate.getName().contains(vo.getResourceTemplate().getName())) {
					continue;
				}
			}
			
			System.out.println("Subhandle 7");
			vo.sendResource(resourceTemplate);
			
			
		}

	}
	public static void addSubscription(SubscribeVO sub)
	{

		subMap.put(sub.getId(), sub);
	}
	
	
	public static void unsubscribe(String id){
		boolean exists = subMap.containsKey(id);
		if(exists)
		{
			(subMap.get(id)).setDone();
			subMap.remove(id);
		}
	}
	
	public static boolean idExists(String id){
		boolean exists = subMap.containsKey(id);
		return exists;
	}
	
}
