package com.smartbear.ready.plugin.jira.impl;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.support.ModelSupport;
import com.eviware.soapui.support.StringUtils;
import com.eviware.soapui.support.UISupport;
import com.eviware.x.form.XFormField;
import com.smartbear.ready.plugin.jira.settings.BugTrackerPrefs;
import com.smartbear.ready.plugin.jira.settings.BugTrackerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class JiraProvider implements SimpleBugTrackerProvider {
    private static final Logger logger = LoggerFactory.getLogger(JiraProvider.class);

    private final static String BUG_TRACKER_ISSUE_KEY_NOT_SPECIFIED = "Issue key not specified";
    private final static String BUG_TRACKER_FILE_NAME_NOT_SPECIFIED = "File name not specified";
    private final static String BUG_TRACKER_INCORRECT_FILE_PATH = "Incorrect file path";

    private ModelItem activeElement;
    private JiraRestClient restClient = null;
    private BugTrackerSettings bugTrackerSettings;
    static private JiraProvider instance = null;

    //Properties below exist for reducing number of Jira API calls since it's very greedy operation
    Iterable<BasicProject> allProjects = null;
    Map<String, Project> requestedProjects = new HashMap<>();
    Iterable<Priority> priorities = null;
    Map<String/*project*/,Map<String/*Issue Type*/, Map<String/*FieldName*/, CimFieldInfo>>> projectFields = new HashMap<>();
    Map<String/*project*/,Map<String/*Issue Type*/, Map<String/*FieldName*/, CimFieldInfo>>> allRequiredFields = new HashMap<>();

    public static JiraProvider getProvider (){
        if (instance == null){
            instance = new JiraProvider();
        }
        return instance;
    }

    public static void freeProvider(){
        instance = null;
    }

    private JiraProvider() {
        bugTrackerSettings = getBugTrackerSettings();
        if (!settingsComplete(bugTrackerSettings)) {
            logger.error("Bug tracker settings are not completely specified.");
            UISupport.showErrorMessage("Bug tracker settings are not completely specified.");
            return;
        }
        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        try {
            restClient = factory.createWithBasicHttpAuthentication(new URI(bugTrackerSettings.getUrl()), bugTrackerSettings.getLogin(), bugTrackerSettings.getPassword());
        } catch (URISyntaxException e) {
            logger.error("Incorrectly specified bug tracker URI.");
            UISupport.showErrorMessage("Incorrectly specified bug tracker URI.");
        }
    }

    public String getName() {
        return "Jira Bug Tracker provider";
    }

    private JiraApiCallResult<Iterable<BasicProject>> getAllProjects() {
        if (allProjects != null){
            return new JiraApiCallResult<Iterable<BasicProject>>(allProjects);
        }

        try {
            allProjects = restClient.getProjectClient().getAllProjects().get();
            return new JiraApiCallResult<Iterable<BasicProject>>(allProjects);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            allProjects = null;
            return new JiraApiCallResult<Iterable<BasicProject>>(e);
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
            allProjects = null;
            return new JiraApiCallResult<Iterable<BasicProject>>(e);
        }
    }

    public List<String> getListOfAllProjects() {
        JiraApiCallResult<Iterable<BasicProject>> projects = getAllProjects();
        if (!projects.isSuccess()) {
            return new ArrayList<String>();
        }

        List<String> projectNames = new ArrayList<String>();
        for (BasicProject project : projects.getResult()) {
            projectNames.add(project.getKey());
        }

        return projectNames;
    }

    private JiraApiCallResult<Project> getProjectByKey(String key) {
        if (!requestedProjects.containsKey(key)) {
            try {
                requestedProjects.put(key, restClient.getProjectClient().getProject(key).get());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                return new JiraApiCallResult<Project>(e);
            } catch (ExecutionException e) {
                logger.error(e.getMessage());
                return new JiraApiCallResult<Project>(e);
            }
        }
        return new JiraApiCallResult<Project>(requestedProjects.get(key));
    }

    private JiraApiCallResult<OptionalIterable<IssueType>> getProjectIssueTypes(String projectKey) {
        JiraApiCallResult<Project> project = getProjectByKey(projectKey);
        if (!project.isSuccess()) {
            return new JiraApiCallResult<OptionalIterable<IssueType>>(project.getError());
        }
        return new JiraApiCallResult<OptionalIterable<IssueType>>(project.getResult().getIssueTypes());
    }

    public List<String> getListOfAllIssueTypes(String projectKey) {
        JiraApiCallResult<OptionalIterable<IssueType>> result = getProjectIssueTypes(projectKey);
        if (!result.isSuccess()) {
            return new ArrayList<String>();
        }

        List<String> issueTypeList = new ArrayList<String>();
        OptionalIterable<IssueType> issueTypes = result.getResult();
        for (IssueType issueType : issueTypes) {
            issueTypeList.add(issueType.getName());
        }

        return issueTypeList;
    }

    private JiraApiCallResult<Iterable<Priority>> getAllPriorities() {
        if (priorities == null) {
            final MetadataRestClient client = restClient.getMetadataClient();
            try {
                priorities = client.getPriorities().get();
            } catch (InterruptedException e) {
                return new JiraApiCallResult<Iterable<Priority>>(e);
            } catch (ExecutionException e) {
                return new JiraApiCallResult<Iterable<Priority>>(e);
            }
        }
        return new JiraApiCallResult<Iterable<Priority>>(priorities);
    }

    private Priority getPriorityByName(String priorityName) {
        JiraApiCallResult<Iterable<Priority>> priorities = getAllPriorities();
        if (!priorities.isSuccess()) {
            return null;
        }
        for (Priority priority : priorities.getResult()) {
            if (priority.getName().equals(priorityName)) {
                return priority;
            }
        }
        return null;
    }

    public List<String> getListOfPriorities() {
        JiraApiCallResult<Iterable<Priority>> priorities = getAllPriorities();
        if (!priorities.isSuccess()) {
            return new ArrayList<String>();
        }

        List<String> prioritiesAll = new ArrayList<String>();
        for (Priority currentPriority : priorities.getResult()) {
            prioritiesAll.add(currentPriority.getName());
        }

        return prioritiesAll;
    }

    private JiraApiCallResult<IssueType> getIssueType(String projectKey, String requiredIssueType) {
        JiraApiCallResult<OptionalIterable<IssueType>> issueTypes = getProjectIssueTypes(projectKey);
        if (!issueTypes.isSuccess()) {
            return new JiraApiCallResult<IssueType>(issueTypes.getError());
        }
        for (IssueType issueType : issueTypes.getResult()) {
            if (issueType.getName().equals(requiredIssueType)) {
                return new JiraApiCallResult<IssueType>(issueType);
            }
        }
        return null;
    }

    public Issue getIssue(String key) {
        try {
            return restClient.getIssueClient().getIssue(key).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private JiraApiCallResult<Map<String/*project*/,Map<String/*Issue Type*/, Map<String/*Field Name*/, CimFieldInfo>>>> getProjectFields (String ... projects){
        List<String> uncachedProjectsList = new ArrayList<>();
        for (String project:projects){
            if (!projectFields.containsKey(project)){
                uncachedProjectsList.add(project);
            }
        }
        if (uncachedProjectsList.size() > 0) {
            String [] uncachedProjectsArray = new String[uncachedProjectsList.size()];
            uncachedProjectsList.toArray(uncachedProjectsArray);
            GetCreateIssueMetadataOptions options = new GetCreateIssueMetadataOptionsBuilder()
                    .withExpandedIssueTypesFields()
                    .withProjectKeys(uncachedProjectsList.toArray(uncachedProjectsArray))
                    .build();
            try {
                Iterable<CimProject> cimProjects = restClient.getIssueClient().getCreateIssueMetadata(options).get();
                for (CimProject cimProject : cimProjects) {
                    Iterable<CimIssueType> issueTypes = cimProject.getIssueTypes();
                    HashMap<String, Map<String, CimFieldInfo>> issueTypeFields = new HashMap<String, Map<String, CimFieldInfo>>();
                    for (CimIssueType currentIssueType : issueTypes) {
                        issueTypeFields.put(currentIssueType.getName(), currentIssueType.getFields());
                    }
                    projectFields.put(cimProject.getKey(),issueTypeFields);
                }
            } catch (InterruptedException e) {
                return new JiraApiCallResult<Map<String,Map<String, Map<String, CimFieldInfo>>>>(e);
            } catch (ExecutionException e) {
                return new JiraApiCallResult<Map<String,Map<String, Map<String, CimFieldInfo>>>>(e);
            }
        }
        return new JiraApiCallResult<Map<String,Map<String, Map<String, CimFieldInfo>>>>(projectFields);
    }

    public Map<String,Map<String, Map<String, CimFieldInfo>>> getProjectRequiredFields(){
        List<String> allProjectsList = getListOfAllProjects();
        List<String> uncachedProjectsList = new ArrayList<>();
        for (String project:allProjectsList){
            if (!allRequiredFields.containsKey(project)){
                uncachedProjectsList.add(project);
            }
        }
        String [] uncachedProjects = new String [uncachedProjectsList.size()];
        allProjectsList.toArray(uncachedProjects);

        JiraApiCallResult<Map<String,Map<String, Map<String, CimFieldInfo>>>> allProjectFieldsResult = getProjectFields(uncachedProjects);
        if (!allProjectFieldsResult.isSuccess()){
            return null;
        }

        for (Map.Entry<String,Map<String, Map<String, CimFieldInfo>>> project:allProjectFieldsResult.getResult().entrySet()){
            Map<String, Map<String, CimFieldInfo>> issueTypeFields = project.getValue();
            Map<String, Map<String/*FieldName*/, CimFieldInfo>> issueTypeRequiredFields = new HashMap<String, Map<String, CimFieldInfo>>();
            for (Map.Entry<String, Map<String, CimFieldInfo>> issueType:issueTypeFields.entrySet()){
                Map<String, CimFieldInfo> fields = issueType.getValue();
                Map<String, CimFieldInfo> requiredFields = new HashMap<>();
                for (Map.Entry<String, CimFieldInfo> field:fields.entrySet()){
                    if(field.getValue().isRequired()){
                        requiredFields.put(field.getKey(), field.getValue());
                    }
                }
                issueTypeRequiredFields.put(issueType.getKey(), requiredFields);
            }
            allRequiredFields.put(project.getKey(), issueTypeRequiredFields);
        }

        return allRequiredFields;
    }

    @Override
    public IssueCreationResult createIssue(String projectKey, String issueKey, String priority, String summary, String description, Map<String, String> extraRequiredValues) {
        //https://bitbucket.org/atlassian/jira-rest-java-client/src/75a64c9d81aad7d8bd9beb11e098148407b13cae/test/src/test/java/samples/Example1.java?at=master
        //http://www.restapitutorial.com/httpstatuscodes.html
        if (restClient == null) {
            return new IssueCreationResult("Incorrectly specified bug tracker URI.");//TODO: correct message
        }

        BasicIssue basicIssue = null;
        try {
            JiraApiCallResult<IssueType> issueType = getIssueType(projectKey, issueKey);
            if (!issueType.isSuccess()) {
                return new IssueCreationResult(issueType.getError().getMessage());
            }

            IssueInputBuilder issueInputBuilder = new IssueInputBuilder(projectKey, issueType.getResult().getId());
            issueInputBuilder.setIssueType(issueType.getResult());
            issueInputBuilder.setProjectKey(projectKey);
            issueInputBuilder.setSummary(summary);
            issueInputBuilder.setDescription(description);
            issueInputBuilder.setPriority(getPriorityByName(priority));
            for (final Map.Entry<String, String> extraRequiredValue : extraRequiredValues.entrySet()) {
                if (extraRequiredValue.getKey().equals("components")) {
                    issueInputBuilder.setComponentsNames(new Iterable<String>() {
                        @Override
                        public Iterator<String> iterator() {
                            return new Iterator<String>() {
                                boolean hasValue = true;

                                @Override
                                public boolean hasNext() {
                                    return hasValue;
                                }

                                @Override
                                public String next() {
                                    hasValue = false;
                                    return extraRequiredValue.getValue();
                                }

                                @Override
                                public void remove() {

                                }
                            };
                        }
                    });
                } else {
                    issueInputBuilder.setFieldValue(extraRequiredValue.getKey(), extraRequiredValue.getValue());
                }
            }
            Promise<BasicIssue> issue = restClient.getIssueClient().createIssue(issueInputBuilder.build());
            basicIssue = issue.get();
        } catch (InterruptedException e) {
            return new IssueCreationResult(e.getMessage());
        } catch (ExecutionException e) {
            return new IssueCreationResult(e.getMessage());
        }

        return new IssueCreationResult(basicIssue);
    }

    protected void finalize() throws Throwable {
        try {
            if (restClient != null) {
                restClient.close();
            }
        } catch (IOException e) {
        }
    }

    @Override
    public AttachmentAddingResult attachFile(URI attachmentUri, String fileName, InputStream inputStream) {
        if (attachmentUri == null) {
            return new AttachmentAddingResult(BUG_TRACKER_ISSUE_KEY_NOT_SPECIFIED);
        }
        if (StringUtils.isNullOrEmpty(fileName)) {
            return new AttachmentAddingResult(BUG_TRACKER_FILE_NAME_NOT_SPECIFIED);
        }

        try {
            restClient.getIssueClient().addAttachment(attachmentUri, inputStream, fileName).get();
        } catch (InterruptedException e) {
            return new AttachmentAddingResult(e.getMessage());
        } catch (ExecutionException e) {
            return new AttachmentAddingResult(e.getMessage());
        }

        return new AttachmentAddingResult();//everything is ok
    }

    @Override
    public AttachmentAddingResult attachFile(URI attachmentUri, String filePath){
        if (attachmentUri == null) {
            return new AttachmentAddingResult(BUG_TRACKER_ISSUE_KEY_NOT_SPECIFIED);
        }
        if (StringUtils.isNullOrEmpty(filePath)) {
            return new AttachmentAddingResult(BUG_TRACKER_INCORRECT_FILE_PATH);
        }
        File file = new File (filePath);
        if (!file.exists() && file.isFile()){
            return new AttachmentAddingResult(BUG_TRACKER_INCORRECT_FILE_PATH);
        }

        restClient.getIssueClient().addAttachments(attachmentUri, file);
        return new AttachmentAddingResult();
    }

    public InputStream getSoapUIExecutionLog() {
        return getExecutionLog("com.eviware.soapui");
    }

    public InputStream getServiceVExecutionLog() {
        return getExecutionLog("com.smartbear.servicev");
    }

    public InputStream getLoadUIExecutionLog() {
        return getExecutionLog("com.eviware.loadui");
    }

    public InputStream getReadyApiLog() {
        return getExecutionLog("com.smartbear.ready");
    }

    public void setActiveItem(ModelItem element) {
        activeElement = element;
    }

    public String getActiveItemName() {
        return activeElement.getName();
    }

    public InputStream getRootProject() {
        WsdlProject project = findActiveElementRootProject(activeElement);
        return new ByteArrayInputStream(project.getConfig().toString().getBytes(StandardCharsets.UTF_8));
    }

    public boolean settingsComplete(BugTrackerSettings settings) {
        return !(settings == null ||
                StringUtils.isNullOrEmpty(settings.getUrl()) ||
                StringUtils.isNullOrEmpty(settings.getLogin()) ||
                StringUtils.isNullOrEmpty(settings.getPassword()));
    }

    public boolean settingsComplete() {
        BugTrackerSettings settings = getBugTrackerSettings();
        return settingsComplete(settings);
    }

    public BugTrackerSettings getBugTrackerSettings() {
        if (bugTrackerSettings == null) {
            Settings soapuiSettings = SoapUI.getSettings();
            bugTrackerSettings = new BugTrackerSettings(soapuiSettings.getString(BugTrackerPrefs.DEFAULT_URL, ""),
                    soapuiSettings.getString(BugTrackerPrefs.LOGIN, ""),
                    soapuiSettings.getString(BugTrackerPrefs.PASSWORD, ""));
        }
        return bugTrackerSettings;
    }

    private WsdlProject findActiveElementRootProject(ModelItem activeElement) {
        return ModelSupport.getModelItemProject(activeElement);
    }

    private InputStream getExecutionLog(String loggerName) {
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(loggerName);
        try {
            return (InputStream) new FileInputStream(log.getName());
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}