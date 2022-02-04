package com.smartbear.ready.plugin.jira;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.plugins.PluginAdapter;
import com.eviware.soapui.plugins.PluginConfiguration;

@PluginConfiguration(groupId = "com.smartbear.ready.plugins",
        name = "JIRA Integration Plugin", version = "1.6.3",
        autoDetect = true, description = "Creates JIRA items for failed tests directly from the ReadyAPI IDE.",
        infoUrl = "https://github.com/smartbear/ready-api-plugin-jira",
        minimumReadyApiVersion = "3.20.1")
public class PluginConfig extends PluginAdapter {
    @Override
    public boolean isActive() {
        return !SoapUI.isCommandLine();
    }
}
