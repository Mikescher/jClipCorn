package de.jClipCorn.gui.frames.updateMetadataFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateMetadataTable extends JCCSimpleTable<UpdateMetadataTableElement> {
	private static final long serialVersionUID = 3308858204018846266L;
	
	private final UpdateMetadataFrame owner;

	public boolean DeleteLocalGenres;
	public boolean DeleteLocalReferences;

	@DesignCreate
	private static UpdateMetadataTable designCreate() { return new UpdateMetadataTable(new UpdateMetadataFrame(ICCWindow.Dummy.frame(), CCMovieList.createStub())); }

	public UpdateMetadataTable(UpdateMetadataFrame owner) {
		super(owner);
		this.owner = owner;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<UpdateMetadataTableElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<UpdateMetadataTableElement>> r = new ArrayList<>();
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"",
				null,
				e -> e.Element.getOnlineReference().Main.getIcon16x16(),
				null,
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"*,min=auto",
				"UpdateMetadataFrame.Table.ColumnTitle",
				e -> e.Element.getFullDisplayTitle(),
				null,
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateMetadataFrame.Table.ColumnChangedScore",
				this::getScoreChange,
				null,
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateMetadataFrame.Table.ColumnChangedGenres",
				this::getGenreChange,
				null,
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateMetadataFrame.Table.ColumnChangedReferences",
				this::getReferencesChange,
				null,
				null,
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateMetadataFrame.Table.ColumnLocalScore",
				null,
				e -> e.Element.getOnlinescore().getIcon(),
				e -> e.Element.getOnlinescore().asInt() + "/10",
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateMetadataFrame.Table.ColumnOnlineScore",
				null,
				e -> (e.OnlineMeta != null && e.OnlineMeta.getOnlineScore() != null) ? (e.OnlineMeta.getOnlineScore().getIcon()) : (null),
				e -> (e.OnlineMeta != null && e.OnlineMeta.getOnlineScore() != null) ? (e.OnlineMeta.getOnlineScore().asInt() + "/10") : (null),
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateMetadataFrame.Table.ColumnLocalGenres",
				e -> e.Element.getGenres().asSortedString(),
				null,
				null,
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateMetadataFrame.Table.ColumnOnlineGenres",
				e -> (e.OnlineMeta != null && e.OnlineMeta.Genres != null) ? e.OnlineMeta.Genres.asSortedString() : (null),
				null,
				null,
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateMetadataFrame.Table.ColumnLocalReferences",
				e -> e.Element.getOnlineReference().toSourceConcatString(),
				null,
				null,
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateMetadataFrame.Table.ColumnOnlineReferences",
				e -> (e.OnlineMeta != null) ? e.OnlineMeta.getFullReference().toSourceConcatString() : (null),
				null,
				null,
				true));
		
		return r;
	}

	@SuppressWarnings("nls")
	private String getScoreChange(UpdateMetadataTableElement e) {
		if (e.OnlineMeta == null) return "";
		if (e.OnlineMeta.getOnlineScore() == null) return "ERR";
		
		int o = e.OnlineMeta.getOnlineScore().asInt();
		int l = e.Element.getOnlinescore().asInt();
		
		if (o > l) return "+" + ((o-l)/2f);
		if (o < l) return "-" + ((l-o)/2f);
		
		return "-";
	}

	@SuppressWarnings("nls")
	private String getGenreChange(UpdateMetadataTableElement e) {
		if (e.OnlineMeta == null) return "";
		if (e.OnlineMeta.Genres == null) return "ERR";

		if (DeleteLocalGenres) {
			int gnew = 0;
			int gdel = 0;
			
			for (CCGenre online : e.OnlineMeta.Genres.iterate()) {
				if (!e.Element.getGenres().includes(online)) gnew++;
			}
			for (CCGenre local : e.Element.getGenres().iterate()) {
				if (!e.OnlineMeta.Genres.includes(local)) gdel++;
			}
			
			if (gnew == 0 && gdel == 0) return "-";
			
			return "+" + gnew + " " + "-" + gdel;
		} else {
			int gnew = 0;
			
			for (CCGenre online : e.OnlineMeta.Genres.iterate()) {
				if (!e.Element.getGenres().includes(online)) gnew++;
			}
			
			gnew = Math.min(gnew, CCGenreList.getMaxListSize() - e.Element.getGenres().getGenreCount());
			
			if (gnew == 0) return "-";
			
			return "+" + gnew;
		}
	}

	@SuppressWarnings("nls")
	private String getReferencesChange(UpdateMetadataTableElement e) {
		if (e.OnlineMeta == null) return "";

		CCOnlineReferenceList onlineref = e.OnlineMeta.getFullReference();
		CCOnlineReferenceList localref  = e.Element.getOnlineReference();
		
		if (onlineref == null || onlineref.Main.isUnset()) return "ERR";
	
		if (DeleteLocalReferences) {
			int gnew = 0;
			int gdel = 0;

			if (!onlineref.Main.equals(localref.Main)) {
				gnew++;
				if (localref.Main.isSet()) gdel++;
			}
			
			for (CCSingleOnlineReference r : onlineref.Additional) if (!localref.Additional.contains(r)) gnew++; // online but not local

			for (CCSingleOnlineReference r : localref.Additional) if (!onlineref.Additional.contains(r)) gdel++; // local but not online
			
			if (gnew == 0 && gdel == 0) return "-";
			
			return "+" + gnew + " " + "-" + gdel;
		} else {
			int gnew = 0;
			
			for (CCSingleOnlineReference r : onlineref) if (!CCStreams.iterate(localref).contains(r)) gnew++;
			
			if (gnew == 0) return "-";
			
			return "+" + gnew;
		}
	}

	@Override
	protected void OnDoubleClickElement(UpdateMetadataTableElement element) {
		element.preview(owner);
	}

	@Override
	protected void OnSelectElement(UpdateMetadataTableElement element) {
		// NOP
	}

	@Override
	protected boolean isMultiselect() {
		return true;
	}
}
