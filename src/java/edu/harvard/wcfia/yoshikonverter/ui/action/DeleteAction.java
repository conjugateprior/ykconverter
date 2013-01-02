package edu.harvard.wcfia.yoshikonverter.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

/**
 * @author will
 */
public class DeleteAction extends AbstractYKAction {
    
    private String accelKey = "D" ;
    private String tooltip = "Discard the selected conversion result";
    
    public DeleteAction(YKConverter conv){
        super(conv, "Discard");
        putValue(Action.SHORT_DESCRIPTION, tooltip);
        putValue(Action.ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(
                        KeyStroke.getKeyStroke(accelKey).getKeyCode(), 
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        ); 
    }
        
    public void actionPerformed(ActionEvent e) {
        converter.deleteView();
    }

}
