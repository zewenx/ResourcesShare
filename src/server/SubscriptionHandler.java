package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.sun.corba.se.spi.activation.Server;

import VO.RequestVO;
import VO.ResourceVO;
import VO.ServerVO;
import VO.SubscribeVO;
import VO.UnsubscribeVO;
import client.SecureConnectionThread;
import client.ConnectionThread;

public class SubscriptionHandler {
	private static Map<String, SubscribeVO> subMap = DataObject.getSublist();
	private static Map<String, List<Thread>> relaySubMap = new HashMap<String, List<Thread>>();

	public static void checkResource(ResourceVO resourceTemplate) {
		for (SubscribeVO vo : subMap.values()) {
			if (!vo.getResourceTemplate().getChannel().equals("")) {
				if (!vo.getResourceTemplate().getChannel().equals(resourceTemplate.getChannel())) {
					continue;
				}
			}
			if (!vo.getResourceTemplate().getOwner().equals("")) {
				if (!vo.getResourceTemplate().getOwner().equals(resourceTemplate.getOwner())) {
					continue;
				}
			}
			if (!vo.getResourceTemplate().equals(null) && !vo.getResourceTemplate().getUri().equals("")) {
				System.out.println(resourceTemplate.getUri());
				System.out.println(vo.getResourceTemplate().getUri());
				if (!vo.getResourceTemplate().getUri().equals(resourceTemplate.getUri())) {
					continue;
				}
			}
			if (vo.getResourceTemplate().getTags() != null && vo.getResourceTemplate().getTags().size() > 0) {
				Set<String> tagResource = new HashSet<String>();
				Set<String> tagTemp = new HashSet<String>();
				tagResource.addAll(vo.getResourceTemplate().getTags());
				tagTemp.addAll(resourceTemplate.getTags());
				if (!tagTemp.containsAll(tagResource)) {
					continue;
				}
			}
			if (vo.getResourceTemplate().getName() != "" && vo.getResourceTemplate().getDescription() != "") {
				if (!(resourceTemplate.getName().contains(vo.getResourceTemplate().getName())
						&& resourceTemplate.getDescription().contains(vo.getResourceTemplate().getDescription()))) {
					continue;
				}
			} else {
				if (vo.getResourceTemplate().getName() == ""
						&& !vo.getResourceTemplate().getDescription().contains(resourceTemplate.getDescription())) {
					continue;
				}

				if (vo.getResourceTemplate().getDescription() == ""
						&& !resourceTemplate.getName().contains(vo.getResourceTemplate().getName())) {
					continue;
				}
			}
			vo.sendResource(resourceTemplate);

		}

	}

	public static void addSubscription(SubscribeVO sub, DataObject data) {

		subMap.put(sub.getId(), sub);
		List<Thread> connectionThreads = new ArrayList<Thread>();
		if (sub.isRelay()) {
			if (data.isSecureConnection) {
				for (ServerVO vo : data.getSecureServerList()) {

					Thread connectionThread = new Thread(new SecureSubscriptionRelayThread(vo, sub, EZShare.Server.debug));
					connectionThread.start();

				}
			} else {
				for (ServerVO vo : data.getServerList()) {
					Thread connectionThread = new Thread(new SubscriptionRelayThread(vo, sub, EZShare.Server.debug));
					connectionThread.start();
					connectionThreads.add(connectionThread);
				}
			}
			relaySubMap.put(sub.getId(), connectionThreads);
		}

	}

	public static void unsubscribe(String id,DataObject data) {
		boolean exists = subMap.containsKey(id);
		if (exists) {
			if(relaySubMap.containsKey(id)){
				//setup unsubscribe variable object
				UnsubscribeVO vo = new UnsubscribeVO();
				vo.setCommand("UNSUBSCRIBE");
				vo.setId(id);
				
				for(ServerVO server : data.serverList){
					new Thread(new client.ConnectionThread(server.getHostname(), server.getPort(), vo, EZShare.Server.debug));
				}
				for(ServerVO server : data.getSecureServerList()){
					new Thread(new client.SecureConnectionThread(server.getHostname(), server.getPort(), vo, EZShare.Server.debug));
				}
			}
			(subMap.get(id)).setDone();
			subMap.remove(id);
		}
	}

	public static boolean idExists(String id) {
		boolean exists = subMap.containsKey(id);
		return exists;
	}

}
