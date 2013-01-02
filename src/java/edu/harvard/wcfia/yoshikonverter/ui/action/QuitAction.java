package edu.harvard.wcfia.yoshikonverter.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

/**
 * @author will
 */
public class QuitAction extends AbstractYKAction {
    
    private static String menuText = "Quit";
    
    public QuitAction(YKConverter conv){
        super(conv, menuText);
    }
    
    public QuitAction(YKConverter conv, Icon icon) {
        super(conv, menuText, icon);
    }

    public void actionPerformed(ActionEvent e) {
        converter.handleQuit();
    }

}
