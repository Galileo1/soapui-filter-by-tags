package trademe;

import com.eviware.x.form.support.AField;
import com.eviware.x.form.support.AField.AFieldType;
import com.eviware.x.form.support.AForm;


@AForm(name = "Search TestCases Using Tags", description = "Specify options for filtering the TestCase", icon="/src/main/java/res/Kevin_Pose-02.png")
public interface CustomForm {
	
    @AField( description = "Specifies the projects in workspace", name = "Project", type = AFieldType.ENUMERATION )
    public final static String PROJECT = "Project";
    
    @AField( description = "Specifies tags in project", name = "Tags", type = AFieldType.MULTILIST )
    public final static String TAGS = "Tags";

}
