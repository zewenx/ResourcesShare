package server;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		JsonParser parser = new JsonParser();
		JsonObject jsonObject  =parser.parse(requestData).getAsJsonObject();
		
		JsonElement commandElement = jsonObject.get("command");
		String command = commandElement.toString().toLowerCase();
		RequestVO requestVO = null;
		if (command.contains("publish")) {
			requestVO = new Gson().fromJson(requestData, PublishVO.class);
		}else if (command.contains("remove")) {
			requestVO = new Gson().fromJson(requestData, RemoveVO.class);
		}else if (command.contains("share")) {
			requestVO = new Gson().fromJson(requestData, ShareVO.class);
		}else if (command.contains("query")) {
			requestVO = new Gson().fromJson(requestData, QueryVO.class);
		}else if (command.contains("fetch")) {
			requestVO = new Gson().fromJson(requestData, FetchVO.class);
		}else if (command.contains("exchange")) {
			requestVO = new Gson().fromJson(requestData, ExchangeVO.class);
		}
		
		List<String> responseList =  requestVO.execute(data);
		return responseList;
	}

}
