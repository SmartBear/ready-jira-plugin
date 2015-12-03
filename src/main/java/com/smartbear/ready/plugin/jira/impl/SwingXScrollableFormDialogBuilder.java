/*
 * Copyright 2004-2014 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
*/

package com.smartbear.ready.plugin.jira.impl;

import com.eviware.soapui.impl.support.actions.ShowOnlineHelpAction;
import com.eviware.soapui.support.action.swing.ActionList;
import com.eviware.soapui.support.action.swing.DefaultActionList;
import com.eviware.x.form.XForm;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormDialogBuilder;
import com.eviware.x.impl.swing.JFormDialog;
import com.eviware.x.impl.swing.JTabbedFormDialog;
import com.eviware.x.impl.swing.JWizardDialog;
import com.eviware.x.impl.swing.SwingXFormDialog;
import com.eviware.x.impl.swing.SwingXFormImpl;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;

public class SwingXScrollableFormDialogBuilder extends XFormDialogBuilder {
    private String name;
    private SwingXFormDialog dialog;

    public SwingXScrollableFormDialogBuilder(String name) {
        this.name = name;
    }

    @Override
    public XForm createForm(String name) {
        SwingXScrollableFormImpl form = new SwingXScrollableFormImpl(name);
        ((SwingXScrollableFormImpl) form).addSpace(5);
        addForm(form);
        return form;
    }

    @Override
    public XForm createForm(String name, FormLayout layout) {
        XForm form = new SwingXScrollableFormImpl(name, layout);
        ((SwingXScrollableFormImpl) form).addSpace(5);
        addForm(form);
        return form;
    }

    @Override
    public XFormDialog buildDialog(ActionList actions, String description, ImageIcon icon) {
        XForm[] forms = getForms();
        dialog = forms.length > 1 ? new JTabbedFormDialog(name, forms, actions, description, icon) : new JScrollableFormDialog(
                name, (SwingXScrollableFormImpl) forms[0], actions, description, icon);

        return dialog;
    }

    @Override
    public XFormDialog buildWizard(String description, ImageIcon icon, String helpURL) {
        Action helpAction = (helpURL.length() > 0 ? new ShowOnlineHelpAction(helpURL) : null);
        XForm[] forms = getForms();
        dialog = new JWizardDialog(name, forms, helpAction, description, icon);

        return dialog;
    }

    @Override
    public ActionList buildOkCancelActions() {
        DefaultActionList actions = new DefaultActionList("Actions");
        actions.addAction(new OKAction());
        actions.addAction(new CancelAction());
        return actions;
    }

    @Override
    public ActionList buildOkCancelHelpActions(String url) {
        DefaultActionList actions = new DefaultActionList("Actions");
        actions.addAction(new ShowOnlineHelpAction(url));
        OKAction okAction = new OKAction();
        actions.addAction(okAction);
        actions.addAction(new CancelAction());
        actions.setDefaultAction(okAction);
        return actions;
    }

    @Override
    public ActionList buildHelpActions(String url) {
        DefaultActionList actions = new DefaultActionList("Actions");
        actions.addAction(new ShowOnlineHelpAction(url));
        return actions;
    }

    protected final class OKAction extends AbstractAction {
        public OKAction() {
            super("OK");
        }

        public void actionPerformed(ActionEvent e) {
            if (dialog != null && dialog.validate()) {
                dialog.setReturnValue(XFormDialog.OK_OPTION);
                dialog.setVisible(false);
            }
        }
    }

    protected final class CancelAction extends AbstractAction {
        public CancelAction() {
            super("Cancel");
        }

        public void actionPerformed(ActionEvent e) {
            if (dialog != null) {
                dialog.setReturnValue(XFormDialog.CANCEL_OPTION);
                dialog.setVisible(false);
            }
        }
    }
}
