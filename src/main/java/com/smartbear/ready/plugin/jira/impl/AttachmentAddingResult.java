package com.smartbear.ready.plugin.jira.impl;

/**
 * Created by avdeev on 18.03.2015.
 */
public class AttachmentAddingResult extends BugTrackerActionResult {
    public AttachmentAddingResult() {
        this.isSuccess = true;
        this.error = null;
    }

    public AttachmentAddingResult(String error){
        this.isSuccess = false;
        this.error = error;
    }
}
