package com.smartbear.ready.plugin.jira.settings;

import com.sun.istack.internal.NotNull;

/**
 * Created by avdeev on 19.03.2015.
 */
public class BugTrackerSettings {
    private String url;
    private String login;
    private String password;

    public BugTrackerSettings(@NotNull String url, @NotNull String login, @NotNull String password){
        this.url = url;
        this.login = login;
        this.password = password;
    }

    public String getUrl (){
        return url;
    }

    public String getLogin (){
        return login;
    }

    public String getPassword (){
        return password;
    }
}
