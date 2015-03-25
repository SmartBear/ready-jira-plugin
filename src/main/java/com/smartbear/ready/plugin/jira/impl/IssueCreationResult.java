package com.smartbear.ready.plugin.jira.impl;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.sun.istack.internal.NotNull;

public class IssueCreationResult extends BugTrackerActionResult{
    protected BasicIssue issue;
    private IssueCreationResult(){}

    public IssueCreationResult(@NotNull BasicIssue issue){
        this.issue = issue;
        this.error = null;
        this.isSuccess = true;
    }

    /*
    * This constructor should be used for failed cases.
    * */
    public IssueCreationResult(@NotNull String error){
        this.issue = null;
        this.error = error;
        this.isSuccess = false;
    }

    public BasicIssue getIssue(){
        return issue;
    }
}
