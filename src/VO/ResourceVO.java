package VO;

import java.util.ArrayList;

public class ResourceVO extends AbstractVO {

	private String name;
	private ArrayList<String> tags;
	private String description;
	private String uri;
	private String channel;
	private String owner;
	private String ezserver;
	
	//constructor for ResourceVO, sets empty template variables
	public ResourceVO(){
		this.channel = "";
		this.owner = "";
		this.description = "";
		this.uri = "";
		this.name = "";
		this.tags = null;
		this.ezserver = null;
	}

	public String getName() {
		return name;
	}

	//sets name variable of the string and removes the null characters 
	//and leading/trailing whitespace
	public void setName(String name) {
		if(name != null){
			this.name = name.replace("\0", "").trim();
		}
	} 

	public ArrayList<String> getTags() {
		return tags;
	}
	//sets all tag variables and removes all null characters
	//and leading/trailing whitespace from each
	public void setTags(ArrayList<String> tags) {
		if(tags != null){
			for(String t : tags){
				t = t.replace("\0", "").trim();
			}
		}
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	//sets description variable and removes all null characters
	//and leading/trailing whitespace
	public void setDescription(String description) {
		if(description != null){
			this.description = description.replace("\0", "").trim();
		}
	}

	public String getUri() {
		return uri;
	}

	//sets uri variable and removes all null characters
	//and leading/trailing whitespace
	public void setUri(String uri) {
		if(uri != null){
			this.uri = uri.replace("\0", "").trim();
		}
	}

	public String getChannel() {
		return channel;
	}
	
	//sets channel variable and removes null characters
	//and leading/trailing whitespace
	public void setChannel(String channel) {
		if(channel != null){
			this.channel = channel.replace("\0", "").trim();
		}
	}

	public String getOwner() {
		return owner;
	}
	
	//sets owner variable and removes null characters
	//and leading/trailing whitespace
	public void setOwner(String owner) {
		if(owner != null){
			this.owner = owner.replace("\0", "").trim();
		}
	}

	public String getEzserver() {
		return ezserver;
	}
	
	//sets Ezserver variable and removes all null characters
	//and leading/trailing whitespace
	public void setEzserver(String ezserver) {
		if(ezserver != null){
			this.ezserver = ezserver.replace("\0", "").trim();
		}
	}

}
