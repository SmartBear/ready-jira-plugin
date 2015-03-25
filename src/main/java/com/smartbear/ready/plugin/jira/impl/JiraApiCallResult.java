package com.smartbear.ready.plugin.jira.impl;

/**
 * Created by avdeev on 23.03.2015.
 */
public class JiraApiCallResult <ResultType>{
    final ResultType result;
    boolean success;
    Throwable error;

    public JiraApiCallResult (ResultType result){
        this.result = result;
        this.success = true;
        this.error = null;
    }

    public JiraApiCallResult (Throwable error){
        this.result = null;
        this.success = false;
        this.error = error;
    }

    public ResultType getResult (){
        return result;
    }

    public boolean isSuccess (){
        return success;
    }

    public Throwable getError (){
        return error;
    }
}
