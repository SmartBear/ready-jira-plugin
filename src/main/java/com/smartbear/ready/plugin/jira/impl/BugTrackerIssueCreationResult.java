package com.smartbear.ready.plugin.jira.impl;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.sun.istack.internal.NotNull;

public class BugTrackerIssueCreationResult {
    protected BasicIssue issue;
    protected String error;
    protected boolean isSuccess;

    private BugTrackerIssueCreationResult (){}

    public BugTrackerIssueCreationResult (@NotNull BasicIssue issue){
        this.issue = issue;
        this.error = null;
        this.isSuccess = true;
    }

    /*
    * This constructor should be used for failed cases.
    * */
    public BugTrackerIssueCreationResult (@NotNull String error){
        this.issue = null;
        this.error = error;
        this.isSuccess = false;
    }


    public boolean getSuccess (){
        return isSuccess;
    }

    public BasicIssue getIssue(){
        return issue;
    }

    public String getError () {
        return error;
    }
}
