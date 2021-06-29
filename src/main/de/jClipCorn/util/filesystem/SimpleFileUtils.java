package de.jClipCorn.util.filesystem;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.lambda.Func2to0;

import java.io.*;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.UUID;

public class SimpleFileUtils {
	public final static String LINE_END = System.getProperty("line.separator"); //$NON-NLS-1$

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

	public static void writeRawResource(File out, String resourcename, Class<?> c) throws IOException {
		try (InputStream is = c.getResourceAsStream(resourcename)) {
			if (is == null) throw new IOException();
			Files.copy(is, out.toPath());
		}
	}

	public static void writeTextResource(File out, String resourcename, Class<?> c) throws IOException {
		try (InputStream is = c.getResourceAsStream(resourcename)) {
			if (is == null) throw new IOException();
			Files.copy(is, out.toPath());
		}
	}

	public static String readTextFromReader(BufferedReader reader) throws IOException {
		StringBuilder content = new StringBuilder();
		boolean first = true;

		String s;
		while ((s = reader.readLine()) != null)
		{
			if (!first) content.append(LINE_END);
			content.append(s);
			first = false;
		}

		return content.toString();
	}
	public static FSPath getSystemTempFile(String ext) {
		return FilesystemUtils.getTempPath().append(MessageFormat.format("{0}.{1}", UUID.randomUUID(), ext));
	}

	public static String[] splitLines(String text) {
		return text.split("\\r?\\n"); //$NON-NLS-1$
	}

	public static void copyWithProgress(FSPath src, FSPath dst, Func2to0<Long, Long> feedback) throws IOException
	{
		long fullsize = src.toFile().length();

		if (src.equalsOnFilesystem(dst))
		{
			feedback.invoke(0L, fullsize);
			feedback.invoke(fullsize, fullsize);
			return;
		}

		FileInputStream fis  = new FileInputStream(src.toFile());
		FileOutputStream fos = new FileOutputStream(dst.toFile());

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