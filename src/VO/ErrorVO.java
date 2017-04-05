package VO;

public class ErrorVO extends ResponseVO{

	
	private String errorMessage;
	
	public ErrorVO() {
		setResponse("error");
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
