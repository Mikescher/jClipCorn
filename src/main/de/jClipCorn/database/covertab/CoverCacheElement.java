package de.jClipCorn.database.covertab;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.EnumFormatException;

public class CoverCacheElement {

	public final int ID;
	public final String Filename;
	public final int Width;
	public final int Height;
	public final long Filesize;
	public final ColorQuantizerMethod PreviewType;
	public final String Checksum;
	public final CCDateTime Timestamp;

	private byte[] Preview; // can be null

	public CoverCacheElement(int id, String fn, int ww, int hh, String cs, long fs, byte[] pv, ColorQuantizerMethod pt, CCDateTime ts) {
		this.ID          = id;
		this.Filename    = fn;
		this.Width       = ww;
		this.Height      = hh;
		this.Filesize    = fs;
		this.Checksum    = cs;
		this.PreviewType = pt;
		this.Timestamp   = ts;

		this.Preview     = pv;
	}

	public CoverCacheElement(int id, String fn, int ww, int hh, String cs, long fs, byte[] pv, int pt, CCDateTime ts) throws EnumFormatException {
		this.ID          = id;
		this.Filename    = fn;
		this.Width       = ww;
		this.Height      = hh;
		this.Filesize    = fs;
		this.Checksum    = cs;
		this.PreviewType = ColorQuantizerMethod.getWrapper().findOrException(pt);
		this.Timestamp   = ts;

		this.Preview     = pv;
	}

	public CoverCacheElement(int id, String fn, int ww, int hh, String cs, long fs, ColorQuantizerMethod pt, CCDateTime ts) {
		this.ID          = id;
		this.Filename    = fn;
		this.Width       = ww;
		this.Height      = hh;
		this.Filesize    = fs;
		this.PreviewType = pt;
		this.Checksum    = cs;
		this.Timestamp   = ts;

		this.Preview     = null;
	}

	public CoverCacheElement(int id, String fn, int ww, int hh, String cs, long fs, int pt, CCDateTime ts) throws EnumFormatException {
		this.ID          = id;
		this.Filename    = fn;
		this.Width       = ww;
		this.Height      = hh;
		this.Filesize    = fs;
		this.PreviewType = ColorQuantizerMethod.getWrapper().findOrException(pt);
		this.Checksum    = cs;
		this.Timestamp   = ts;

		this.Preview   = null;
	}

	public byte[] getPreviewOrNull() {
		return Preview;
	}

	public byte[] getPreview(CCDatabase db) {
		if (Preview != null) return Preview;

		Preview = db.getPreviewForCover(this);

		return Preview;
	}
}
