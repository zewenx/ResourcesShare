package VO;

import java.lang.reflect.Executable;
import java.util.List;

import com.google.gson.Gson;

public class AbstractVO {
	public String toJson(){
		return new Gson().toJson(this);
	}
	
	
}
