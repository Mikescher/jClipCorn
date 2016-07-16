package de.jClipCorn.util.helper;

public class ByteUtilies {
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String byteArrayToHexString(byte[] b) {
		int len = b.length;
		StringBuilder data = new StringBuilder();

		for (int i = 0; i < len; i++) {
			data.append(Integer.toHexString((b[i] >> 4) & 0xF));
			data.append(Integer.toHexString(b[i] & 0xF));
		}
		
		return data.toString().toUpperCase();
	}

}
