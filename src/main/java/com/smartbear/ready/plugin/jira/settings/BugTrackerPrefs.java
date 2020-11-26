package com.smartbear.ready.plugin.jira.settings;

import com.eviware.soapui.settings.Setting;
import com.smartbear.ready.plugin.jira.factories.JiraPrefsFactory;

public interface BugTrackerPrefs {
    @Setting(name = JiraPrefsFactory.BUG_TRACKER_URL, description = JiraPrefsFactory.BUG_TRACKER_URL_DESCRIPTION)
    String DEFAULT_URL = BugTrackerPrefs.class.getSimpleName() + "@" + "jira-default-url";

    @Setting(name = JiraPrefsFactory.BUG_TRACKER_LOGIN, description = JiraPrefsFactory.BUG_TRACKER_LOGIN_DESCRIPTION)
    String LOGIN = BugTrackerPrefs.class.getSimpleName() + "@" + "jira-login";

    @Setting(name = JiraPrefsFactory.BUG_TRACKER_PASSWORD, description = JiraPrefsFactory.BUG_TRACKER_PASSWORD_DESCRIPTION)
    String PASSWORD = BugTrackerPrefs.class.getSimpleName() + "@" + "jira-password";

    @Setting(name = JiraPrefsFactory.SKIP_RELEASED_VERSIONS, description = JiraPrefsFactory.SKIP_RELEASED_VERSIONS_DESCRIPTION)
    String SKIP_VERSIONS = BugTrackerPrefs.class.getSimpleName() + "@" + "jira-skip-released-versions";
}

