package com.smartbear.ready.plugin.jira;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(groupId = "com.smartbear.ready.plugins",
        name = "Ready! API Jira Plugin", version = "1.0",
        autoDetect = true, description = "Jira Ready! API Plugin",
        infoUrl = "https://github.com/smartbear/ready-api-plugin-jira")
public class PluginConfig extends PluginAdapter {
}
