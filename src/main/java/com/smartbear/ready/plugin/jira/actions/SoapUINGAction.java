package com.smartbear.ready.plugin.jira.actions;

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.eviware.soapui.plugins.ToolbarPosition;
import com.smartbear.ready.functional.actions.FunctionalActionGroups;

/**
 * Created by avdeev on 30.03.2015.
 */
@ActionConfiguration(actionGroup = FunctionalActionGroups.FUNCTIONAL_MODULE_TOOLBAR_ACTIONS, targetType = ModelItem.class, isToolbarAction = true,
        iconPath = CreateNewBugAction.PATH_TO_TOOLBAR_ICON, description = CreateNewBugAction.TOOLBAR_ACTION_DESCRIPTION)
public class SoapUINGAction extends CreateNewBugAction {
}
