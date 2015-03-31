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

    public static final String CHECK_CREATED_ITEM = "You can check created item using link below.";

    public static void showDialog(String issueType, String link, String issueKey) {
        JOptionPane.showMessageDialog(null,
                getPanel(issueType, link, issueKey),
                issueKey,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static JPanel getPanel(String issueType, String link, String issueKey) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(String.format("%s was successfully created.", issueType));
        JLabel labelEx = new JLabel(CHECK_CREATED_ITEM);
        panel.add(label);
        panel.add(labelEx);
        panel.add(UISupport.createLabelLink(link, link));
        return panel;
    }
}
