package com.smartbear.ready.plugin.jira.impl;

/**
 * Created by avdeev on 25.03.2015.
 */
public abstract class BugTrackerActionResult {
    protected String error;
    protected boolean isSuccess;

    public boolean getSuccess (){
        return isSuccess;
    }

    public String getError () {
        return error;
    }
}
