package de.jClipCorn.gui.frames.updateMetadataFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.gui.frames.updateCodecFrame.UpdateCodecTableElement;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.stream.CCStreams;

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
	protected JCCSimpleColumnList<UpdateMetadataTableElement> configureColumns() {
		JCCSimpleColumnList<UpdateMetadataTableElement> r = new JCCSimpleColumnList<>(this);

		r.add("")
		 .withSize("auto")
		 .withIcon(e -> e.Element.getOnlineReference().Main.getIcon16x16())
		 .sortable();
		
		r.add("UpdateMetadataFrame.Table.ColumnTitle")
		 .withSize("*,min=auto,max=600,priority=max")
		 .withText(e -> e.Element.getFullDisplayTitle())
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnChangedScore")
		 .withSize("auto")
		 .withText(this::getScoreChange)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnChangedGenres")
		 .withSize("auto")
		 .withText(this::getGenreChange)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnChangedReferences")
		 .withSize("auto")
		 .withText(this::getReferencesChange)
		 .sortable();
		
		r.add("UpdateMetadataFrame.Table.ColumnLocalScore")
		 .withSize("auto")
		 .withIcon(e -> e.Element.getOnlinescore().getIcon())
		 .withTooltip(e -> e.Element.getOnlinescore().getDisplayString())
		 .sortable();
		
		r.add("UpdateMetadataFrame.Table.ColumnOnlineScore")
		 .withSize("auto")
		 .withIcon(e -> (e.OnlineMeta != null && e.OnlineMeta.OnlineScore != null) ? (e.OnlineMeta.OnlineScore.getIcon()) : (null))
		 .withTooltip(e -> (e.OnlineMeta != null && e.OnlineMeta.OnlineScore != null) ? (e.OnlineMeta.OnlineScore.getDisplayString()) : (null))
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalGenres")
		 .withSize("auto")
		 .withText(e -> e.Element.getGenres().asSortedString())
		 .sortable();
		
		r.add("UpdateMetadataFrame.Table.ColumnOnlineGenres")
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null && e.OnlineMeta.Genres != null) ? e.OnlineMeta.Genres.asSortedString() : (null))
		 .sortable();
		
		r.add("UpdateMetadataFrame.Table.ColumnLocalReferences")
		 .withSize("auto")
		 .withText(e -> e.Element.getOnlineReference().toSourceConcatString())
		 .sortable();
		
		r.add("UpdateMetadataFrame.Table.ColumnOnlineReferences")
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null) ? e.OnlineMeta.getFullReference().toSourceConcatString() : (null))
		 .sortable();
		
		return r;
	}

	@SuppressWarnings("nls")
	private String getScoreChange(UpdateMetadataTableElement e) {
		if (e.OnlineMeta == null) return "";
		if (e.OnlineMeta.OnlineScore == null) return "ERR";
		
		var o = e.OnlineMeta.OnlineScore.getRatio();
		var l = e.Element.getOnlinescore().getRatio();
		
		if (o > l) return "[+] " + e.Element.getOnlinescore().getDisplayString() + " -> " + e.OnlineMeta.OnlineScore.getDisplayString();
		if (o < l) return "[-] " + e.Element.getOnlinescore().getDisplayString() + " -> " + e.OnlineMeta.OnlineScore.getDisplayString();

		if (!CCOnlineScore.isEqual(e.OnlineMeta.OnlineScore, e.Element.getOnlinescore()))
			return "[=] " + e.Element.getOnlinescore().getDisplayString() + " -> " + e.OnlineMeta.OnlineScore.getDisplayString();

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
