package com.smartbear.ready.plugin.jira.impl;

import com.eviware.soapui.support.UISupport;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Created by avdeev on 25.03.2015.
 */
public class IssueInfoDialog {
    public static int showDialog(String issueType, String link, String issueKey) {
        return JOptionPane.showConfirmDialog(null,
                getPanel(issueType, link, issueKey),
                issueKey,
                JOptionPane.OK_OPTION);//TODO: strange, but it doesn't work properly.
    }

    private static JPanel getPanel(String issueType, String link, String issueKey) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(String.format("%s was successfully created.", issueType));
        JLabel labelEx = new JLabel("You can check created item using link below.");
        panel.add(label);
        panel.add(labelEx);
        panel.add(UISupport.createLabelLink(link, link));
        return panel;
    }
}
