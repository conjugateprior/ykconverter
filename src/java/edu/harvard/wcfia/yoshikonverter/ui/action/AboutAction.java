package edu.harvard.wcfia.yoshikonverter.ui.action;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import edu.harvard.wcfia.yoshikonverter.ApplicationProperties;
import edu.harvard.wcfia.yoshikonverter.Environment;
import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

/**
 * @author will
 */
public class AboutAction extends AbstractYKAction {

    protected JDialog aboutDialog;
    
    public AboutAction(YKConverter conv) {
        super(conv, "About");
    }
    
    public void actionPerformed(ActionEvent e) {
        if (aboutDialog == null){ // this is first invocation
        	ApplicationProperties ap = Environment.getApplicationProperties();
        	Icon icon = Environment.getIcon(ap.iconPath);
        	JPanel aboutPanel = new JPanel(new BorderLayout());
        	aboutPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        	JPanel message = new JPanel(new GridLayout(3,1));
        	JLabel name = new JLabel(ap.appName, SwingConstants.CENTER);
        	message.add(name);
        	String label = "v." + ap.appVersion + "(" + ap.buildNumber + ")";
        	JLabel version = new JLabel(label, SwingConstants.CENTER);
        	version.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        	message.add(version);
        	JLabel copy = new JLabel(ap.appCopy, SwingConstants.CENTER);
        	message.add(copy);
        	aboutPanel.add(message, BorderLayout.SOUTH);
        	JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        	iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        	aboutPanel.add(iconLabel, BorderLayout.CENTER);
        	aboutDialog = new JDialog(converter, "About", true);
        	aboutDialog.getContentPane().add(aboutPanel);
        	aboutDialog.pack();
        	aboutDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        	aboutDialog.setLocationRelativeTo(converter);
        }
        aboutDialog.setVisible(true);
    }
}
