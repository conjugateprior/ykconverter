package edu.harvard.wcfia.yoshikonverter;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {
	
	public String appName;
	public String appVersion;
	public String iconPath;
	public int buildNumber;
	public String appCopy;
	
	public ApplicationProperties(){
    	Properties props = new Properties();
    	try {
            InputStream str = ApplicationProperties.class.getClassLoader()
            .getResourceAsStream("application.properties");
            props.load(str);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        appName = props.getProperty("application.name", "application.name");
        appVersion = props.getProperty("version", "version");
        iconPath = props.getProperty("icon.path", "icon.path");
        buildNumber = Integer.parseInt(props.getProperty("build.number", "-1"));
        appCopy = props.getProperty("application.copyright", "application.copyright");			
	}
}

