package com.smartbear.ready.plugin.jira.actions;

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.smartbear.ready.license.annotation.ClassRequiresLicense;
import com.smartbear.ready.license.protection.LicensedModule;
import com.smartbear.servicev.action.SvpActionGroups;

/**
 * Created by avdeev on 30.03.2015.
 */
@ActionConfiguration(actionGroup = SvpActionGroups.SVP_MODULE_TOOLBAR_COMPONENTS, targetType = ModelItem.class, isToolbarAction = true,
        iconPath = "com/smartbear/ready/plugin/jira/icons/Bug-tracker-icon_20-20-px.png")
@ClassRequiresLicense(validModules = LicensedModule.SoapUIPro)
public class ServiceVAction extends CreateNewBugAction{
}
