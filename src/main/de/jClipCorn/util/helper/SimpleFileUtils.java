package de.jClipCorn.util.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.UUID;

public class SimpleFileUtils {	
	public final static Charset CHARSET_UTF8 = Charset.forName("UTF-8"); //$NON-NLS-1$
	
	public final static String LINE_END = System.getProperty("line.separator"); //$NON-NLS-1$
	
	public static String readTextFile(InputStreamReader reader) throws IOException {
		return readTextFile(new BufferedReader(reader));
	}
	
	public static String readUTF8TextFile(String file) throws IOException {
		return readUTF8TextFile(new File(file));
	}

	public static String readUTF8TextFile(File file) throws IOException {
		FileInputStream stream;
		String result = readUTF8TextFile(stream = new FileInputStream(file));
		stream.close();
		return result;
	}
	
	public static String readUTF8TextFile(FileInputStream file) throws IOException {
		return readTextFile(new InputStreamReader(file, CHARSET_UTF8));
	}
	
	public static String readTextFile(BufferedReader reader) throws IOException {
		StringBuffer content = new StringBuffer();
		boolean first = true;

		try {
			String s = null;

			while ((s = reader.readLine()) != null) {
				if (!first) {
					content.append(LINE_END);
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
					content.append(LINE_END);
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

	public static void writeTextResource(File out, String resourcename, Class<?> c) throws IOException {
		InputStream is = null;
		
		try {
			is = c.getResourceAsStream(resourcename);
			if (is == null) throw new IOException();
			
			Files.copy(is, out.toPath());
		} finally {
			if (is != null) is.close();
		}
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

	public static String[] splitLines(String text) {
		return text.split("\\r?\\n"); //$NON-NLS-1$
	}

	@SuppressWarnings("nls")
	public static String getTempFile(String ext) {
		String fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), ext);
		
		return new File(fileName).getAbsolutePath();
	}
}