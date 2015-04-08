package com.smartbear.ready.plugin.jira.actions;

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.smartbear.servicev.action.SvpActionGroups;

/**
 * Created by avdeev on 30.03.2015.
 */
@ActionConfiguration(actionGroup = SvpActionGroups.SVP_MODULE_TOOLBAR_COMPONENTS, targetType = ModelItem.class, isToolbarAction = true,
        iconPath = CreateNewBugAction.PATH_TO_TOOLBAR_ICON, description = CreateNewBugAction.TOOLBAR_ACTION_DESCRIPTION)
public class ServiceVAction extends CreateNewBugAction{
}
