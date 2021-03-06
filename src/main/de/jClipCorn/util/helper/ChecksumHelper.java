package de.jClipCorn.util.helper;

import com.twmacinta.util.MD5;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

public class ChecksumHelper
{
	private final static String FASTMD5_ERROR_RESULT = "0"; //$NON-NLS-1$
	private final static int    FASTMD5_BLOCKSIZE = 1 * 1024 * 1024; // 1MB

	private static final Pattern REX_FVH_PART3 = Pattern.compile("^(?:[0-9A-F]{2}:){7}[0-9A-F]{2}$"); //$NON-NLS-1$

	private static final int FASTVIDEOHASH_VERSION              = 1;
	private static final int FASTVIDEOHASH_READ_BLOCKSIZE       = 32*1024;
	private static final int FASTVIDEOHASH_BLOCKCOUNT           = 8;
	private static final int FASTVIDEOHASH_READ_COUNT_PER_BLOCK = 4;
	private static final int FASTVIDEOHASH_TOTALREAD_COUNT      = FASTVIDEOHASH_BLOCKCOUNT * FASTVIDEOHASH_READ_COUNT_PER_BLOCK;
	private static final int FASTVIDEOHASH_DIGITS_PER_BLOCK     = 2;

	public static String calculateFastMD5(FSPath[] f) {
		MD5 md5 = new MD5();

		for (int i = 0; i < f.length; i++) {
			if (! calculateLargeMD5(md5, f[i])) {
				CCLog.addError(LocaleBundle.getString("LogMessage.CouldNotCalcFileMD5")); //$NON-NLS-1$
				return FASTMD5_ERROR_RESULT;
			}
		}

		return md5.asHex().toUpperCase();
	}

	private static boolean calculateLargeMD5(MD5 md5, FSPath f) {
		byte[] block = new byte[FASTMD5_BLOCKSIZE];

		if (f.isEmpty() || !f.exists() || f.filesize().getBytes() < FASTMD5_BLOCKSIZE * 3) {
			return false;
		}

		long len = f.filesize().getBytes();

		try {
			FileInputStream i = new FileInputStream(f.toFile());
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

	//public static String fastVideoHash(File[] files) throws IOException
	//{
	//	var raws = new ArrayList<Tuple<String, String[]>>();
	//	for (File f : files) raws.add(fastVideoHashRaw(f));
	//
	//	StringBuilder result = new StringBuilder();
	//	result.append('[');
	//	result.append(StringUtils.leftPad(Integer.toString(FASTVIDEOHASH_VERSION), 2, '0'));
	//	result.append('-');
	//
	//	for (int i = 0; i < raws.size(); i++)
	//	{
	//		if (i>0) result.append('|');
	//		result.append(raws.get(i).Item1);
	//	}
	//
	//	result.append('-');
	//
	//	for (int i = 0; i < FASTVIDEOHASH_BLOCKCOUNT; i++)
	//	{
	//		if (i>0) result.append(':');
	//		for (int j = 0; j < raws.size(); j++)
	//		{
	//			if (j>0) result.append('|');
	//			result.append(raws.get(j).Item2[i]);
	//		}
	//	}
	//
	//	return result.toString();
	//}

	public static String fastVideoHash(FSPath f) throws IOException
	{
		var raw = fastVideoHashRaw(f);

		StringBuilder result = new StringBuilder();

		result.append('[');
		result.append(StringUtils.leftPad(Integer.toString(FASTVIDEOHASH_VERSION), 2, '0'));
		result.append('-').append(raw.Item1);
		for (int i = 0; i < raw.Item2.length; i++)
		{
			result.append((i==0) ? '-' : ':');
			result.append(raw.Item2[i]);
		}
		result.append(']');

		return result.toString();
	}

	@SuppressWarnings("nls")
	private static Tuple<String, String[]> fastVideoHashRaw(FSPath f) throws IOException
	{
		var len = f.filesize().getBytes(); // in bytes

		var hexlen = StringUtils.leftPad(Long.toHexString(len).toUpperCase(), 10, "0");
		var blocks = new String[FASTVIDEOHASH_BLOCKCOUNT];

		//if (len < READ_BLOCKSIZE)
		//{
		//	result.append("-").append("X".repeat(DIGITS_PER_BLOCK)).append((":"+"X".repeat(DIGITS_PER_BLOCK)).repeat(BLOCKCOUNT-1)).append(']');
		//	return result.toString();
		//}

		var buffer = new byte[FASTVIDEOHASH_READ_BLOCKSIZE];
		try(RandomAccessFile file = new RandomAccessFile(f.toFile(), "r"))
		{
			int readidx = 0;
			for (int i1 = 0; i1 < FASTVIDEOHASH_BLOCKCOUNT; i1++)
			{
				StringBuilder fullhash = new StringBuilder();
				for (int i2 = 0; i2 < FASTVIDEOHASH_READ_COUNT_PER_BLOCK; i2++)
				{
					if (len >= FASTVIDEOHASH_READ_BLOCKSIZE)
					{
						var pos = (long)Math.floor((len - FASTVIDEOHASH_READ_BLOCKSIZE) * (readidx*1d / (FASTVIDEOHASH_TOTALREAD_COUNT-1)));
						file.seek(pos);
					}
					else
					{
						file.seek(0);
					}

					file.read(buffer);
					fullhash.append(DigestUtils.sha256Hex(buffer));
					readidx++;
				}

				blocks[i1] = DigestUtils.sha256Hex(fullhash.toString()).substring(0, FASTVIDEOHASH_DIGITS_PER_BLOCK).toUpperCase();
			}
		}

		return Tuple.Create(hexlen, blocks);
	}

	public static boolean isValidVideoHash(String ccfvh)
	{
		if (ccfvh == null || ccfvh.length() < 3) return false;

		if (ccfvh.charAt(0) != '[') return false;
		if (ccfvh.charAt(ccfvh.length()-1) != ']') return false;

		var split = ccfvh.substring(1, ccfvh.length()-1).split("-");
		if (split.length == 0) return false;

		var version = Str.tryParseInt(split[0]);
		if (!version.isPresent()) return false;

		if (version.get() == 1)
		{
			if (!Str.equals(split[0], "01")) return false;
			if (split.length != 3) return false;

			if (split[2].length() != 23) return false;
			if (!RegExHelper.isPatternMatch(REX_FVH_PART3, split[2])) return false;

			return true;
		}
		else
		{
			return false;
		}
	}

	public static boolean isPossibleVideoHash(String ccfvh, CCMediaInfo mediainfo)
	{
		var split = ccfvh.substring(1, ccfvh.length()-2).split("-");

		var version = Str.tryParseInt(split[0]);

		if (version.get() == 1)
		{
			// filesize should match
			if (!Str.equals(split[1], StringUtils.leftPad(Long.toHexString(mediainfo.getFilesize().getBytes()).toUpperCase(), 10, "0"))) return false;

			return true;
		}
		else
		{
			throw new Error("Unknown CCFVH version: " + version.get());
		}
	}
}
