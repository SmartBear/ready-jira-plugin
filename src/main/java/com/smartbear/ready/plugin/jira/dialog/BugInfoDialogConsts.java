package com.smartbear.ready.plugin.jira.dialog;

import com.eviware.soapui.support.UISupport;
import com.eviware.x.form.support.AField;
import com.eviware.x.form.support.AForm;

/**
 * Created by avdeev on 16.03.2015.
 */
public interface BugInfoDialogConsts {
    public final static String TARGET_ISSUE_PROJECT = "Project";

    public final static String ISSUE_TYPE = "Issue type";

    public final static String ISSUE_PRIORITY = "Priority";

    public final static String ISSUE_SUMMARY = "Bug summary";

    public final static String ISSUE_DESCRIPTION = "Bug description";

    public final static String ATTACH_SOAPUI_LOG = "Attach SoapUI NG log file";

    public final static String ATTACH_PROJECT = "Attach project file";

    public final static String ATTACH_LOADUI_LOG = "Attach LoadUI NG log file";
    public final static String ATTACH_SERVICEV_LOG = "Attach ServiceV log file";
    public final static String ATTACH_READYAPI_LOG = "Attach ReadyAPI log file";
}
