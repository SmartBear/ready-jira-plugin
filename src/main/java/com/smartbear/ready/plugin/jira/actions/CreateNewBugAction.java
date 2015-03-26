package com.smartbear.ready.plugin.jira.actions;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.plugins.ActionConfiguration;
import com.eviware.soapui.support.StringUtils;
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
import com.smartbear.ready.plugin.jira.impl.AttachmentAddingResult;
import com.smartbear.ready.plugin.jira.impl.IssueCreationResult;
import com.smartbear.ready.plugin.jira.impl.IssueInfoDialog;
import com.smartbear.ready.plugin.jira.impl.JiraProvider;
import com.smartbear.ready.plugin.jira.impl.Utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActionConfiguration(actionGroup = FunctionalActionGroups.FUNCTIONAL_MODULE_TOOLBAR_ACTIONS, targetType = ModelItem.class, isToolbarAction = true,
        iconPath = "com/smartbear/ready/plugin/jira/icons/Create-new-bug-tracker-issue-icon_20-20-px.png")
public class CreateNewBugAction extends AbstractSoapUIAction<ModelItem> {
    private static String NEW_ISSUE_DIALOG_CAPTION = "Create new Jira issue";

    private XFormDialog dialog;

    HashMap<String, CimFieldInfo> requiredFields = new HashMap<>();


    @Inject
    public CreateNewBugAction() {
        super("Create Jira issue", "Specifies the required fields to create new issue in Jira");
    }

    @Override
    public void perform(ModelItem target, Object o) {
        JiraProvider bugTrackerProvider = JiraProvider.getProvider();
        if (!bugTrackerProvider.settingsComplete()){
            UISupport.showErrorMessage("Bug tracker settings are not completely specified.");
            return;
        }
        bugTrackerProvider.setActiveItem(target);//TODO: check if it's really target
        List<String> projects = bugTrackerProvider.getListOfAllProjects();
        if (projects == null || projects.size() == 0){
            UISupport.showErrorMessage("No available Jira projects.");
            return;
        }
        String selectedProject = (String)projects.toArray()[0];
        dialog = createAndInitBugInfoDialog(bugTrackerProvider, selectedProject);
        if (dialog.show()){
            handleOk(bugTrackerProvider);
        }
    }

    private void handleOk (JiraProvider bugTrackerProvider){
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
        IssueCreationResult result = bugTrackerProvider.createIssue(projectKey, issueType, priority, summary, description, extraValues);
        if (result.getSuccess()){
            boolean isAttachmentSuccess = true;
            URI newIssueAttachURI = bugTrackerProvider.getIssue(result.getIssue().getKey()).getAttachmentsUri();
            String bugTrackerActiveItemName = bugTrackerProvider.getActiveItemName();
            StringBuilder resultError = new StringBuilder();
            if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_SOAPUI_LOG)){
                AttachmentAddingResult attachResult = bugTrackerProvider.attachFile(newIssueAttachURI, bugTrackerActiveItemName + ".log", bugTrackerProvider.getSoapUIExecutionLog());
                if (!attachResult.getSuccess()){
                    isAttachmentSuccess = false;
                    resultError.append(attachResult.getError());
                    resultError.append("\r\n");
                }
            }

            if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_LOADUI_LOG)){
                AttachmentAddingResult attachResult = bugTrackerProvider.attachFile(newIssueAttachURI, bugTrackerActiveItemName + ".log", bugTrackerProvider.getLoadUIExecutionLog());
                if (!attachResult.getSuccess()){
                    isAttachmentSuccess = false;
                    resultError.append(attachResult.getError());
                    resultError.append("\r\n");
                }
            }

            if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_SERVICEV_LOG)){
                AttachmentAddingResult attachResult = bugTrackerProvider.attachFile(newIssueAttachURI, bugTrackerActiveItemName + ".log", bugTrackerProvider.getServiceVExecutionLog());
                if (!attachResult.getSuccess()){
                    isAttachmentSuccess = false;
                    resultError.append(attachResult.getError());
                    resultError.append("\r\n");
                }
            }

            if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_READYAPI_LOG)){
                AttachmentAddingResult attachResult = bugTrackerProvider.attachFile(newIssueAttachURI, bugTrackerActiveItemName + ".log", bugTrackerProvider.getReadyApiLog());
                if (!attachResult.getSuccess()){
                    isAttachmentSuccess = false;
                    resultError.append(attachResult.getError());
                    resultError.append("\r\n");
                }
            }

            if (dialog.getBooleanValue(BugInfoDialogConsts.ATTACH_PROJECT)){
                AttachmentAddingResult attachResult = bugTrackerProvider.attachFile(newIssueAttachURI, bugTrackerActiveItemName + ".xml", bugTrackerProvider.getRootProject());
                if (!attachResult.getSuccess()){
                    isAttachmentSuccess = false;
                    resultError.append(attachResult.getError());
                    resultError.append("\r\n");
                }
            }

            String attachAnyFileValue = dialog.getValue(BugInfoDialogConsts.ATTACH_ANY_FILE);
            if (!StringUtils.isNullOrEmpty(dialog.getValue(BugInfoDialogConsts.ATTACH_ANY_FILE))){
                AttachmentAddingResult attachResult = bugTrackerProvider.attachFile(newIssueAttachURI, attachAnyFileValue);
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

    private void addRequiredFields (XForm baseDialog, JiraProvider bugTrackerProvider, String selectedProject, String selectedIssueType){
        Map<String, CimFieldInfo>requiredFields = bugTrackerProvider.getRequiredFieldsForProjectIssue(selectedProject, selectedIssueType);
        if (requiredFields != null){
            for (Map.Entry<String, CimFieldInfo> entry:requiredFields.entrySet()){
                String key = entry.getKey();
                if (key.equals("summary") || key.equals("project") || key.equals("issuetype") || key.equals("description")){
                    continue;
                }
                CimFieldInfo fieldInfo = entry.getValue();
                if (fieldInfo.getAllowedValues() != null){
                    Object [] values = Utils.IterableValuesToArray(fieldInfo.getAllowedValues());
                    baseDialog.addComboBox(fieldInfo.getName(), values, (String)values[0]);
                } else {
                    baseDialog.addTextField(fieldInfo.getName(), fieldInfo.getName(), XForm.FieldType.TEXT);
                }
                this.requiredFields.put(fieldInfo.getId(), fieldInfo);
            }
        }
    }

    private XFormDialog createAndInitBugInfoDialog(JiraProvider bugTrackerProvider, String selectedProject){
        XFormDialogBuilder builder = XFormFactory.createDialogBuilder(NEW_ISSUE_DIALOG_CAPTION);
        XForm form = builder.createForm("Basic");
        XFormOptionsField projectsCombo = form.addComboBox(BugInfoDialogConsts.TARGET_ISSUE_PROJECT, bugTrackerProvider.getListOfAllProjects().toArray(), BugInfoDialogConsts.TARGET_ISSUE_PROJECT);
        String selectedIssueType = (String)bugTrackerProvider.getListOfAllIssueTypes(selectedProject).toArray()[0];
        XFormOptionsField issueTypesCombo = form.addComboBox(BugInfoDialogConsts.ISSUE_TYPE, bugTrackerProvider.getListOfAllIssueTypes(selectedProject).toArray(), BugInfoDialogConsts.ISSUE_TYPE);
        String selectedPriority = (String)bugTrackerProvider.getListOfPriorities().toArray()[0];
        form.addComboBox(BugInfoDialogConsts.ISSUE_PRIORITY, bugTrackerProvider.getListOfPriorities().toArray(), selectedPriority);
        form.addTextField(BugInfoDialogConsts.ISSUE_SUMMARY, "Issue summary", XForm.FieldType.TEXT);
        form.addTextField(BugInfoDialogConsts.ISSUE_DESCRIPTION, "Issue description", XForm.FieldType.TEXTAREA);
        addRequiredFields(form, bugTrackerProvider, selectedProject, selectedIssueType);
        form.addCheckBox(BugInfoDialogConsts.ATTACH_SOAPUI_LOG, "");
        form.addCheckBox(BugInfoDialogConsts.ATTACH_PROJECT, "");
        form.addTextField(BugInfoDialogConsts.ATTACH_ANY_FILE, "Attach file", XForm.FieldType.FILE);
        return builder.buildDialog(builder.buildOkCancelActions(), "Please specify issue options", null);
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
