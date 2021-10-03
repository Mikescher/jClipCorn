package de.jClipCorn.gui.frames.updateMetadataFrame;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
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
		if (Element.isMovie())  PreviewMovieFrame.show(owner,  Element.asMovie(),  true);
		if (Element.isSeries()) PreviewSeriesFrame.show(owner, Element.asSeries(), true);
	}
}
