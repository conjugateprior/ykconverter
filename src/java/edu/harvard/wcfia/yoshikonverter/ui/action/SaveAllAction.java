package edu.harvard.wcfia.yoshikonverter.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

/**
 * @author will
 */
public class SaveAllAction extends AbstractYKAction {
    
    //private String accelKey = "S"; // shift S actually
    private String tooltip = 
        "Save all the conversion results as text documents";
    
    public SaveAllAction(YKConverter conv) {
        super(conv, "Save All"); 
        putValue(Action.SHORT_DESCRIPTION, tooltip);
    }

    public void actionPerformed(ActionEvent e) {
        converter.saveAll();
    }

}
