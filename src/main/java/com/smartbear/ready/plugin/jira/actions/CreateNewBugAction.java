package com.smartbear.ready.plugin.jira.actions;

import com.atlassian.jira.rest.client.api.NamedEntity;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.CustomFieldOption;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.ready.LicenseCheckUtils;
import com.eviware.soapui.support.StringUtils;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.x.dialogs.Worker;
import com.eviware.x.dialogs.XProgressDialog;
import com.eviware.x.dialogs.XProgressMonitor;
import com.eviware.x.form.XForm;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormDialogBuilder;
import com.eviware.x.form.XFormFactory;
import com.eviware.x.form.XFormField;
import com.eviware.x.form.XFormFieldListener;
import com.eviware.x.form.XFormOptionsField;
import com.eviware.x.form.support.XFormMultiSelectList;
import com.google.inject.Inject;
import com.smartbear.ready.plugin.jira.dialog.BugInfoDialogConsts;
import com.smartbear.ready.plugin.jira.impl.AttachmentAddingResult;
import com.smartbear.ready.plugin.jira.impl.IssueCreationResult;
import com.smartbear.ready.plugin.jira.impl.IssueInfoDialog;
import com.smartbear.ready.plugin.jira.impl.JiraProvider;
import com.smartbear.ready.plugin.jira.impl.SwingXScrollableFormDialogBuilder;
import com.smartbear.ready.plugin.jira.impl.XFormDialogEx;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.GraphicsEnvironment;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNewBugAction extends AbstractSoapUIAction<ModelItem> {
    public static final String TOOLBAR_BUTTON_CAPTION = "JIRA";
    public static final String SPECIFIES_THE_REQUIRED_FIELDS_TO_CREATE_NEW_ISSUE_IN_JIRA = "Populate the required fields to create a new JIRA issue";
    public static final String WORKSPACE_ITEM_SELECTED = "Unable to create a  JIRA item." + StringUtils.NEWLINE + "Select a project, test case or test suite in the Navigator.";
    public static final String NO_AVAILABLE_JIRA_PROJECTS = "Unable to retrieve information from JIRA." + StringUtils.NEWLINE + "Possible causes:" + StringUtils.NEWLINE + "  - The JIRA Integration plugin settings are invalid." + StringUtils.NEWLINE + " You might have specified email instead of username." + StringUtils.NEWLINE + "  - You do not have enough permissions in JIRA.";
    public static final String NEW_ISSUE_DETAILS_FORM_NAME = "Creating a new JIRA item";
    public static final String PLEASE_WAIT = "Please wait";
    public static final String ADDING_ATTACHMENTS = "Adding attachments";
    public static final String READING_JIRA_SETTINGS_FOR_SELECTED_PROJECT_AND_ISSUE_TYPE = "Reading JIRA settings for the selected project and item type";
    public static final String READING_JIRA_SETTINGS = "Reading information from JIRA";
    public static final String TOOLBAR_ACTION_DESCRIPTION = "Create a new JIRA item";
    public static final String PATH_TO_TOOLBAR_ICON = "com/smartbear/ready/plugin/jira/icons/jira.svg";
    public static final String EMPTY_VALUE_FOR_OPTIONS_FIELD = "";
    private static String NEW_ISSUE_DIALOG_CAPTION = "Create a new ";
    private static final String MULTISELECT_CUSTOM_FIELD_TYPE = "com.atlassian.jira.plugin.system.customfieldtypes:multiselect";
    private static final String MULTICHECKBOXES_CUSTOM_FIELD_TYPE = "com.atlassian.jira.plugin.system.customfieldtypes:multicheckboxes";

    protected String selectedProject, selectedIssueType;
    private static final Logger logger = LoggerFactory.getLogger(CreateNewBugAction.class);

    public static final String DESCRIPTION_FIELD_NAME = "description";
    private static final List<String> skippedFieldKeys = Arrays.asList("summary",
            "project",
            "issuetype",
            DESCRIPTION_FIELD_NAME,
            JiraProvider.VERSIONS_FIELD_NAME,
            "attachment",
            JiraProvider.PRIORITY_FIELD_NAME,
            JiraProvider.FIX_VERSIONS_FIELD_NAME);
    private static final List<String> multilineTextEditors = Arrays.asList("com.atlassian.jira.plugin.system.customfieldtypes:textarea");

    @Inject
    public CreateNewBugAction() {
        super(TOOLBAR_BUTTON_CAPTION, SPECIFIES_THE_REQUIRED_FIELDS_TO_CREATE_NEW_ISSUE_IN_JIRA);
    }

    @Override
    public void perform(ModelItem target, Object o) {
        if (!LicenseCheckUtils.userHasAccessToSoapUING()) {
            UISupport.showErrorMessage("To use this feature, you need a ReadyAPI Test license.\nYou can request a trial at smartbear.com.");
            return;
        }

        JiraProvider bugTrackerProvider = JiraProvider.getProvider();
        if (!bugTrackerProvider.settingsComplete()) {
            UISupport.showErrorMessage(JiraProvider.BUG_TRACKER_SETTINGS_ARE_NOT_COMPLETELY_SPECIFIED);
            return;
        }
        if (target instanceof Workspace) {
            UISupport.showErrorMessage(WORKSPACE_ITEM_SELECTED);
            return;
        }
        bugTrackerProvider.setActiveItem(target);
        List<String> projects = bugTrackerProvider.getListOfAllProjects();
        if (projects == null || projects.size() == 0) {
            UISupport.showErrorMessage(NO_AVAILABLE_JIRA_PROJECTS);
            return;
        }
        XFormDialog dialogOne = createInitialSetupDialog(bugTrackerProvider);
        if (dialogOne.show()) {
            XFormDialog dialogTwo = createIssueDetailsDialog(bugTrackerProvider, selectedProject, selectedIssueType);
            XFormDialogEx dialogTwoEx = (XFormDialogEx) dialogTwo;
            if (dialogTwoEx != null) {
                int screenHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
                dialogTwoEx.setHeight(7 * screenHeight / 10);
            }
            if (dialogTwo != null && dialogTwo.show()) {
                handleOkAction(bugTrackerProvider, dialogTwo);
            }
        } else {
            selectedProject = null;
            selectedIssueType = null;
        }
    }

    private class JiraIssueCreatorWorker implements Worker {
        final JiraProvider bugTrackerProvider;
        final String projectKey;
        final String issueType;
        final String summary;
        final String description;
        final Map<String, Object> extraValues;
        IssueCreationResult result;

        public JiraIssueCreatorWorker(JiraProvider bugTrackerProvider, String projectKey, String issueType,
                                      String summary, String description, Map<String, Object> extraValues) {
            this.bugTrackerProvider = bugTrackerProvider;
            this.projectKey = projectKey;
            this.issueType = issueType;
            this.summary = summary;
            this.description = description;
            this.extraValues = extraValues;
        }

        @Override
        public Object construct(XProgressMonitor xProgressMonitor) {
            result = bugTrackerProvider.createIssue(projectKey, issueType, summary, description, extraValues);
            return result;
        }

        @Override
        public void finished() {
        }

        @Override
        public boolean onCancel() {
            return false;
        }

        public IssueCreationResult getResult() {
            return result;
        }
    }

    private class JiraIssueAttachmentWorker implements Worker {
        final JiraProvider bugTrackerProvider;
        final IssueCreationResult creationResult;
        final XFormDialog issueDetails;
        StringBuilder resultError;
        boolean isAttachmentSuccess;

        public JiraIssueAttachmentWorker(JiraProvider bugTrackerProvider, IssueCreationResult creationResult,
                                         XFormDialog issueDetails) {
            this.bugTrackerProvider = bugTrackerProvider;
            this.creationResult = creationResult;
            this.issueDetails = issueDetails;
        }

        @Override
        public Object construct(XProgressMonitor xProgressMonitor) {
            isAttachmentSuccess = true;
            URI newIssueAttachURI = null;
            try {
                newIssueAttachURI = new URI(creationResult.getIssue().getSelf().toString().concat("/attachments"));
            } catch (URISyntaxException e) {
                logger.error("[CreateNewBugAction][construct] Error while createIssue , newIssueAttachURI: {}", e.getMessage());
            }
            resultError = new StringBuilder();
            if (issueDetails.getBooleanValue(BugInfoDialogConsts.ATTACH_READYAPI_LOG)) {
                AttachmentAddingResult attachResult = bugTrackerProvider.attachFile(newIssueAttachURI,
                        bugTrackerProvider.getActiveItemName() + ".log", bugTrackerProvider.getReadyApiLog());
                if (!attachResult.getSuccess()) {
                    isAttachmentSuccess = false;
                    resultError.append(attachResult.getError());
                    resultError.append("\r\n");
                }
            }

            if (issueDetails.getBooleanValue(BugInfoDialogConsts.ATTACH_PROJECT)) {
                AttachmentAddingResult attachResult = bugTrackerProvider.attachFile(newIssueAttachURI,
                        bugTrackerProvider.getRootProjectName() + ".xml", bugTrackerProvider.getRootProject());
                if (!attachResult.getSuccess()) {
                    isAttachmentSuccess = false;
                    resultError.append(attachResult.getError());
                    resultError.append("\r\n");
                }
            }

            String attachAnyFileValue = issueDetails.getValue(BugInfoDialogConsts.ATTACH_ANY_FILE);
            if (!StringUtils.isNullOrEmpty(issueDetails.getValue(BugInfoDialogConsts.ATTACH_ANY_FILE))) {
                AttachmentAddingResult attachResult = bugTrackerProvider.attachFile(newIssueAttachURI, attachAnyFileValue);
                if (!attachResult.getSuccess()) {
                    isAttachmentSuccess = false;
                    resultError.append(attachResult.getError());
                }
            }

            return resultError;
        }

        @Override
        public void finished() {

        }

        @Override
        public boolean onCancel() {
            return false;
        }

        public boolean getAttachmentSuccess() {
            return isAttachmentSuccess;
        }

        public StringBuilder getResultError() {
            return resultError;
        }
    }

    private void handleOkAction(JiraProvider bugTrackerProvider, XFormDialog issueDetails) {
        StringToStringMap values = issueDetails.getValues();
        String summary = values.get(BugInfoDialogConsts.ISSUE_SUMMARY, null);
        String description = values.get(BugInfoDialogConsts.ISSUE_DESCRIPTION, null);
        String projectKey = selectedProject;
        String issueType = selectedIssueType;
        Map<String, Object> extraValues = new HashMap<>();
        for (Map.Entry<String, CimFieldInfo> entry : bugTrackerProvider.getProjectFields(projectKey)
                .getOrDefault(projectKey, new HashMap<>()).getOrDefault(issueType, new HashMap<>()).entrySet()) {
            String key = entry.getKey();
            if (skippedFieldKeys.contains(key) &&
                    !key.equals(JiraProvider.VERSIONS_FIELD_NAME) &&
                    !key.equals(JiraProvider.FIX_VERSIONS_FIELD_NAME) &&
                    !key.equals(JiraProvider.PRIORITY_FIELD_NAME)) {
                continue;
            }

            if (isSpecifiedTypeCustomField(entry.getValue(), MULTISELECT_CUSTOM_FIELD_TYPE) ||
                    isSpecifiedTypeCustomField(entry.getValue(), MULTICHECKBOXES_CUSTOM_FIELD_TYPE)) {
                XFormField formField = issueDetails.getFormField(entry.getValue().getName());
                String[] selectedOptions = StringUtils.toStringArray(((XFormMultiSelectList) formField).getSelectedOptions());
                extraValues.put(key, selectedOptions);
                continue;
            }

            if (!StringUtils.isNullOrEmpty(values.get(entry.getValue().getName()))) {
                extraValues.put(key, values.get(entry.getValue().getName()));
            }
        }
        XProgressDialog issueCreationProgressDialog = UISupport.getDialogs().createProgressDialog(
                NEW_ISSUE_DETAILS_FORM_NAME, 100, PLEASE_WAIT, false);
        JiraIssueCreatorWorker worker = new JiraIssueCreatorWorker(bugTrackerProvider, projectKey,
                issueType, summary, description, extraValues);
        try {
            issueCreationProgressDialog.run(worker);
        } catch (Exception e) {
        }
        IssueCreationResult result = worker.getResult();
        if (result.getSuccess()) {
            JiraIssueAttachmentWorker attachmentWorker =
                    new JiraIssueAttachmentWorker(bugTrackerProvider, result, issueDetails);
            XProgressDialog addingAttachmentProgressDialog =
                    UISupport.getDialogs().createProgressDialog(ADDING_ATTACHMENTS, 100, PLEASE_WAIT, false);
            try {
                addingAttachmentProgressDialog.run(attachmentWorker);
            } catch (Exception e) {
            }

            if (!attachmentWorker.getAttachmentSuccess()) {
                UISupport.showErrorMessage(attachmentWorker.getResultError().toString());
                if (issueDetails.show()) {
                    handleOkAction(bugTrackerProvider, issueDetails);
                }
            } else {
                IssueInfoDialog.showDialog(issueType,
                        bugTrackerProvider.getBugTrackerSettings().getUrl().concat("/browse/").concat(result.getIssue().getKey()),
                        result.getIssue().getKey());//TODO: make link correct for all cases
            }

        } else {
            UISupport.showErrorMessage(result.getError());
            if (issueDetails.show()) {
                handleOkAction(bugTrackerProvider, issueDetails);
            }
        }
    }

    public static String[] IterableObjectsToNameArray(JiraProvider bugTrackerProvider, Iterable<Object> input, boolean addEmptyValue) {
        ArrayList<String> objects = new ArrayList<>();
        if (addEmptyValue) {
            objects.add(EMPTY_VALUE_FOR_OPTIONS_FIELD);
        }
        for (Object obj : input) {
            CustomFieldOption customFieldOption = bugTrackerProvider.transformToCustomFieldOption(obj);
            if (customFieldOption != null) {
                objects.add(customFieldOption.getValue());
            } else {
                if (obj instanceof NamedEntity) {
                    NamedEntity namedEntity = (NamedEntity) obj;
                    objects.add(namedEntity.getName());
                }
            }
        }
        return objects.toArray(new String[objects.size()]);
    }

    public static Object[] FixVersionsToNameArray(JiraProvider bugTrackerProvider, Iterable<Object> input,
                                                  boolean skipReleasedVersions, boolean needEmptyValue) {
        ArrayList<Object> objects = new ArrayList<>();
        if (needEmptyValue) {
            objects.add(EMPTY_VALUE_FOR_OPTIONS_FIELD);
        }
        for (Object obj : input) {
            Version versionValue = bugTrackerProvider.transformToVersion(obj);
            if (versionValue != null) {
                if (!skipReleasedVersions || (skipReleasedVersions && !versionValue.isReleased())) {
                    objects.add(versionValue.getName());
                }
            }
        }
        return objects.toArray();
    }

    private CimFieldInfo getFieldInfo(JiraProvider bugTrackerProvider, String selectedProject, String selectedIssueType, String fieldInfoKey) {
        logger.info("getFieldInfo.bugTrackerProvider : {}, selectedProject : {}, fieldInfoKey: {}", bugTrackerProvider.toString(), selectedProject, fieldInfoKey);
        Map<String, Map<String, Map<String, CimFieldInfo>>> allFields = bugTrackerProvider.getProjectFields(selectedProject);
        for (Map.Entry<String, CimFieldInfo> field : allFields.getOrDefault(selectedProject, new HashMap<>())
                .getOrDefault(selectedIssueType, new HashMap<>()).entrySet()) {
            String key = field.getKey();
            if (key != null && key.equals(fieldInfoKey)) {
                return field.getValue();
            }
        }
        return null;
    }

    private void addExtraFields(XForm baseDialog, JiraProvider bugTrackerProvider, String selectedProject, String selectedIssueType) {
        Map<String, Map<String, Map<String, CimFieldInfo>>> allFields = bugTrackerProvider.getProjectFields(selectedProject);
        for (Map.Entry<String, CimFieldInfo> field : allFields.getOrDefault(selectedProject,
                new HashMap<>()).getOrDefault(selectedIssueType, new HashMap<>()).entrySet()) {
            String key = field.getKey();
            if (skippedFieldKeys.contains(key)) {
                continue;
            }
            CimFieldInfo fieldInfo = field.getValue();
            XFormField newField;
            if (fieldInfo.getAllowedValues() != null) {
                boolean isMultiValueCustomField = isSpecifiedTypeCustomField(fieldInfo, MULTICHECKBOXES_CUSTOM_FIELD_TYPE) ||
                        isSpecifiedTypeCustomField(fieldInfo, MULTISELECT_CUSTOM_FIELD_TYPE);
                boolean addEmptyValue = isMultiValueCustomField ? false : !fieldInfo.isRequired();
                String[] values = IterableObjectsToNameArray(bugTrackerProvider, fieldInfo.getAllowedValues(), addEmptyValue);
                if (values.length > 0) {
                    if (isMultiValueCustomField) {
                        newField = baseDialog.addComponent(fieldInfo.getName(), new XFormMultiSelectList(values));
                    } else {
                        newField = baseDialog.addComboBox(fieldInfo.getName(), values, fieldInfo.getName());
                        makeComboBoxFieldEditable(newField);
                    }
                } else {
                    newField = baseDialog.addTextField(fieldInfo.getName(), fieldInfo.getName(), XForm.FieldType.TEXT);
                }
            } else {
                boolean isMultilineTextEditor = multilineTextEditors.contains(fieldInfo.getSchema().getCustom());
                newField = baseDialog.addTextField(fieldInfo.getName(), fieldInfo.getName(),
                        isMultilineTextEditor ? XForm.FieldType.TEXTAREA : XForm.FieldType.TEXT);
            }
            if (fieldInfo.isRequired()) {
                newField.setRequired(true, fieldInfo.getName());
            }
        }
    }

    private boolean isSpecifiedTypeCustomField(CimFieldInfo fieldInfo, String type) {
        String custom = fieldInfo.getSchema().getCustom();
        if (custom == null) {
            return false;
        }

        return custom.equals(type);
    }

    private void makeComboBoxFieldEditable(XFormField field) {
        if (field instanceof com.eviware.x.impl.swing.JComboBoxFormField) {
            com.eviware.x.impl.swing.JComboBoxFormField comboBox = (com.eviware.x.impl.swing.JComboBoxFormField) field;
            comboBox.getComponent().setEditable(true);
            AutoCompleteDecorator.decorate(comboBox.getComponent());
        }
    }

    private class RequiredFieldsWorker implements Worker {
        public static final String ISSUE_SUMMARY = "Summary";
        public static final String ISSUE_DESCRIPTION = "Description";
        public static final String ATTACH_FILE = "Attach a file";
        public static final String PLEASE_SPECIFY_ISSUE_OPTIONS = "Specify item's field values. Required fields are marked with red.";
        final JiraProvider bugTrackerProvider;
        final String selectedProject;
        final String selectedIssueType;
        XFormDialog dialog;

        public RequiredFieldsWorker(JiraProvider bugTrackerProvider, String selectedProject, String selectedIssueType) {
            this.selectedProject = selectedProject;
            this.selectedIssueType = selectedIssueType;
            this.bugTrackerProvider = bugTrackerProvider;
        }

        @Override
        public Object construct(XProgressMonitor xProgressMonitor) {
            SwingXScrollableFormDialogBuilder builder = new SwingXScrollableFormDialogBuilder(NEW_ISSUE_DIALOG_CAPTION +
                    selectedIssueType + " item in  [" + selectedProject + "] Project");
            XForm form = builder.createForm("Basic");
            XFormField summaryField = form.addTextField(BugInfoDialogConsts.ISSUE_SUMMARY, ISSUE_SUMMARY,
                    XForm.FieldType.TEXT);
            summaryField.setRequired(true, ISSUE_SUMMARY);
            CimFieldInfo descriptionFieldInfo = getFieldInfo(bugTrackerProvider, selectedProject, selectedIssueType,
                    DESCRIPTION_FIELD_NAME);
            if (descriptionFieldInfo != null) {
                XFormField descriptionField = form.addTextField(BugInfoDialogConsts.ISSUE_DESCRIPTION, ISSUE_DESCRIPTION,
                        XForm.FieldType.TEXTAREA);
                descriptionField.setRequired(getFieldInfo(bugTrackerProvider, selectedProject,
                        selectedIssueType, DESCRIPTION_FIELD_NAME).isRequired(), ISSUE_DESCRIPTION);
            }
            CimFieldInfo priorityFieldInfo = getFieldInfo(bugTrackerProvider, selectedProject, selectedIssueType,
                    JiraProvider.PRIORITY_FIELD_NAME);
            if (priorityFieldInfo != null) {
                XFormField priorityField = form.addComboBox(priorityFieldInfo.getName(),
                        IterableObjectsToNameArray(bugTrackerProvider, priorityFieldInfo.getAllowedValues(), false),
                        priorityFieldInfo.getName());
                makeComboBoxFieldEditable(priorityField);
                priorityField.setRequired(priorityFieldInfo.isRequired(), priorityFieldInfo.getName());
            }
            //adding Affect versions field
            CimFieldInfo affectedVersionFieldInfo = getFieldInfo(bugTrackerProvider, selectedProject, selectedIssueType,
                    JiraProvider.VERSIONS_FIELD_NAME);
            if (affectedVersionFieldInfo != null) {
                XFormField affectedVersionField = null;
                if (affectedVersionFieldInfo.getAllowedValues() != null) {
                    String[] values = IterableObjectsToNameArray(bugTrackerProvider,
                            affectedVersionFieldInfo.getAllowedValues(), !affectedVersionFieldInfo.isRequired());
                    if (values.length > 0) {
                        affectedVersionField = form.addComboBox(affectedVersionFieldInfo.getName(), values,
                                affectedVersionFieldInfo.getName());
                        makeComboBoxFieldEditable(affectedVersionField);
                    }
                }
                if (affectedVersionFieldInfo.isRequired() && affectedVersionField != null) {
                    affectedVersionField.setRequired(true, affectedVersionFieldInfo.getName());
                }
            }
            //end of adding Affect versions field

            addExtraFields(form, bugTrackerProvider, selectedProject, selectedIssueType);

            //adding Fix Version field (filtered)
            CimFieldInfo fixedVersionFieldInfo = getFieldInfo(bugTrackerProvider, selectedProject, selectedIssueType,
                    "fixVersions");
            if (fixedVersionFieldInfo != null) {
                XFormField fixVersionField = null;
                if (fixedVersionFieldInfo.getAllowedValues() != null) {
                    Object[] values = FixVersionsToNameArray(bugTrackerProvider,
                            fixedVersionFieldInfo.getAllowedValues(),
                            bugTrackerProvider.getBugTrackerSettings().getSkipReleasedVersions(),
                            !fixedVersionFieldInfo.isRequired());
                    if (values.length > 0) {
                        fixVersionField = form.addComboBox(fixedVersionFieldInfo.getName(), values,
                                fixedVersionFieldInfo.getName());
                        makeComboBoxFieldEditable(fixVersionField);
                    }
                }
                if (fixedVersionFieldInfo.isRequired() && fixVersionField != null) {
                    fixVersionField.setRequired(true, fixedVersionFieldInfo.getName());
                }
            }
            //end of adding Fix Version field (filtered)

            form.addCheckBox(BugInfoDialogConsts.ATTACH_READYAPI_LOG, BugInfoDialogConsts.ATTACH_READYAPI_LOG);
            form.addCheckBox(BugInfoDialogConsts.ATTACH_PROJECT, BugInfoDialogConsts.ATTACH_PROJECT);
            form.addTextField(BugInfoDialogConsts.ATTACH_ANY_FILE, ATTACH_FILE, XForm.FieldType.FILE);
            dialog = builder.buildDialog(builder.buildOkCancelActions(), PLEASE_SPECIFY_ISSUE_OPTIONS, null);
            return dialog;
        }

        @Override
        public void finished() {

        }

        @Override
        public boolean onCancel() {
            return false;
        }

        public XFormDialog getDialog() {
            return dialog;
        }
    }

    private XFormDialog createIssueDetailsDialog(final JiraProvider bugTrackerProvider, final String selectedProject,
                                                 final String selectedIssueType) {
        RequiredFieldsWorker worker = new RequiredFieldsWorker(bugTrackerProvider, selectedProject, selectedIssueType);
        XProgressDialog readingProjectSettingsProgressDialog = UISupport.getDialogs().createProgressDialog(
                READING_JIRA_SETTINGS_FOR_SELECTED_PROJECT_AND_ISSUE_TYPE, 100, PLEASE_WAIT, false);
        try {
            readingProjectSettingsProgressDialog.run(worker);
        } catch (Exception e) {
        }
        return worker.getDialog();
    }

    private class InitialDialogWorker implements Worker {
        public static final String CHOOSE_REQUIRED_PROJECT_AND_ISSUE_TYPE = "Select a project and an item type.";
        final JiraProvider bugTrackerProvider;
        XFormDialog dialog;

        public InitialDialogWorker(JiraProvider bugTrackerProvider) {
            this.bugTrackerProvider = bugTrackerProvider;
        }

        @Override
        public Object construct(XProgressMonitor xProgressMonitor) {
            XFormDialogBuilder builder = XFormFactory.createDialogBuilder(NEW_ISSUE_DIALOG_CAPTION + " item");
            XForm form = builder.createForm("Basic");
            List<String> allProjectsList = bugTrackerProvider.getListOfAllProjects();
            XFormOptionsField projectsCombo = form.addComboBox(BugInfoDialogConsts.TARGET_ISSUE_PROJECT,
                    allProjectsList.toArray(), BugInfoDialogConsts.TARGET_ISSUE_PROJECT);
            if (StringUtils.isNullOrEmpty(selectedProject)) {
                selectedProject = (String) (allProjectsList.toArray()[0]);
            }
            projectsCombo.setValue(selectedProject);
            Object[] currentProjectIssueTypes = bugTrackerProvider.getListOfProjectIssueTypes(selectedProject).toArray();
            final XFormOptionsField issueTypesCombo = form.addComboBox(BugInfoDialogConsts.ISSUE_TYPE,
                    currentProjectIssueTypes, BugInfoDialogConsts.ISSUE_TYPE);
            projectsCombo.addFormFieldListener(new XFormFieldListener() {
                @Override
                public void valueChanged(XFormField xFormField, String newValue, String oldValue) {
                    selectedProject = newValue;
                    issueTypesCombo.setOptions(bugTrackerProvider.getListOfProjectIssueTypes(selectedProject).toArray());
                }
            });
            if (StringUtils.isNullOrEmpty(selectedIssueType)) {
                selectedIssueType = (String) currentProjectIssueTypes[0];
            }
            issueTypesCombo.setOptions(currentProjectIssueTypes);
            issueTypesCombo.setValue(selectedIssueType);
            issueTypesCombo.addFormFieldListener(new XFormFieldListener() {
                @Override
                public void valueChanged(XFormField xFormField, String newValue, String oldValue) {
                    selectedIssueType = newValue;
                }
            });
            dialog = builder.buildDialog(builder.buildOkCancelActions(), CHOOSE_REQUIRED_PROJECT_AND_ISSUE_TYPE, null);
            return dialog;
        }

        @Override
        public void finished() {

        }

        @Override
        public boolean onCancel() {
            return false;
        }

        public XFormDialog getDialog() {
            return dialog;
        }
    }

    private XFormDialog createInitialSetupDialog(final JiraProvider bugTrackerProvider) {
        InitialDialogWorker worker = new InitialDialogWorker(bugTrackerProvider);
        XProgressDialog readInitialInfoProgressDialog = UISupport.getDialogs().createProgressDialog(
                READING_JIRA_SETTINGS, 100, PLEASE_WAIT, false);
        try {
            readInitialInfoProgressDialog.run(worker);
        } catch (Exception e) {
        }

        return worker.getDialog();
    }

    @Override
    public boolean shouldBeEnabledFor(ModelItem modelItem) {
        if (modelItem instanceof WsdlProject || modelItem instanceof TestCase ||
                modelItem instanceof TestSuite || modelItem instanceof TestStep) {
            return true;
        }

        return false;
    }
}
