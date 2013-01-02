package edu.harvard.wcfia.yoshikonverter.ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.text.DefaultEditorKit;

/**
 * An editable view of a document, indexed by the original file
 * @author will
 */
public class DocumentView extends JPanel {
    
    private JTextArea area;
    private File file;
    private TransferHandler handler;
    
    /**
     * An editable document view.  The TransferHandler is installed in the
     * JTextArea.
     *  
     * @param f file
     * @param txt file contents
     * @param thandler what happens when you drag things onto the text area
     */
    public DocumentView(File f, String txt, TransferHandler thandler){
        super(new BorderLayout());
        file = f;
        handler = thandler;
        
        area = new JTextArea(20,40); // editable
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        area.setText(txt);
        area.setTransferHandler(handler); // pass this functionality down
        
        InputMap inputMap = area.getInputMap();
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_X,
                                               Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        inputMap.put(key, DefaultEditorKit.cutAction);
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(p, BorderLayout.CENTER);
    }
    
    /**
     * Returns the file from which the current displayed text was derived
     * @return file
     */
    public File getFile(){
        return file;
    }
    
    /**
     * Returns the (possibly edited) file contents.
     * @return text
     */
    public String getText(){
        String txt = area.getText();
        return txt; // they may have edited it...
    }
    
    public void tuc(){
    	String sel = area.getSelectedText();
    	if (sel != null)
    		area.setText(sel);
    }
    
    /**
     * Saves file contents in a new file, with UTF-8 text encoding.
     * @param newFile
     * @throws IOException
     */
    public void save(File newFile) throws IOException{
        FileOutputStream fos = new FileOutputStream(newFile);
        Writer out = new OutputStreamWriter(fos, "UTF8");
        BufferedWriter bw = new BufferedWriter(out);
        bw.write(getText());
        bw.close();
    }
}
