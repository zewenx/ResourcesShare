package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

import org.apache.commons.io.output.ThresholdingOutputStream;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.regexp.internal.recompile;

import EZShare.Server;
import VO.AbstractVO;
import VO.ExchangeVO;
import VO.FetchVO;
import VO.PublishVO;
import VO.QueryVO;
import VO.RemoveVO;
import VO.RequestVO;
import VO.SecureExchangeVO;
import VO.SecureQueryVO;
import VO.ShareVO;
import VO.SubscribeVO;
import VO.UnsubscribeVO;
import netscape.javascript.JSObject;

public class CommandExecutor {
	static private CommandExecutor mCommandExecutor;
	private DataObject data;
	private DataInputStream in;
	private DataOutputStream out;
	
	
	private CommandExecutor() {
		data = new DataObject();
		data.setSecret(Server.parameters.get(Commands.secret));
	}
	
	public void setInputOutputStream(DataInputStream in,DataOutputStream out){
		this.in = in;
		this.out = out;
	}

	
	static CommandExecutor init() {
		if (mCommandExecutor == null) {
			mCommandExecutor = new CommandExecutor();
		}
		return mCommandExecutor;
	}

	public DataObject getDataObject() {
		return data;
	}

	synchronized List<String> submit(String requestData, boolean isSecureSocket) {
		data.setSecureConnection(isSecureSocket);
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(requestData).getAsJsonObject();

		JsonElement commandElement = jsonObject.get("command");
		String command = commandElement.toString().toLowerCase();
		RequestVO requestVO = null;
		if (command.contains("publish")) {
			requestVO = new Gson().fromJson(requestData, PublishVO.class);
		} else if (command.contains("remove")) {
			requestVO = new Gson().fromJson(requestData, RemoveVO.class);
		} else if (command.contains("share")) {
			requestVO = new Gson().fromJson(requestData, ShareVO.class);

		}else if (command.contains("subscribe")) {
			requestVO = new Gson().fromJson(requestData, SubscribeVO.class);
			((SubscribeVO) requestVO).setInputOutputStream(in,out);
		}else if (command.contains("unsubscribe")) {
			requestVO = new Gson().fromJson(requestData, UnsubscribeVO.class);

		} else if (command.contains("query")) {
			if (isSecureSocket) {
				requestVO = new Gson().fromJson(requestData, SecureQueryVO.class);
			} else {
				requestVO = new Gson().fromJson(requestData, QueryVO.class);
			}
		} else if (command.contains("fetch")) {
			requestVO = new Gson().fromJson(requestData, FetchVO.class);
		} else if (command.contains("exchange")) {
			if (isSecureSocket) {
				requestVO = new Gson().fromJson(requestData, SecureExchangeVO.class);
			} else {
				requestVO = new Gson().fromJson(requestData, ExchangeVO.class);
			}
		}

		List<String> responseList = requestVO.execute(data);
		return responseList;
	}

}
