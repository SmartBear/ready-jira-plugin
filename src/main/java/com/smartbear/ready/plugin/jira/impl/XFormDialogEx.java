package com.smartbear.ready.plugin.jira.impl;

import com.eviware.x.form.XFormDialog;

/**
 * Created by avdeev on 30.11.2015.
 */
public abstract interface XFormDialogEx extends XFormDialog{
    public int getWidth();
    public int getHeight();
    public void setHeight(int height);
}
