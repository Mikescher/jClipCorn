package de.jClipCorn.features.serialization.xmlimport;

public class ImportOptions {
	public final boolean ResetAddDate;
	public final boolean ResetViewed;
	public final boolean ResetScore;
	public final boolean ResetTags;

	public final boolean IgnoreCoverData;

	public ImportOptions(boolean resetAddDate, boolean resetViewed, boolean resetScore, boolean resetTags, boolean ignoreCoverData) {
		ResetAddDate = resetAddDate;
		ResetViewed = resetViewed;
		ResetScore = resetScore;
		ResetTags = resetTags;
		IgnoreCoverData = ignoreCoverData;
	}
}
