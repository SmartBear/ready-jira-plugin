package com.smartbear.ready.plugin.jira.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public interface SimpleBugTrackerProvider {
    public BugTrackerIssueCreationResult createIssue(String projectKey, String issueKey, String priority, String summary, String description, Map<String, String> extraRequiredValues);
    public BugTrackerAttachmentCreationResult attachFile(URI attachmentUri, String fileName, InputStream inputStream);
}
