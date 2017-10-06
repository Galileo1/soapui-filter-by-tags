package trademe;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(groupId = "trademe.plugins", name = "filter-by-tag-plugin SoapUI Action", version = "1.0",
        autoDetect = true, description = "A simple plugin to filter test cases by tags",
        infoUrl = "https://github.com/Galileo1/soapui-filter-by-tags" )
public class PluginConfig extends PluginAdapter {
}
