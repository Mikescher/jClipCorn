package de.jClipCorn.database.covertab;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.EnumFormatException;

public class CCCoverData {

	public final int ID;
	public final String Filename;
	public final int Width;
	public final int Height;
	public final CCFileSize Filesize;
	public final ColorQuantizerMethod PreviewType;
	public final String Checksum;
	public final CCDateTime Timestamp;

	private byte[] Preview; // can be null

	public CCCoverData(int id, String fn, int ww, int hh, String cs, CCFileSize fs, byte[] pv, ColorQuantizerMethod pt, CCDateTime ts) {
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

	public CCCoverData(int id, String fn, int ww, int hh, String cs, CCFileSize fs, byte[] pv, int pt, CCDateTime ts) throws EnumFormatException {
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

	public CCCoverData(int id, String fn, int ww, int hh, String cs, CCFileSize fs, ColorQuantizerMethod pt, CCDateTime ts) {
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

	public CCCoverData(int id, String fn, int ww, int hh, String cs, CCFileSize fs, int pt, CCDateTime ts) throws EnumFormatException {
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
