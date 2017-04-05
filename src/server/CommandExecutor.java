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
	private DataObject data;
	
	private CommandExecutor() {
		data = new DataObject();
	}
	
	static CommandExecutor init(){
		if (mCommandExecutor == null) {
			mCommandExecutor = new CommandExecutor();
		}
		return mCommandExecutor;
	}

	synchronized List<String> submit(String requestData) {
		RequestVO requestVO = new Gson().fromJson(requestData, RequestVO.class);
		String command = requestVO.getCommand();
		if (command.equals("publish")) {
			requestVO = new Gson().fromJson(requestData, PublishVO.class);
		}else if (command.equals("remove")) {
			requestVO = new Gson().fromJson(requestData, RemoveVO.class);
		}else if (command.equals("share")) {
			requestVO = new Gson().fromJson(requestData, ShareVO.class);
		}else if (command.equals("query")) {
			requestVO = new Gson().fromJson(requestData, QueryVO.class);
		}else if (command.equals("fetch")) {
			requestVO = new Gson().fromJson(requestData, FetchVO.class);
		}else if (command.equals("exchange")) {
			requestVO = new Gson().fromJson(requestData, ExchangeVO.class);
		}
		
		List<String> responseList =  requestVO.execute(data);
		return responseList;
	}

}
