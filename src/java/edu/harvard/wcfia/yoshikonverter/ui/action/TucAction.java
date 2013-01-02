package edu.harvard.wcfia.yoshikonverter.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import edu.harvard.wcfia.yoshikonverter.Environment;
import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

public class TucAction extends AbstractYKAction {

	private String tooltip = 
		"Delete all text except the selection";

	public TucAction(YKConverter conv) {
		super(conv, "TUC"); 
		putValue(Action.SHORT_DESCRIPTION, tooltip);
		
		// apple R
		if (Environment.isMac())
			putValue(Action.ACCELERATOR_KEY, 
	                KeyStroke.getKeyStroke(
	                        KeyStroke.getKeyStroke("R").getKeyCode(), 
	                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
	                ));
	}

	public void actionPerformed(ActionEvent e) {
		converter.handleTuc();
	}

}

