package trademe;

public class IDataStruct {
	private String tagName;	
	private String tagId;
	private String projectName;

	public IDataStruct(String tagName, String tagId, String projectName){
		this.setTagName(tagName);
		this.setTagId(tagId);
		this.setProjectName(projectName);
	}
	
	private void setTagId(String tagId) {
		this.tagId = tagId;		
	}
	
	private void setProjectName(String projectName) {
		this.projectName = projectName;		
	}
	
	private void setTagName(String tagName) {
		this.tagName = tagName;		
	}

	public String getTagName(){
		return this.tagName;
	}
	
	public String getTagId(){
		return this.tagId;
		
	}

	public String getProjectName(){
		return this.projectName;
	}
	
}
