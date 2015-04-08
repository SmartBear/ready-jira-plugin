package com.smartbear.ready.plugin.jira.actions;

/**
 * Created by avdeev on 30.03.2015.
 */

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.smartbear.ready.security.actions.SecurityActionGroups;


@ActionConfiguration(actionGroup = SecurityActionGroups.SECURITY_MODULE_TOOLBAR_ACTIONS, targetType = ModelItem.class, isToolbarAction = true,
        iconPath = CreateNewBugAction.PATH_TO_TOOLBAR_ICON, description = CreateNewBugAction.TOOLBAR_ACTION_DESCRIPTION)
public class SecurityAction extends CreateNewBugAction{
}
