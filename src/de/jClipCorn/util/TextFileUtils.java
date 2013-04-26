package de.jClipCorn.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TextFileUtils {	
	public static String readTextFile(String filename) throws IOException {
		return readTextFile(new File(filename));
	}
	
	@SuppressWarnings("resource")
	public static String readTextFile(File file) throws IOException {
		return readTextFile(new FileReader(file));
	}
	
	public static String readTextFile(FileReader reader) throws IOException {
		return readTextFile(new BufferedReader(reader));
	}
	
	public static String readTextFile(BufferedReader reader) throws IOException {
		StringBuffer content = new StringBuffer();
		boolean first = true;

		try {
			String s = null;

			while ((s = reader.readLine()) != null) {
				if (!first) {
					content.append(System.getProperty("line.separator")); //$NON-NLS-1$
				}
				content.append(s);
				first = false;
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return content.toString();
	}

	public static String readTextResource(String resourcename, Class<?> c) throws IOException {
		BufferedReader reader = null;
		StringBuffer content = new StringBuffer();
		
		try {
			InputStream is = c.getResourceAsStream(resourcename);
			if (is == null) throw new IOException();
			reader = new BufferedReader(new InputStreamReader(is));
			String s = null;
			boolean first = true;

			while ((s = reader.readLine()) != null) {
				if (!first) {
					content.append(System.getProperty("line.separator")); //$NON-NLS-1$
				}
				content.append(s);
				first = false;
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return content.toString();
	}
	
	public static void writeTextFile(String filename, String text) throws IOException {
		writeTextFile(new File(filename), text);
	}
	
	public static void writeTextFile(File file, String text) throws IOException {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		
		try {
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, "UTF8"); //$NON-NLS-1$
			bw = new BufferedWriter(osw);
		
			bw.write(text);
		
			bw.close();
		} finally {
			if (bw != null) bw.close();
		}
	}
}