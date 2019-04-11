package de.jClipCorn.features.serialization.xmlimport;

import de.jClipCorn.util.xml.CCXMLParser;

public class ImportState {
	public final CCXMLParser Document;
	public final int XMLVersion;

	public final boolean ResetAddDate;
	public final boolean ResetViewed;
	public final boolean ResetScore;
	public final boolean ResetTags;

	public final boolean IgnoreCoverData;

	public ImportState(CCXMLParser doc, int xmlVersion, ImportOptions opt) {
		Document        = doc;
		XMLVersion      = xmlVersion;
		ResetAddDate    = opt.ResetAddDate;
		ResetViewed     = opt.ResetViewed;
		ResetScore      = opt.ResetScore;
		ResetTags       = opt.ResetTags;
		IgnoreCoverData = opt.IgnoreCoverData;
	}
}
