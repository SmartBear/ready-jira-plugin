package com.smartbear.ready.plugin.jira.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public interface SimpleBugTrackerProvider {
    public IssueCreationResult createIssue(String projectKey, String issueKey, String summary, String description, Map<String, String> extraRequiredValues);
    public AttachmentAddingResult attachFile(URI attachmentUri, String fileName, InputStream inputStream);
    public AttachmentAddingResult attachFile(URI attachmentUri, String filePath);
}
