package com.smartbear.ready.plugin.jira.settings;

import com.eviware.soapui.settings.Setting;
import com.smartbear.ready.plugin.jira.factories.JiraPrefsFactory;

public interface BugTrackerPrefs {
    @Setting(name = JiraPrefsFactory.BUG_TRACKER_URL, description = JiraPrefsFactory.BUG_TRACKER_URL_DESCRIPTION)
    public final static String DEFAULT_URL = BugTrackerPrefs.class.getSimpleName() + "@" + "jira-default-url";

    @Setting(name = JiraPrefsFactory.BUG_TRACKER_LOGIN_CAPTION, description = JiraPrefsFactory.BUG_TRACKER_LOGIN_DESCRIPTION)
    public final static String LOGIN = BugTrackerPrefs.class.getSimpleName() + "@" + "jira-login";

    @Setting(name = JiraPrefsFactory.BUG_TRACKER_PASSWORD, description = JiraPrefsFactory.BUG_TRACKER_PASSWORD_DESCRIPTION)
    public final static String PASSWORD = BugTrackerPrefs.class.getSimpleName() + "@" + "jira-password";

    @Setting(name = JiraPrefsFactory.SKIP_RELEASED_VERSIONS, description = JiraPrefsFactory.SKIP_RELEASED_VERSIONS_DESCRIPTION)
    public final static String SKIP_VERSIONS = BugTrackerPrefs.class.getSimpleName() + "@" + "jira-skip-released-versions";
}

