package com.smartbear.ready.plugin.jira.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public interface SimpleBugTrackerProvider {
    IssueCreationResult createIssue(String projectKey, String issueKey, String summary, String description, Map<String, String> extraRequiredValues);
    AttachmentAddingResult attachFile(URI attachmentUri, String fileName, InputStream inputStream);
    AttachmentAddingResult attachFile(URI attachmentUri, String filePath);
}
