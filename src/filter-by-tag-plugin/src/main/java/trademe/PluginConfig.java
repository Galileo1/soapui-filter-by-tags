package trademe;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(groupId = "trademe.plugins", name = "filter-by-tag-plugin SoapUI Action", version = "0.1",
        autoDetect = true, description = "A simple plugin to filter testcases by tags",
        infoUrl = "varun gaur" )
public class PluginConfig extends PluginAdapter {
}
