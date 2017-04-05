package VO;

import java.util.List;

public class RequestVO extends AbstractVO {
	private String command;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List<String> execute(){
		return null;
	}
}
