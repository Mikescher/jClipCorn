package de.jClipCorn.util.helper;

import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.exceptions.FVHException;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

public class ChecksumHelper
{
	private static final Pattern REX_FVH_DATAPART = Pattern.compile("^(?:[0-9A-F]{2}:){7}[0-9A-F]{2}$"); //$NON-NLS-1$

	private static final int FASTVIDEOHASH_VERSION              = 2;
	private static final int FASTVIDEOHASH_READ_BLOCKSIZE       = 32*1024;
	private static final int FASTVIDEOHASH_BLOCKCOUNT           = 8;
	private static final int FASTVIDEOHASH_READ_COUNT_PER_BLOCK = 4;
	private static final int FASTVIDEOHASH_TOTALREAD_COUNT      = FASTVIDEOHASH_BLOCKCOUNT * FASTVIDEOHASH_READ_COUNT_PER_BLOCK;
	private static final int FASTVIDEOHASH_DIGITS_PER_BLOCK     = 2;

	public static String fastVideoHash(List<FSPath> files) throws IOException, FVHException
	{
		if (files.size() > 9) throw new FVHException("FVH version 2 can hash max 9 files");

		var raws = new ArrayList<Tuple<Long, String[]>>();
		for (var f: files) raws.add(fastVideoHashRaw(f));

		var total_fs = CCStreams.iterate(raws).sumLong(p -> p.Item1);
		var fs_hex = StringUtils.leftPad(Long.toHexString(total_fs).toUpperCase(), 10, "0");

		StringBuilder result = new StringBuilder();
		result.append('[');
		result.append(StringUtils.leftPad(Integer.toString(FASTVIDEOHASH_VERSION), 2, '0'));
		result.append('-').append(fs_hex);
		result.append('-').append(files.size());
		for (var raw: raws)
		{
			result.append('-');
			for (int i = 0; i < raw.Item2.length; i++)
			{
				if (i > 0) result.append(':');
				result.append(raw.Item2[i]);
			}
		}
		result.append(']');

		return result.toString();
	}

	public static String fastVideoHash(FSPath f) throws IOException, FVHException
	{
		return fastVideoHash(Collections.singletonList(f));
	}

	@SuppressWarnings("nls")
	private static Tuple<Long, String[]> fastVideoHashRaw(FSPath f) throws IOException
	{
		var len = f.filesize().getBytes(); // in bytes

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

		return Tuple.Create(len, blocks);
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
			if (!RegExHelper.isPatternMatch(REX_FVH_DATAPART, split[2])) return false;

			return true;
		}
		else if (version.get() == 2)
		{
			if (!Str.equals(split[0], "02")) return false;

			if (!Str.isInteger(split[2])) return false;

			int fcount = Integer.parseInt(split[2]);

			if (split.length != 3 + fcount) return false;

			for (int i = 0; i < fcount; i++) {
				if (split[3+i].length() != 23) return false;
				if (!RegExHelper.isPatternMatch(REX_FVH_DATAPART, split[3+i])) return false;
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	public static class ChecksumResult
	{
		public final String CRC32;
		public final String MD5;
		public final String SHA256;
		public final String SHA512;

		public ChecksumResult(String crc32, String md5, String sha256, String sha512)
		{
			this.CRC32  = crc32;
			this.MD5    = md5;
			this.SHA256 = sha256;
			this.SHA512 = sha512;
		}
	}

	@SuppressWarnings("nls")
	public static ChecksumResult computeAllChecksums(FSPath file) throws IOException
	{
		try
		{
			var crc32  = new CRC32();
			var md5    = MessageDigest.getInstance("MD5");
			var sha256 = MessageDigest.getInstance("SHA-256");
			var sha512 = MessageDigest.getInstance("SHA-512");

			byte[] buffer = new byte[64 * 1024];

			try (InputStream fis = new BufferedInputStream(new FileInputStream(file.toFile())))
			{
				int bytesRead;
				while ((bytesRead = fis.read(buffer)) != -1)
				{
					crc32.update(buffer, 0, bytesRead);
					md5.update(buffer, 0, bytesRead);
					sha256.update(buffer, 0, bytesRead);
					sha512.update(buffer, 0, bytesRead);
				}
			}

			return new ChecksumResult(
				String.format("%08X", crc32.getValue()),
				bytesToHex(md5.digest()),
				bytesToHex(sha256.digest()),
				bytesToHex(sha512.digest())
			);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IOException("Hash algorithm not available", e);
		}
	}

	private static String bytesToHex(byte[] bytes)
	{
		var sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) sb.append(String.format("%02X", b)); //$NON-NLS-1$
		return sb.toString();
	}

	public static boolean isPossibleVideoHash(String ccfvh, CCMediaInfo mediainfo)
	{
		var split = ccfvh.substring(1, ccfvh.length()-2).split("-");

		var version = Str.tryParseInt(split[0]);

		if (version.get() == 1)
		{
			if (mediainfo.Filesize.isEmpty()) return false;

			// filesize should match
			if (!Str.equals(split[1], StringUtils.leftPad(Long.toHexString(mediainfo.Filesize.get().getBytes()).toUpperCase(), 10, "0"))) return false;

			return true;
		}
		else if (version.get() == 2)
		{
			// filesize should match
			if (!Str.equals(split[1], StringUtils.leftPad(Long.toHexString(mediainfo.Filesize.get().getBytes()).toUpperCase(), 10, "0"))) return false;

			return true;
		}
		else
		{
			throw new Error("Unknown CCFVH version: " + version.get());
		}
	}
}
