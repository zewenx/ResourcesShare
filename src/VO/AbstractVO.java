package VO;

import com.google.gson.Gson;

public class AbstractVO {
	public String toJson(){
		return new Gson().toJson(this);
	}
}
