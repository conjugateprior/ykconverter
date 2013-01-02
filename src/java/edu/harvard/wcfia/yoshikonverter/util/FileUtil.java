package edu.harvard.wcfia.yoshikonverter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class FileUtil {

	public static byte[] getBytes(File f) throws IOException {
		FileChannel in = null;
		try {
			in = new FileInputStream(f).getChannel();
			long size = in.size();
			if (size > Integer.MAX_VALUE) {
				throw new IOException("File : " + f
						+ " is too large for processing"
				);
			}
			MappedByteBuffer buf = 
				in.map(FileChannel.MapMode.READ_ONLY, 0, size);
			byte[] bytes = new byte[(int) size];

			buf.get(bytes);
			return bytes;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	public static byte[] stripBOM(byte[] bytesInFile){
		int read = 0; // how many to skip
		if ( (bytesInFile[0] == (byte)0x00) && (bytesInFile[1] == (byte)0x00) &&
				(bytesInFile[2] == (byte)0xFE) && (bytesInFile[3] == (byte)0xFF) ) {
			//encoding = "UTF-32BE";
			read = 4;
		} else if ( (bytesInFile[0] == (byte)0xFF) && (bytesInFile[1] == (byte)0xFE) &&
				(bytesInFile[2] == (byte)0x00) && (bytesInFile[3] == (byte)0x00) ) {
			//encoding = "UTF-32LE";
			read = 4;
		} else if (  (bytesInFile[0] == (byte)0xEF) && (bytesInFile[1] == (byte)0xBB) &&
				(bytesInFile[2] == (byte)0xBF) ) {
			//encoding = "UTF-8";
			read = 3;
		} else if ( (bytesInFile[0] == (byte)0xFE) && (bytesInFile[1] == (byte)0xFF) ) {
			//encoding = "UTF-16BE";
			read = 2;
		} else if ( (bytesInFile[0] == (byte)0xFF) && (bytesInFile[1] == (byte)0xFE) ) {
			//encoding = "UTF-16LE";
			read = 2;
		} 
		if (read == 0)
			return bytesInFile;
		
		byte[] newb = new byte[bytesInFile.length-read];
		for (int ii = read; ii < bytesInFile.length; ii++) {
			newb[ii-read] = bytesInFile[ii];
		}
		return newb;
	}
	
	public static String getTextFromFile(File f, String encodingName) throws IOException {
		byte[] b = getBytes(f);
		String txt = new String(stripBOM(b), encodingName);
		return txt;
	}
	
	public static void putTextInFile(String txt, File f, String encodingName) throws IOException {
		FileOutputStream out = new FileOutputStream(f);
		byte[] b = txt.getBytes(Charset.forName(encodingName));
		out.write(b);
		out.close();
	}
	
}
