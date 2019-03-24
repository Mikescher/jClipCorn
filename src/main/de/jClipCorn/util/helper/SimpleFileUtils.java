package de.jClipCorn.util.helper;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.lambda.Func1to0;
import de.jClipCorn.util.lambda.Func2to0;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
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
		StringBuilder content = new StringBuilder();
		boolean first = true;

		try {
			String s;

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
		StringBuilder content = new StringBuilder();
		
		try {
			InputStream is = c.getResourceAsStream(resourcename);
			if (is == null) throw new IOException();
			reader = new BufferedReader(new InputStreamReader(is, Str.UTF8));
			String s;
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

		try (InputStream is = c.getResourceAsStream(resourcename)) {
			if (is == null) throw new IOException();
			Files.copy(is, out.toPath());
		}
	}
	
	public static void writeTextFile(String filename, String text) throws IOException {
		writeTextFile(new File(filename), text);
	}
	
	public static void writeTextFile(File file, String text) throws IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw = null;
		
		try {
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, Str.UTF8); //$NON-NLS-1$
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
	public static String getTempFilename(String ext) {
		String fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), ext);

		return new File(fileName).getAbsolutePath();
	}

	@SuppressWarnings("nls")
	public static String getSystemTempFile(String ext) {
		String fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), ext);

		return PathFormatter.combine(System.getProperty("java.io.tmpdir"), fileName);
	}

	public static void copyWithProgress(File src, File dst, Func2to0<Long, Long> feedback) throws IOException
	{
		FileInputStream fis  = new FileInputStream(src);
		FileOutputStream fos = new FileOutputStream(dst);

		long fullsize = src.length();

		byte[] buf = new byte[1024*1024*32];
		int size = 0;
		long flag = 0;
		while ((size = fis.read(buf)) != -1)
		{
			fos.write(buf, 0, size);
			flag += size;
			feedback.invoke(flag, fullsize);
		}

		fis.close();
		fos.close();
	}
}