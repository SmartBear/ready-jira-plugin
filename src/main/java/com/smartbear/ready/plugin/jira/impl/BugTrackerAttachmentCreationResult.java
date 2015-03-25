package com.smartbear.ready.plugin.jira.impl;

/**
 * Created by avdeev on 18.03.2015.
 */
public class BugTrackerAttachmentCreationResult {
    private boolean success;
    private String error;

    public BugTrackerAttachmentCreationResult () {
        this.success = true;
        this.error = null;
    }

    public BugTrackerAttachmentCreationResult (String error){
        this.success = false;
        this.error = error;
    }

    public String getError (){
        return error;
    }

    public boolean getSuccess (){
        return success;
    }
}
