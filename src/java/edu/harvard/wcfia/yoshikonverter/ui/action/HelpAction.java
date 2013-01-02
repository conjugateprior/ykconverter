package edu.harvard.wcfia.yoshikonverter.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import com.apple.eawt.Application;

import edu.harvard.wcfia.yoshikonverter.Environment;
import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

/**
 * @author will
 */
public class HelpAction extends AbstractYKAction {

    private String accelKey = "H";
    private String tooltip = "Launch online help";

    public HelpAction(YKConverter conv){
        super(conv, "Help");

        putValue(Action.SHORT_DESCRIPTION, tooltip);
        if (!Environment.isMac()){ // mac help is separate
            putValue(Action.ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(
                        KeyStroke.getKeyStroke(accelKey).getKeyCode(), 
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                )
            );
        } else {
            putValue(Action.ACCELERATOR_KEY, 
                    KeyStroke.getKeyStroke(
                    KeyEvent.VK_SLASH, KeyEvent.SHIFT_MASK | 
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
    }

    private void fillOurDir(File ourDir){
    	File f = new File(ourDir, "build_" + 
    			Environment.getApplicationProperties().buildNumber);
    	File index = new File(ourDir, "index.html");
    	try {
    		 // create a build number file
    		f.createNewFile();
    		
    		// drop the index page into ourDir
    		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new 
    				FileOutputStream(index), "UTF-8"));
    		w.write(Environment.getStringResource("onlinehelp/index.html"));
    		w.close();
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}    	
    }
    
    private void buildOurDir(File ourDir){
    	ourDir.mkdirs();
    	fillOurDir(ourDir);
    }
    
    // non-recursive delete
    private void replaceDirContents(File ourDir){
    	File [] fs = ourDir.listFiles();
    	for (int ii = 0; ii < fs.length; ii++) 
			fs[ii].delete();
    	fillOurDir(ourDir);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (Environment.isMac()){
        	System.err.println("launching help viewer");
        	Application.getApplication().openHelpViewer();
        
        } else { // Windows or Linux
        	String userdir = System.getProperty("user.home");
        	File ourDir = new File(userdir, ".ykconverter");
        	if (!ourDir.exists()) // if it is the first time
        		buildOurDir(ourDir);

        	int ourBuildNumber = Environment.getApplicationProperties().buildNumber;
        	File[] fs = ourDir.listFiles();
        	for (int ii = 0; ii < fs.length; ii++) {
        		String name = fs[ii].getName();
				if (name.startsWith("build_")){
					int existingBuildNumber = Integer.parseInt(name.substring(6, name.length()));
					if (existingBuildNumber < ourBuildNumber)
						replaceDirContents(ourDir);
					break;
				}
			}
        	
        	/*
        	Preferences preferences = 
        		Preferences.userNodeForPackage(HelpAction.class);
        	String helpText = preferences.get("helpIndex", "");
        	if (helpText.equals("")){
        		// extract the page from the jar and drop it into the preferences
        		helpText = Environment.getStringResource("index.html");
        		preferences.put("helpIndex", helpText);
        		try {
        			preferences.flush();
        		} catch (BackingStoreException bex){
        			bex.printStackTrace();
        		}
        	}
        	*/
        	
        	File index = new File(ourDir, "index.html");
        	try {
        		// Now we're really Java 6
        		java.awt.Desktop.getDesktop().browse(index.toURI());
        	} catch (Exception ex){
        		ex.printStackTrace(); // nothing much to do about this...
        	}
        	
        }
    }

}
