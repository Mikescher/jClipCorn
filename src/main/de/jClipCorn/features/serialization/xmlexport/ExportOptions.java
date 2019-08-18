package de.jClipCorn.features.serialization.xmlexport;

public class ExportOptions {
	public final boolean FileHash;
	public final boolean CoverHash;
	public final boolean CoverData;
	public final boolean ExportLocalID;

	public ExportOptions(boolean fileHash, boolean coverHash, boolean coverData, boolean localid) {
		this.FileHash      = fileHash;
		this.CoverHash     = coverHash;
		this.CoverData     = coverData;
		this.ExportLocalID = localid;
	}
}
