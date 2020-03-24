package com.smartbear.ready.plugin.jira.settings;

/**
 * Created by avdeev on 19.03.2015.
 */
public class BugTrackerSettings {
    private String url;
    private String login;
    private String password;
    private boolean skipReleasedVersions;

    public BugTrackerSettings(String url, String login, String password, boolean skipReleasedVersions){
        this.url = url;
        this.login = login;
        this.password = password;
        this.skipReleasedVersions = skipReleasedVersions;
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

    public boolean getSkipReleasedVersions () { return skipReleasedVersions; }
}
