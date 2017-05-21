package VO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion.NestedParameters;

import server.DataObject;

public class SubscribeVO extends RequestVO{
	boolean relay;
	private ResourceVO resourceTemplate;
	private String id = "";
	private DataInputStream in;
	private DataOutputStream out;
	private int resourceCount = 0;
	
	public boolean isRelay() {
		return relay;
	}
	
	public void setInputOutputStream(DataInputStream in,DataOutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	public void setID(String id){
		this.id = id;
	}
	public boolean isResourceSame(ResourceVO resource)
	{
		return resourceTemplate.equals(resource);
	}
	public void setRelay(boolean relay) {
		this.relay = relay;
	}
	public ResourceVO getResourceTemplate() {
		return resourceTemplate;
	}
	public String getId() {
		return id;
	}

	public void setResource(ResourceVO resourceTemplate) {
		this.resourceTemplate = resourceTemplate;
	}
	@Override
	public List<String> execute(DataObject data) {
		List<String> responseList = new ArrayList<String>();
		//Error Handling
		if(this.id.equals("")){
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("missing id");
			responseList.add(vo.toJson());
			return responseList;
		}
		if(data.isSubIdinUse(id)){
			ErrorVO vo = new ErrorVO();
			vo.setErrorMessage("id currently in use");
			responseList.add(vo.toJson());
			return responseList;
		}
		
		//Process
		data.addSubscriber(id, this);
		SuccessVO successVO = new SuccessVO();
		try{
			out.writeUTF(successVO.toJson());
	
			//need to keep thread open here and wait for responses
			
			while(true){
				
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return responseList;
		
	}
	
	public void sendResource(ResourceVO vo){
		resourceCount++;
		try{
			System.out.println("gfdsifjdslfk");
			out.writeUTF(vo.toJson());
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
