package server;

import java.util.List;

import com.google.gson.Gson;

import VO.AbstractVO;
import VO.ExchangeVO;
import VO.FetchVO;
import VO.PublishVO;
import VO.QueryVO;
import VO.RemoveVO;
import VO.RequestVO;
import VO.ShareVO;
import netscape.javascript.JSObject;

public class CommandExecutor {
	static private CommandExecutor mCommandExecutor;
	
	private CommandExecutor() {
	
	}
	
	static CommandExecutor init(){
		if (mCommandExecutor == null) {
			mCommandExecutor = new CommandExecutor();
		}
		return mCommandExecutor;
	}

	synchronized List<String> submit(String requestData) {
		RequestVO requestVO = new Gson().fromJson(requestData, RequestVO.class);
//		String command = requestVO.getCommand();
//		if (command.equals("publish")) {
//			PublishVO vo = new Gson().fromJson(requestData, PublishVO.class);
//			publish(vo);
//		}else if (command.equals("remove")) {
//			RemoveVO vo = new Gson().fromJson(requestData, RemoveVO.class);
//			remove(vo);
//		}else if (command.equals("share")) {
//			ShareVO vo = new Gson().fromJson(requestData, ShareVO.class);
//			share(vo);
//		}else if (command.equals("query")) {
//			QueryVO vo = new Gson().fromJson(requestData, QueryVO.class);
//			query(vo);
//		}else if (command.equals("fetch")) {
//			FetchVO vo = new Gson().fromJson(requestData, FetchVO.class);
//			fetch(vo);
//		}else if (command.equals("exchange")) {
//			ExchangeVO vo = new Gson().fromJson(requestData, ExchangeVO.class);
//			exchange(vo);
//		}
//		
		List<String> responseList =  requestVO.execute();
		return responseList;
	}

//	private void exchange(ExchangeVO vo) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void fetch(FetchVO vo) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void query(QueryVO vo) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void share(ShareVO vo) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void remove(RemoveVO vo) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	private void publish(PublishVO vo) {
//		// TODO Auto-generated method stub
//		System.out.println(vo.toJson());
//	}

}
