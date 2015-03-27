package com.smartbear.ready.plugin.jira.factories;

import com.eviware.soapui.actions.Prefs;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.plugins.auto.PluginPrefs;
import com.eviware.soapui.support.components.SimpleForm;
import com.eviware.soapui.support.types.StringToStringMap;
import com.smartbear.ready.plugin.jira.settings.BugTrackerPrefs;

@PluginPrefs
public class JiraPrefsFactory implements Prefs {
    public static final String BUG_TRACKER_LOGIN = "Login";
    public static final String BUG_TRACKER_PASSWORD = "Password";
    public static final String BUG_TRACKER_LOGIN_DESCRIPTION = "User name/login for the specified bug tracker";
    public static final String BUG_TRACKER_PASSWORD_DESCRIPTION = "Password";
    public static final String BUG_TRACKER_URL = "Bug tracker url";
    public static final String BUG_TRACKER_URL_DESCRIPTION = "Bug tracker url";

    private SimpleForm form;

    @Override
    public SimpleForm getForm() {
        if (form == null) {
            form = new SimpleForm();
            form.addSpace();
            form.appendTextField(BUG_TRACKER_LOGIN, BUG_TRACKER_LOGIN_DESCRIPTION);
            form.appendPasswordField(BUG_TRACKER_PASSWORD, BUG_TRACKER_PASSWORD_DESCRIPTION);
            form.appendTextField(BUG_TRACKER_URL, BUG_TRACKER_URL_DESCRIPTION);
        }

        return form;
    }

    @Override
    public void setFormValues(Settings settings) {
        getForm().setValues(getValues(settings));
    }

    @Override
    public void getFormValues(Settings settings) {
        StringToStringMap values = new StringToStringMap();
        form.getValues(values);
        storeValues(values, settings);
    }

    @Override
    public void storeValues(StringToStringMap values, Settings settings) {
        settings.setString(BugTrackerPrefs.LOGIN, values.get(BUG_TRACKER_LOGIN));
        settings.setString(BugTrackerPrefs.PASSWORD, values.get(BUG_TRACKER_PASSWORD));
        settings.setString(BugTrackerPrefs.DEFAULT_URL, values.get(BUG_TRACKER_URL));
    }

    @Override
    public StringToStringMap getValues(Settings settings) {
        StringToStringMap values = new StringToStringMap();
        values.put(BUG_TRACKER_LOGIN, settings.getString(BugTrackerPrefs.LOGIN, ""));
        values.put(BUG_TRACKER_PASSWORD, settings.getString(BugTrackerPrefs.PASSWORD, ""));
        values.put(BUG_TRACKER_URL, settings.getString(BugTrackerPrefs.DEFAULT_URL, ""));
        return values;
    }

    @Override
    public String getTitle() {
        return "Jira";
    }
}
