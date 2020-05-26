package com.smartbear.ready.plugin.jira;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(groupId = "com.smartbear.ready.plugins",
        name = "JIRA Integration Plugin", version = "1.4",
        autoDetect = true, description = "Creates JIRA items for failed tests directly from the ReadyAPI IDE.",
        infoUrl = "https://github.com/smartbear/ready-api-plugin-jira")
public class PluginConfig extends PluginAdapter {
    @Override
    public boolean isActive() {
        return !SoapUI.isCommandLine();
    }
}
