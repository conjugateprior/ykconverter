package edu.harvard.wcfia.yoshikonverter.exception;

import java.io.File;

/**
 * @author will
 */
public class ConversionException extends Exception {

    private File file;

    public ConversionException(File f, Throwable towel){
        super("Could not convert file " + f.getName(), towel);
        file = f;
    }
    
    public ConversionException(File f){
        super("Could not convert file " + f.getName());
        file = f;
    }
    
    public File getFile(){
        return file;
    }
    
}
