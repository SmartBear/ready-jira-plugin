package com.smartbear.ready.plugin.jira.actions;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.x.form.XForm;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormDialogBuilder;
import com.eviware.x.form.XFormFactory;
import com.eviware.x.form.XFormOptionsField;
import com.google.inject.Inject;
import com.smartbear.ready.functional.actions.FunctionalActionGroups;
import com.smartbear.ready.plugin.jira.dialog.BugInfoDialogConsts;
import com.smartbear.ready.plugin.jira.impl.BugTrackerAttachmentCreationResult;
import com.smartbear.ready.plugin.jira.impl.BugTrackerIssueCreationResult;
import com.smartbear.ready.plugin.jira.impl.IssueInfoDialog;
import com.smartbear.ready.plugin.jira.impl.JiraApiCallResult;
import com.smartbear.ready.plugin.jira.impl.JiraProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ActionConfiguration(actionGroup = FunctionalActionGroups.FUNCTIONAL_MODULE_TOOLBAR_ACTIONS, targetType = ModelItem.class, isToolbarAction = true,
        iconPath = "com/smartbear/ready/plugin/jira/icons/Create-new-bug-tracker-issue-icon_20-20-px.png")
public class CreateNewBugAction extends AbstractSoapUIAction<ModelItem> {
    private XFormDialog dialog;
    HashMap<String, CimFieldInfo> requiredFields = new HashMap<>();
    @Inject
    public CreateNewBugAction() {
        super("Create new bug", "Specifies the required fields to create bug in Jira");
    }

    @Override
    public void perform(ModelItem target, Object o) {
        JiraProvider bugTrackerProvider = JiraProvider.getProvider();
        if (!bugTrackerProvider.settingsComplete()){
            UISupport.showErrorMessage("Bug tracker settings are not completely specified.");
            return;
        }
        bugTrackerProvider.setActiveItem(target);//TODO: check if it's really target
        dialog = createAndInitBugInfoDialog(bugTrackerProvider);
        if (dialog.show()){
            StringToStringMap values = dialog.getValues();
            String summary = values.get(BugInfoDialogConsts.ISSUE_SUMMARY, null);
            String description = values.get(BugInfoDialogConsts.ISSUE_DESCRIPTION, null);
            String projectKey = values.get(BugInfoDialogConsts.TARGET_ISSUE_PROJECT, null);
            String issueType = values.get(BugInfoDialogConsts.ISSUE_TYPE, null);
            String priority = values.get(BugInfoDialogConsts.ISSUE_PRIORITY, null);
            Map<String, String> extraValues = new HashMap<String, String>();
            for (Map.Entry<String, CimFieldInfo> entry:requiredFields.entrySet()){
                extraValues.put(entry.getKey(), values.get(entry.getValue().getName()));
            }
            BugTrackerIssueCreationResult result = bugTrackerProvider.createIssue(projectKey, issueType, priority, summary, description, extraValues);
            if (result.getSuccess()){
                boolean isAttachmentSuccess = true;
                StringBuilder resultError = new StringBuilder();
                if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_SOAPUI_LOG)){
                    BugTrackerAttachmentCreationResult attachResult = bugTrackerProvider.attachFile(bugTrackerProvider.getIssue(result.getIssue().getKey()).getAttachmentsUri(), bugTrackerProvider.getActiveItemName() + ".log", bugTrackerProvider.getSoapUIExecutionLog());
                    if (!attachResult.getSuccess()){
                        isAttachmentSuccess = false;
                        resultError.append(attachResult.getError());
                        resultError.append("\r\n");
                    }
                }

                if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_LOADUI_LOG)){
                    BugTrackerAttachmentCreationResult attachResult = bugTrackerProvider.attachFile(bugTrackerProvider.getIssue(result.getIssue().getKey()).getAttachmentsUri(), bugTrackerProvider.getActiveItemName() + ".log", bugTrackerProvider.getLoadUIExecutionLog());
                    if (!attachResult.getSuccess()){
                        isAttachmentSuccess = false;
                        resultError.append(attachResult.getError());
                        resultError.append("\r\n");
                    }
                }

                if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_SERVICEV_LOG)){
                    BugTrackerAttachmentCreationResult attachResult = bugTrackerProvider.attachFile(bugTrackerProvider.getIssue(result.getIssue().getKey()).getAttachmentsUri(), bugTrackerProvider.getActiveItemName() + ".log", bugTrackerProvider.getServiceVExecutionLog());
                    if (!attachResult.getSuccess()){
                        isAttachmentSuccess = false;
                        resultError.append(attachResult.getError());
                        resultError.append("\r\n");
                    }
                }

                if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_READYAPI_LOG)){
                    BugTrackerAttachmentCreationResult attachResult = bugTrackerProvider.attachFile(bugTrackerProvider.getIssue(result.getIssue().getKey()).getAttachmentsUri(), bugTrackerProvider.getActiveItemName() + ".log", bugTrackerProvider.getReadyApiLog());
                    if (!attachResult.getSuccess()){
                        isAttachmentSuccess = false;
                        resultError.append(attachResult.getError());
                        resultError.append("\r\n");
                    }
                }

                if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_PROJECT)){
                    BugTrackerAttachmentCreationResult attachResult = bugTrackerProvider.attachFile(bugTrackerProvider.getIssue(result.getIssue().getKey()).getAttachmentsUri(), bugTrackerProvider.getActiveItemName() + ".xml", bugTrackerProvider.getRootProject());
                    if (!attachResult.getSuccess()){
                        isAttachmentSuccess = false;
                        resultError.append(attachResult.getError());
                    }
                }

                if (!isAttachmentSuccess){
                    UISupport.showErrorMessage(resultError.toString());
                } else {
                    IssueInfoDialog.showDialog(issueType, bugTrackerProvider.getBugTrackerSettings().getUrl().concat("/browse/").concat(result.getIssue().getKey()), result.getIssue().getKey());//TODO: make link correct for all cases
                }

            } else {
                UISupport.showErrorMessage(result.getError());
            }
        }
    }

    private Object[] transformIterableValues (Iterable<Object> input){
        ArrayList<Object> objects = new ArrayList<>();
        for (Object obj:input){
            if (obj instanceof BasicComponent) {
                objects.add(((BasicComponent) obj).getName());
            }
        }
        return objects.toArray();
    }

    XFormDialog createAndInitBugInfoDialog(JiraProvider bugTrackerProvider){
        XFormDialogBuilder builder = XFormFactory.createDialogBuilder("Create new bug");
        XForm form = builder.createForm("Basic");
        String selectedProject = (String)bugTrackerProvider.getAvailableProjects().toArray()[0];
        XFormOptionsField projectsCombo = form.addComboBox(BugInfoDialogConsts.TARGET_ISSUE_PROJECT, bugTrackerProvider.getAvailableProjects().toArray(), selectedProject);
        String selectedIssueType = (String)bugTrackerProvider.getAvailableIssueTypes(selectedProject).toArray()[0];
        XFormOptionsField issueTypesCombo = form.addComboBox(BugInfoDialogConsts.ISSUE_TYPE, bugTrackerProvider.getAvailableIssueTypes(selectedProject).toArray(), selectedIssueType);
        String selectedPriority = (String)bugTrackerProvider.getPriorities().toArray()[0];
        XFormOptionsField prioritiesCombo = form.addComboBox(BugInfoDialogConsts.ISSUE_PRIORITY, bugTrackerProvider.getPriorities().toArray(), selectedPriority);
        form.addTextField(BugInfoDialogConsts.ISSUE_SUMMARY, "Bug summary", XForm.FieldType.TEXT);
        form.addTextField(BugInfoDialogConsts.ISSUE_DESCRIPTION, "Bug description", XForm.FieldType.TEXTAREA);
        JiraApiCallResult<Map<String, CimFieldInfo>>requiredFields = bugTrackerProvider.getRequiredFields(selectedProject, selectedIssueType);
        if (requiredFields.isSuccess()){
            for (Map.Entry<String, CimFieldInfo> entry:requiredFields.getResult().entrySet()){
                String key = entry.getKey();
                if (key.equals("summary") || key.equals("project") || key.equals("issuetype") || key.equals("description")){
                    continue;
                }
                CimFieldInfo fieldInfo = entry.getValue();
                if (fieldInfo.getAllowedValues() != null){
                    Object [] values = transformIterableValues(fieldInfo.getAllowedValues());
                    form.addComboBox(fieldInfo.getName(), values, (String)values[0]);
                } else {
                    form.addTextField(fieldInfo.getName(), fieldInfo.getName(), XForm.FieldType.TEXT);
                }
                this.requiredFields.put(fieldInfo.getId(), fieldInfo);
            }
        }
        form.addCheckBox(BugInfoDialogConsts.ATTACH_SOAPUI_LOG, "");
        form.addCheckBox(BugInfoDialogConsts.ATTACH_READYAPI_LOG, "");
        form.addCheckBox(BugInfoDialogConsts.ATTACH_PROJECT, "");
        return builder.buildDialog(builder.buildOkCancelActions(), "Specify options", null);
    }

    @Override
    public boolean shouldBeEnabledFor(ModelItem modelItem) {
        if (modelItem instanceof WsdlProject || modelItem instanceof TestCase ||
                modelItem instanceof TestSuite || modelItem instanceof TestStep){
            return true;
        }

        return false;
    }

}
