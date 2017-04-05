package VO;

import java.util.List;

import server.DataObject;

public abstract class RequestVO extends AbstractVO {
	private String command;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public abstract List<String> execute(DataObject data);
}
