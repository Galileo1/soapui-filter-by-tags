package trademe;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.eviware.soapui.config.ProjectConfig;
import com.eviware.soapui.config.TagConfig;
import com.eviware.soapui.config.TagsConfig;
import com.eviware.soapui.impl.WorkspaceImpl;
import com.eviware.soapui.impl.wsdl.AbstractWsdlModelItem;
import com.eviware.soapui.impl.wsdl.WsdlTestSuite;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import com.eviware.soapui.support.components.ModelItemListDesktopPanel;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormField;
import com.eviware.x.form.XFormFieldListener;
import com.eviware.x.form.XFormOptionsField;
import com.eviware.x.form.support.ADialogBuilder;
import trademe.IDataStruct;


@ActionConfiguration(actionGroup = "WorkspaceImplActions")
public class MyAction extends AbstractSoapUIAction<WorkspaceImpl> {
	private XFormDialog dialog;
	public static final String STEP_NAME = "Name";
	public static final String EMPTY_STRING = "";
	public static final String ALL_PROJECTS = "All";

 	
	StringToStringMap dialogValues = new StringToStringMap();
	List<Project> workSpaceProjectList = new ArrayList<>();	
	Set<TagConfig> tagsForAllProject = new HashSet<>();
	List<IDataStruct> tagKeyValueAllProjects = new ArrayList<>();	
	String img ;
	
    public MyAction() {
        super("Workspace Tags", "A plugin action at the project level");
    }
    
	@Override
    public void perform(WorkspaceImpl workspace, Object o) {
		
		//get all the projects in workspace		
		workSpaceProjectList = workspace.getOpenProjectList();
		
		List<Project> workSpaceProjectsWithTags = getProjectsWithTags(workSpaceProjectList);
		
		if (workSpaceProjectList.isEmpty()){
			UISupport.showInfoMessage("No projects in Workspace. Import some project and try again!!");
			return;
		}
		
		//get the list of all the tags in all the projects
		tagsForAllProject = getTagsForAllProject(workSpaceProjectList);
		
		if (tagsForAllProject.isEmpty()){
			UISupport.showInfoMessage("Couldn't find any tags for projects in Workspace!!");
			return;
		}
		
		//prepare the map for all project tags key/set for later reference
		setMapKeyValueAllProject(workSpaceProjectList);
		
		//create the dialog with the found tag names and show
		buildCustomDialog(dialogValues, getUniqueTagNames(tagsForAllProject));    	
		try {
			dialog.show();
		} catch (Exception e) {
			//this is done because couple of time dialog has throw ArrayOutOfBound Exception
			UISupport.showErrorMessage("Unable to open plugin dialog. "+ e);
		}
    	
    	//return if the dialog in canceled
    	if( dialog.getReturnValue() != XFormDialog.OK_OPTION )
    		return;
    	    	 	
    	processUserInput(dialog,workSpaceProjectsWithTags);    	
    		        		
    }	

	private List<Project> getProjectsWithTags(List<Project> workSpaceProjectList) {
		List<Project> localWorkSpaceProjectsWithTags = new ArrayList<>();
		for (Project eachWSProject:  workSpaceProjectList) {
			@SuppressWarnings("unchecked")
			ProjectConfig wsProjectConfig = ((AbstractWsdlModelItem<ProjectConfig>) eachWSProject).getConfig();
			if (wsProjectConfig.getTags().getTagList().size() > 0) {
				localWorkSpaceProjectsWithTags.add(eachWSProject);
			}			
		}
		return localWorkSpaceProjectsWithTags;
	}

	private void processUserInput(XFormDialog dialog, List<Project> workSpaceProjectsWithTags) {
		Set<String> userInputTags = new HashSet<>();
		Set<String> userInputProject = new HashSet<>();
		XFormOptionsField tagsSelected = (XFormOptionsField) dialog.getFormField( CustomForm.TAGS);
    	XFormOptionsField projectSelected = (XFormOptionsField) dialog.getFormField( CustomForm.PROJECT);    	
    	
    	if(tagsSelected.getSelectedOptions().length <= 0){
    		UISupport.showErrorMessage("You need to select a Tag first."); 
    		return;
    	}
    	
    	for (Object project : projectSelected.getSelectedOptions()) {    		  	
    		userInputProject.add(project.toString());
    	}
    	
    	for (Object tags : tagsSelected.getSelectedOptions()) { 
    		userInputTags.add(tags.toString());
    	}
	
    	if (new ArrayList<String>(userInputProject).get(0) == ALL_PROJECTS) { 
    		int count = 0 ;
    		//UISupport.showInfoMessage(Integer.toString(workSpaceProjectsWithTags.size()),"Project will be called");
    		for (Project eachProject  : workSpaceProjectsWithTags) {
    			createListForTaggedCases(userInputTags, eachProject); 
    			count = count + 1;
    		}
    		//UISupport.showInfoMessage(Integer.toString(count),"Project has been called");
    	} else {
    		Project selectedProject = null;
    		for (Project eachProject  : workSpaceProjectsWithTags) {
    			if (eachProject.getName().equals(new ArrayList<String>(userInputProject).get(0))){
    				selectedProject = eachProject;
    			}
    		}
    		createListForTaggedCases(userInputTags, selectedProject); 
    	}
		
	}

	private void buildCustomDialog(StringToStringMap dialogValues, final Set<String> tagNames) {
		List<String> projectlist = new ArrayList<>();		
		projectlist.add(ALL_PROJECTS);		
		for (IDataStruct projects : tagKeyValueAllProjects){
			if (!projectlist.contains(projects.getProjectName())){ 
					projectlist.add(projects.getProjectName());
			}
		}
		
		dialog = ADialogBuilder.buildDialog(CustomForm.class);
		dialog.setOptions(CustomForm.PROJECT, projectlist.toArray());
		dialog.setOptions(CustomForm.TAGS, tagNames.toArray());		
		dialog.getFormField(CustomForm.PROJECT).addFormFieldListener(new XFormFieldListener() {
			public void valueChanged(XFormField sourceField, String newValue, String oldValue) {
				Set<String> changeTag = new HashSet<>();				
				if (newValue.equals(ALL_PROJECTS)){
					dialog.setOptions(CustomForm.TAGS, tagNames.toArray(new String[tagNames.size()]));
				} else {
					for (IDataStruct tagsIn : tagKeyValueAllProjects){
						if (newValue.equals(tagsIn.getProjectName())){
							changeTag.add(tagsIn.getTagName());
						}
					}
					dialog.setOptions(CustomForm.TAGS, changeTag.toArray(new String[changeTag.size()]));
				}
			}
		});
	}
							
	private Set<TagConfig>  getTagsforProject(Project wsdlProject) {
		@SuppressWarnings("unchecked")
		ProjectConfig config = ((AbstractWsdlModelItem<ProjectConfig>) wsdlProject).getConfig();		  	 
	    TagsConfig tags = config.getTags();
	    Set<TagConfig> tagList = new HashSet<TagConfig>(tags.getTagList());	   
	    return tagList;  
	}
	
	private Set<String> getUniqueTagNames(Set<TagConfig> tagsForAllProject) {
		List<String> tagNames = new ArrayList<>();
		for (TagConfig tag : tagsForAllProject) {
			tagNames.add(tag.getName());
		}		
		return new HashSet<>(tagNames);
	}
	
	private String getTagForDisplay(String keyOrValue) {
		for (TagConfig tag : tagsForAllProject){
			if (tag.getId().equals(keyOrValue)){
				return tag.getName();
			}
			if (tag.getName().equals(keyOrValue)){
				return tag.getId();
			}
    	}
		
		return keyOrValue;
	}	
	
	@SuppressWarnings("unchecked")
	private List<IDataStruct> setMapKeyValueAllProject(List<Project> wsdlProjectList) {
		for (Project wsdlProject : wsdlProjectList ) {
			ProjectConfig config = ((AbstractWsdlModelItem<ProjectConfig>) wsdlProject).getConfig();
			List<TagConfig> tagList = config.getTags().getTagList();
			for (TagConfig tag: tagList){			
				tagKeyValueAllProjects.add(new IDataStruct(tag.getName(),tag.getId(),wsdlProject.getName()));		
			}
		}
		return tagKeyValueAllProjects;
	}
	
	private Set<TagConfig> getTagsForAllProject(List<Project> projectList) {
		Set<TagConfig> allProjectTagList = new HashSet<>();
		for (Project eachProject : projectList) {
			allProjectTagList.addAll(getTagsforProject(eachProject));
		}
		return allProjectTagList;	
	}
	
	@SuppressWarnings("unchecked")
	private void createListForTaggedCases(Set<String> userInputTags, Project project) {		

		List<? extends WsdlTestSuite> testSuiteList = (List<? extends WsdlTestSuite>) project.getTestSuiteList();
		Set<String> userInputTagsValueSet = findValueSet(userInputTags,project);
		if (userInputTagsValueSet.isEmpty()) {			
			UISupport.showInfoMessage("Project " + project.getName() + " does not have following tags : " + String.join(",", userInputTags));
			return;
		}
		
		for (String eachUserInput : userInputTagsValueSet ) {
			Set<WsdlTestCase> markedTCList = new HashSet<>();
			List<ModelItem> taggedResult = new ArrayList<>();
			List<String> taggedResultname = new ArrayList<String>();
			for  (WsdlTestSuite ts : testSuiteList) {
				Set<WsdlTestCase> localTCList =  new HashSet<>(ts.getTestCaseList());
				for (WsdlTestCase tc : localTCList) {
					for (String tagId : tc.getTagIds() ) {						
						if (tagId.equals(eachUserInput)) {							
							markedTCList.add(tc);
						}
					}
				}
			}		
			taggedResult.addAll(markedTCList);
			if (taggedResult.size() > 0) {
				for (WsdlTestCase tc : markedTCList ) {
					taggedResultname.add(tc.getName());
				}			
			
				try {					
					UISupport.showDesktopPanel( new ModelItemListDesktopPanel( project.getName() + " Search Results." ,
							"The following tests are tagged as " + getTagForDisplay(eachUserInput).toUpperCase(),          
							taggedResult.toArray(new ModelItem[taggedResult.size()] )));
				} catch (Exception e) {
					
				}		
				
				
			} else {
				
				UISupport.showInfoMessage("Project " + project.getName() + " has "+ getTagForDisplay(eachUserInput).toUpperCase()+ " tag " +  
						".But none of the test cases are marked with this tag.");
				
			}
		}
	}
	
	private Set<String> findValueSet(Set<String> userInputTags, Project project) {
		Set<String> userInputTagsValues = new HashSet<>();	
		for (String userInputTag : userInputTags) {
			for (IDataStruct eachEntry : tagKeyValueAllProjects) {
				if (eachEntry.getTagName().equals(userInputTag) && eachEntry.getProjectName().equals(project.getName())) {
					userInputTagsValues.add(eachEntry.getTagId());					
				}				
			}
		}	
		return userInputTagsValues;			
	}
	
	
}



	


		
		

    

