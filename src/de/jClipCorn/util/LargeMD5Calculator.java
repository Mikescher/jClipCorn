package de.jClipCorn.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.twmacinta.util.MD5;

import de.jClipCorn.gui.log.CCLog;

public class LargeMD5Calculator {
	private final static int BLOCKSIZE = 1 * 1024 * 1024; // 1MB
	private static byte[] block = new byte[BLOCKSIZE];
	
	public static String getMD5(String[] s) {
		File[] f = new File[s.length];
		for (int i = 0; i < s.length; i++) {
			f[i] = new File(s[i]);
		}
		
		return getMD5(f);
	}
	
	public static String getMD5(File[] f) {
		MD5 md5 = new MD5();
		
		for (int i = 0; i < f.length; i++) {
			if (! calcMD5(md5, f[i])) {
				return null;
			}
		}
		
		return md5.asHex().toUpperCase();
	}
	
	private static boolean calcMD5(MD5 md5, File f) {
		if (! f.exists() || f.length() < BLOCKSIZE * 3) {
			return false;
		}
		
		long len = f.length();
		
        try {
        	FileInputStream i = new FileInputStream(f);
        	i.skip(len / 2);

        	i.read(block);
			md5.Update(block);
			
			i.close();
		} catch (IOException e) {
			CCLog.addError(e);
			return false;
		}
		
		return true;
	}
	
	public static String calcMD5(BufferedImage f) {
		MD5 md5 = new MD5();
		
		md5.Update(((DataBufferByte)f.getData().getDataBuffer()).getData());
		
		return md5.asHex().toUpperCase();
	}
}
