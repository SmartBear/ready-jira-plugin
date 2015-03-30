package com.smartbear.ready.plugin.jira.actions;

/**
 * Created by avdeev on 30.03.2015.
 */

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.smartbear.ready.license.annotation.ClassRequiresLicense;
import com.smartbear.ready.license.protection.LicensedModule;
import com.smartbear.ready.security.actions.SecurityActionGroups;


@ActionConfiguration(actionGroup = SecurityActionGroups.SECURITY_MODULE_TOOLBAR_ACTIONS, targetType = ModelItem.class, isToolbarAction = true,
        iconPath = "com/smartbear/ready/plugin/jira/icons/Create-new-bug-tracker-issue-icon_20-20-px.png")
@ClassRequiresLicense(validModules = LicensedModule.SoapUIPro)
public class SecurityAction extends CreateNewBugAction{
}
