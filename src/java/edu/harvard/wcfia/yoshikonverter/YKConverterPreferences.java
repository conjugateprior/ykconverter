package edu.harvard.wcfia.yoshikonverter;

import java.io.File;
import java.io.IOException;

/**
 * Preferences bean.
 * @author will
 *
 */
public class YKConverterPreferences {

    protected boolean savedAlongside;
    protected File saveDirectory;
    protected boolean suffixReplaced;
    
    public YKConverterPreferences(){
        // defaults
        savedAlongside = true;
        saveDirectory = new File(System.getProperty("user.home"));
        suffixReplaced = true;
    }

    public boolean isSavedAlongside() {
        return savedAlongside;
    }

    public void setSavedAlongside(boolean s) {
        savedAlongside = s;
    }

    public File getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(File d) {
        saveDirectory = d;
    }

    public boolean isSuffixReplaced() {
        return suffixReplaced;
    }

    public void setSuffixReplaced(boolean r) {
        suffixReplaced = r;
    }
    
    public void save() throws IOException {
        
    }
    
}
