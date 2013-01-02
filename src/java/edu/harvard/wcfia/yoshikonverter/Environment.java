package edu.harvard.wcfia.yoshikonverter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Environment {

	private static ApplicationProperties applicationProperties = 
		new ApplicationProperties();
	
    private static boolean macintosh = (System.getProperty("mrj.version") != null);
    private static boolean windows = System.getProperty("os.name").startsWith("Windows");

    public static boolean isMac(){
        //return false; // debug
        return macintosh;
    }
    
    public static boolean isWindows(){
        //return false; // debug
        return windows;
    }
    
    public static ApplicationProperties getApplicationProperties(){
    	return applicationProperties;
    }
    
    public static String getStringResource(String name){
        BufferedReader ic = null;
        StringBuffer sb = new StringBuffer();
        try {
            ClassLoader cl = Environment.class.getClassLoader();
            ic = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(name)));
            String line = null;
            String sep = System.getProperty("line.separator","\n");
            while ((line = ic.readLine()) != null){
            	sb.append(line);
            	sb.append(sep);
            }
            ic.close();
            
            return sb.toString();
            
        } catch (Exception e){
        	e.printStackTrace();
            return "NO STRING RESOURCE FOUND";
        }
     }
    
    public static Icon getIcon(String name){
        Icon ic = null;
        try {
            ClassLoader cl = Environment.class.getClassLoader();
            ic = new ImageIcon(cl.getResource(name));
            return ic;
        } catch (Exception e){
            try {
                ic = new ImageIcon("resources/" + name);
                return ic;
            } catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }
     }
     
    
}
