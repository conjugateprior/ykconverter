package edu.harvard.wcfia.yoshikonverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.harvard.wcfia.yoshikonverter.util.FileUtil;

/**
 * A pile of static stuff to get text out of html, MSWord, and pdf documents.
 * 
 * @author will
 */
public class Converter {

	static class RemoveMarkupHandler extends DefaultHandler {
		
		private StringBuffer sb = new StringBuffer();
		
		private boolean ignoreScript = false;
		private boolean ignoreStyle = false;
		
		public void characters(char[] ch, int start, int length) {
			if (!ignoreScript && !ignoreStyle){
				String text = new String(ch, start, length);
				sb.append(text + " ");
			}
		}
		public void error(SAXParseException e) throws SAXException {
			e.printStackTrace();
		}
		
		public String getContent() {
			return sb.toString();
		}
		
		/*
		public void ignorableWhitespace(char[] ch, int start, int length)
				throws SAXException {
			if (!ignoreScript && !ignoreStyle){
				//String text = new String(ch, start, length);
				sb.append("( )");
			}
			super.ignorableWhitespace(ch, start, length); 
		}
		*/
		
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			if (localName.toLowerCase().equals("script")){
				ignoreScript = true;
			} else if (localName.toLowerCase().equals("style")){
				ignoreStyle = true;
			}
			super.startElement(uri, localName, name, attributes); // as before 
		}
		
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			if (localName.toLowerCase().equals("script")){ 
				ignoreScript = false;
			} else if (localName.toLowerCase().equals("style")){
				ignoreStyle = false;
			}
			super.endElement(uri, localName, name); // as before 
		}
	}

	private static PDFTextStripper stripper;
	static {
		try {
			stripper = new PDFTextStripper();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static String parserName = "org.ccil.cowan.tagsoup.Parser";
	private static XMLReader parser;
	
	private static Pattern windowsLinebreaks = Pattern.compile("(\r\n)+");
	private static Pattern tabs = Pattern.compile("[\t]{2,}+");
	
	/**
	 * Inhale an html file, and retrieve the text.
	 * @throws SAXException
	 */
	public static String inhaleHtml(File file, String cs) throws IOException, SAXException {
		if (parser == null)
			parser = XMLReaderFactory.createXMLReader(parserName);
		
		InputSource source = 
			new InputSource(new InputStreamReader(new FileInputStream(file), cs));
		RemoveMarkupHandler t = new RemoveMarkupHandler();
		parser.setContentHandler(t);
		parser.parse(source);
		String firstPass = t.getContent();
		
		// post process for <!-- --> bits that usually indicate javascript sections
		//String secondPass = removeXmlComments(firstPass);
		
		String thirdPass = compactSpacesAndTabs( compactWindowsLinebreaks(firstPass) );
		String[] spl = thirdPass.split("[\r\n\f]");
		StringBuffer ss = new StringBuffer();
		int newlinecounter = 0;
		for (int ii = 0; ii < spl.length; ii++) {
			String line = spl[ii].trim();
			if (line.length() == 0){
				if (newlinecounter == 2)
					ss.append("\n");
				newlinecounter++;
			} else {
				ss.append(line);
				ss.append("\n");
				newlinecounter = 0;
			}
		}		
		return ss.toString();
	}

	
	public static String compactSpacesAndTabs(String s){
		Matcher m = tabs.matcher(s);
		return m.replaceAll("    ");
	}
	
	
	public static String compactWindowsLinebreaks(String s){
		Matcher m = windowsLinebreaks.matcher(s);
		return m.replaceAll(" \n");
	}
	
	/**
	 * A simple implementation that assumes non-nested comments.
	 */
	private static String removeXmlComments(String txt){
		int index = 0;
		int newIndex = 0;
		StringBuffer sb = new StringBuffer();
		while (true){
			newIndex = txt.indexOf("<!--", index);
			if (newIndex == -1){
				sb.append(txt.substring(index, txt.length()));
				break;
			} 
			sb.append(txt.substring(index, newIndex));
			index = newIndex+4;

			// now look for closing
			newIndex = txt.indexOf("-->", index);
			if (newIndex == -1){
				break;
			}
			index = newIndex+3;
		}		
		return sb.toString();
	}
	
	/**
	 * Inhale an MSWord file and retrieve the text.
	 */
	public static String inhaleMSFormat(File file) throws IOException {
		InputStream str = null;
		try {
			str = new FileInputStream(file);
			HWPFDocument doc = new HWPFDocument(str);
			String contents = doc.getRange().text();
			return contents;
		} finally {
			if (str != null) {
				str.close();
			}
		}
	}

	// TODO make this sensitive to char encoding
	/**
	 * Inhale a pdf file and retrieve the text.
	 */
	public static String inhalePdf(File file) throws IOException {
		PDDocument doc = null;
		String res = null;
		try {
			doc = PDDocument.load(file);
			if (doc.isEncrypted())
				throw new IOException(file.getName() + " is encrypted");
			res = stripper.getText(doc);
			
		} finally {
			if (doc != null)
				doc.close();
		}
		
		// a little bit of post processing
		//   join hyphenated words
		//   (later) make curly quotes normal ones: .replaceAll("[\\u0093\\u0094]", "\"" );
		
		String newres = res.replaceAll("(\\w+)-[ ]*\\n[ ]*(\\w+)", "$1$2");
		return newres;
	}

	/**
	 * Inhale a text file in the specified encoding.
	 */
	public static String inhale(File f, String cs) throws IOException {
		if (cs == null)
			return new String(FileUtil.getBytes(f));
		return new String(FileUtil.getBytes(f), cs);
	}

	public static void main(String[] args) {
		System.out.println("Advert-\n  ising and other han- \ndy things, but -\n and this"
				.replaceAll("(\\w+)-[ ]*\\n[ ]*(\\w+)", "$1$2"));
		
		String test1 = "<!-- foo -->and bar<!--comment";
		String test2 = "and bar <!-- comment --><!--";
		String test3 = "and <!-- other -->bar<!--- -->";
		try {
			System.out.println("[" + Converter.removeXmlComments(test1) + "]");
			System.out.println("[" + Converter.removeXmlComments(test2) + "]");
			System.out.println("[" + Converter.removeXmlComments(test3) + "]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}