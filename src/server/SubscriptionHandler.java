package server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.sun.corba.se.spi.activation.Server;

import VO.ResourceVO;
import VO.ServerVO;
import VO.SubscribeVO;
import client.SecureConnectionThread;
import client.ConnectionThread;

public class SubscriptionHandler {
	private static Map<String, SubscribeVO> subMap = DataObject.getSublist();
	private static Map<String, Thread> relaySubMap = new HashMap<String, Thread>();

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
			if (vo.getResourceTemplate().getTags() != null || vo.getResourceTemplate().getTags().size() > 0) {
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
		Thread connectionThread = null;
		if (sub.isRelay()) {
			if (data.isSecureConnection) {
				for (ServerVO vo : data.getSecureServerList()) {
					connectionThread = new Thread(new SecureConnectionThread(vo.getHostname(),vo.getPort(), sub, EZShare.Server.debug));
					connectionThread.start();
				}
			} else {
				for (ServerVO vo : data.getServerList()) {
					connectionThread = new Thread(new ConnectionThread(vo.getHostname(),vo.getPort(), sub, EZShare.Server.debug));
					connectionThread.start();
				}
			}
			relaySubMap.put(sub.getId(), connectionThread);
		}

	}

	public static void unsubscribe(String id) {
		boolean exists = subMap.containsKey(id);
		if (exists) {
			(subMap.get(id)).setDone();
			subMap.remove(id);
		}
	}

	public static boolean idExists(String id) {
		boolean exists = subMap.containsKey(id);
		return exists;
	}

}
