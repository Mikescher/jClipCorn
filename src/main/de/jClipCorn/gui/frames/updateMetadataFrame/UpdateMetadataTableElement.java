package de.jClipCorn.gui.frames.updateMetadataFrame;

import javax.swing.JFrame;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.features.online.metadata.OnlineMetadata;

public class UpdateMetadataTableElement {

	public final CCDatabaseElement Element;
	
	public OnlineMetadata OnlineMeta = null;

	public boolean Processed = false;
	
	public UpdateMetadataTableElement(CCDatabaseElement el) {
		super();
		Element = el;
	}

	public void preview(JFrame owner) {
		if (Element instanceof CCMovie) 
			new PreviewMovieFrame(owner, (CCMovie)Element).setVisible(true);
		
		if (Element instanceof CCSeries) 
			new PreviewSeriesFrame(owner, (CCSeries)Element).setVisible(true);
	}
}
