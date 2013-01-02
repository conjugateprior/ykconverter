package edu.harvard.wcfia.yoshikonverter.ui;

import java.nio.charset.Charset;

public class CharsetWrapper {
	
	protected String canonicalName;
	protected String displayName;

	public CharsetWrapper(Charset cs) {
		canonicalName = cs.name();
		displayName = cs.displayName();
	}

	public String toString(){
		return displayName;
	}

	public int hashCode(){
		return canonicalName.hashCode();
	}
	
	public boolean equals(Object o){
		CharsetWrapper other = (CharsetWrapper)o;
		return canonicalName.equals(other.getCanonicalName());
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	public String getCanonicalName(){
		return canonicalName;
	}
}


