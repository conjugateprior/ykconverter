package edu.harvard.wcfia.yoshikonverter.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

/**
 * @author will
 */
public class OpenAction extends AbstractYKAction {
    
    private String accelKey = "O" ;
    private String tooltip = "Open one or more files and convert them to text";
    
    private JFileChooser chooser; 
    
    public OpenAction(YKConverter conv) {
        super(conv, "Open...");
        
        putValue(Action.SHORT_DESCRIPTION, tooltip);
        putValue(Action.ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(
                        KeyStroke.getKeyStroke(accelKey).getKeyCode(), 
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
        ); 
    }
    
    public void actionPerformed(ActionEvent e) {
        if (chooser == null){
        	chooser = new JFileChooser();
        	chooser.setMultiSelectionEnabled(true);
        }
        
        File[] f = null;

        int resp = chooser.showOpenDialog(converter);
        if (resp != JFileChooser.APPROVE_OPTION) { return; }
        f = chooser.getSelectedFiles();
        if (f == null || f.length == 0)
        	return;
        converter.openFile(f);
        
    }
}
