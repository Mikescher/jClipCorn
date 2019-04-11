package de.jClipCorn.features.serialization.xmlexport;

public class ExportState {
	public final boolean FileHash;
	public final boolean CoverHash;
	public final boolean CoverData;

	public ExportState(boolean fileHash, boolean coverHash, boolean coverData) {
		this.FileHash = fileHash;
		this.CoverHash = coverHash;
		this.CoverData = coverData;
	}
}
