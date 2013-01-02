package edu.harvard.wcfia.yoshikonverter.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

public class SaveAction extends AbstractYKAction {
    
    private String accelKey = "S" ;
    private String tooltip = "Save the selected conversion result as a text document";
    
    public SaveAction(YKConverter conv) {
        super(conv, "Save");
        putValue(Action.SHORT_DESCRIPTION, tooltip);
        putValue(Action.ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(
                        KeyStroke.getKeyStroke(accelKey).getKeyCode(), 
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        ); 
    }

    public void actionPerformed(ActionEvent ae){
        converter.save();
    }
    
}
