package edu.harvard.wcfia.yoshikonverter.ui.action;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

/**
 * @author will
 */
public abstract class AbstractYKAction extends AbstractAction {

    protected YKConverter converter;
    
    public AbstractYKAction(YKConverter conv, String name) {
        super(name);
        converter = conv;
    }

    public AbstractYKAction(YKConverter conv, String name, Icon icon) {
        super(name, icon);
        converter = conv;
    }

}
