package com.smartbear.ready.plugin.jira;

import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(groupId = "com.smartbear.ready.plugins",
        name = "JIRA Integration Plugin", version = "1.0",
        autoDetect = true, description = "Creates JIRA items for failed tests directly from the Ready! API IDE.",
        infoUrl = "https://github.com/smartbear/ready-api-plugin-jira")
public class PluginConfig extends PluginAdapter {
}
