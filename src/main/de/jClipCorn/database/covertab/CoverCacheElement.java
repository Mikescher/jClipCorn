package de.jClipCorn.database.covertab;

import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.EnumFormatException;

public class CoverCacheElement {

	public final int ID;
	public final String Filename;
	public final int Width;
	public final int Height;
	public final long Filesize;
	public ColorQuantizerMethod PreviewType;

	public String Checksum;
	public byte[] Preview;
	public CCDateTime Timestamp;

	public CoverCacheElement(int id, String fn, int ww, int hh, String cs, long fs, byte[] pv, ColorQuantizerMethod pt, CCDateTime ts) {
		this.ID          = id;
		this.Filename    = fn;
		this.Width       = ww;
		this.Height      = hh;
		this.Filesize    = fs;
		this.Checksum    = cs;
		this.Preview     = pv;
		this.PreviewType = pt;
		this.Timestamp   = ts;
	}

	public CoverCacheElement(int id, String fn, int ww, int hh, String cs, long fs, byte[] pv, int pt, CCDateTime ts) throws EnumFormatException {
		this.ID          = id;
		this.Filename    = fn;
		this.Width       = ww;
		this.Height      = hh;
		this.Filesize    = fs;
		this.Checksum    = cs;
		this.Preview     = pv;
		this.PreviewType = ColorQuantizerMethod.getWrapper().findOrException(pt);
		this.Timestamp   = ts;
	}

	public CoverCacheElement(int id, String fn, int ww, int hh, long fs, ColorQuantizerMethod pt) {
		this.ID          = id;
		this.Filename    = fn;
		this.Width       = ww;
		this.Height      = hh;
		this.Filesize    = fs;
		this.PreviewType = pt;

		this.Checksum  = null;
		this.Preview   = null;
		this.Timestamp = null;
	}

	public CoverCacheElement(int id, String fn, int ww, int hh, long fs, int pt) throws EnumFormatException {
		this.ID          = id;
		this.Filename    = fn;
		this.Width       = ww;
		this.Height      = hh;
		this.Filesize    = fs;
		this.PreviewType = ColorQuantizerMethod.getWrapper().findOrException(pt);

		this.Checksum  = null;
		this.Preview   = null;
		this.Timestamp = null;
	}
}
