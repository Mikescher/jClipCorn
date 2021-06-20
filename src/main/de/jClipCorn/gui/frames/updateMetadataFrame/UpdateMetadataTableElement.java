package de.jClipCorn.gui.frames.updateMetadataFrame;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;

import javax.swing.*;

public class UpdateMetadataTableElement {

	public final CCDatabaseElement Element;
	
	public OnlineMetadata OnlineMeta = null;

	public boolean Processed = false;
	
	public UpdateMetadataTableElement(CCDatabaseElement el) {
		super();
		Element = el;
	}

	public void preview(JFrame owner) {
		if (Element instanceof CCMovie)  PreviewMovieFrame.show(owner,  (CCMovie)  Element, true);
		if (Element instanceof CCSeries) PreviewSeriesFrame.show(owner, (CCSeries) Element, true);
	}
}
