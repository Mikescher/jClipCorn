package de.jClipCorn.gui.frames.updateMetadataFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;

public class UpdateMetadataTable extends JCCSimpleTable<UpdateMetadataTableElement> {
	private static final long serialVersionUID = 3308858204018846266L;

	private final UpdateMetadataFrame owner;

	public boolean DeleteLocalGenres;
	public boolean DeleteLocalReferences;
	public boolean DeleteLocalAnimeSeason;
	public boolean DeleteLocalAnimeStudio;

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
		 .withIcon(e -> e.getOnlineReference().Main.getIcon16x16())
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnTitle")
		 .withSize("*,min=auto,max=600,priority=max")
		 .withText(UpdateMetadataTableElement::getDisplayTitle)
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

		r.add("UpdateMetadataFrame.Table.ColumnChangedAnimeSeason")
		 .withSize("auto")
		 .withText(this::getAnimeSeasonChange)
		 .withTransparency(p -> getAnimeSeasonChange(p).equals("N/A") ? 0.5 : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnChangedAnimeStudio")
		 .withSize("auto")
		 .withText(this::getAnimeStudioChange)
		 .withTransparency(p -> getAnimeStudioChange(p).equals("N/A") ? 0.5 : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalScore")
		 .withSize("auto")
		 .withIcon(e -> e.supportsScore() ? e.getLocalScore().getIcon() : null)
		 .withTooltip(e -> e.supportsScore() ? e.getLocalScore().getDisplayString() : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineScore")
		 .withSize("auto")
		 .withIcon(e -> (e.OnlineMeta != null && e.OnlineMeta.OnlineScore != null) ? (e.OnlineMeta.OnlineScore.getIcon()) : (null))
		 .withTooltip(e -> (e.OnlineMeta != null && e.OnlineMeta.OnlineScore != null) ? (e.OnlineMeta.OnlineScore.getDisplayString()) : (null))
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalGenres")
		 .withSize("auto")
		 .withText(e -> e.supportsGenres() ? e.getLocalGenres().asSortedString() : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineGenres")
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null && e.OnlineMeta.Genres != null) ? e.OnlineMeta.Genres.asSortedString() : (null))
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalReferences")
		 .withSize("auto")
		 .withText(e -> e.getOnlineReference().toSourceConcatString())
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineReferences")
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null) ? e.OnlineMeta.getFullReference().toSourceConcatString() : (null))
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalAnimeSeason")
		 .withSize("auto")
		 .withText(e -> { var v = e.getLocalAnimeSeason(); return (v != null && !v.isEmpty()) ? v.toString() : null; })
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineAnimeSeason")
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null && e.OnlineMeta.AnimeSeason != null && !e.OnlineMeta.AnimeSeason.isEmpty()) ? e.OnlineMeta.AnimeSeason.toString() : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalAnimeStudio")
		 .withSize("auto")
		 .withText(e -> { var v = e.getLocalAnimeStudio(); return (v != null && !v.isEmpty()) ? v.toString() : null; })
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineAnimeStudio")
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null && e.OnlineMeta.AnimeStudio != null && !e.OnlineMeta.AnimeStudio.isEmpty()) ? e.OnlineMeta.AnimeStudio.toString() : null)
		 .sortable();

		return r;
	}

	@SuppressWarnings("nls")
	private String getScoreChange(UpdateMetadataTableElement e) {
		if (!e.supportsScore()) return "";
		if (e.OnlineMeta == null) return "";
		if (e.OnlineMeta.OnlineScore == null) return "ERR";

		var o = e.OnlineMeta.OnlineScore.getRatio();
		var l = e.getLocalScore().getRatio();

		if (o > l) return "[+] " + e.getLocalScore().getDisplayString() + " -> " + e.OnlineMeta.OnlineScore.getDisplayString();
		if (o < l) return "[-] " + e.getLocalScore().getDisplayString() + " -> " + e.OnlineMeta.OnlineScore.getDisplayString();

		if (!CCOnlineScore.isEqual(e.OnlineMeta.OnlineScore, e.getLocalScore()))
			return "[=] " + e.getLocalScore().getDisplayString() + " -> " + e.OnlineMeta.OnlineScore.getDisplayString();

		return "-";
	}

	@SuppressWarnings("nls")
	private String getGenreChange(UpdateMetadataTableElement e) {
		if (!e.supportsGenres()) return "";
		if (e.OnlineMeta == null) return "";
		if (e.OnlineMeta.Genres == null) return "ERR";

		if (DeleteLocalGenres) {
			int gnew = 0;
			int gdel = 0;

			for (CCGenre online : e.OnlineMeta.Genres.iterate()) {
				if (!e.getLocalGenres().includes(online)) gnew++;
			}
			for (CCGenre local : e.getLocalGenres().iterate()) {
				if (!e.OnlineMeta.Genres.includes(local)) gdel++;
			}

			if (gnew == 0 && gdel == 0) return "-";

			return "+" + gnew + " " + "-" + gdel;
		} else {
			int gnew = 0;

			for (CCGenre online : e.OnlineMeta.Genres.iterate()) {
				if (!e.getLocalGenres().includes(online)) gnew++;
			}

			gnew = Math.min(gnew, CCGenreList.getMaxListSize() - e.getLocalGenres().getGenreCount());

			if (gnew == 0) return "-";

			return "+" + gnew;
		}
	}

	@SuppressWarnings("nls")
	private String getReferencesChange(UpdateMetadataTableElement e) {
		if (e.OnlineMeta == null) return "";

		CCOnlineReferenceList onlineref = e.OnlineMeta.getFullReference();
		CCOnlineReferenceList localref  = e.getOnlineReference();

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

	@SuppressWarnings("nls")
	private String getAnimeSeasonChange(UpdateMetadataTableElement e) {
		return getStringListChange(e, e.getLocalAnimeSeason(), (e.OnlineMeta == null) ? null : e.OnlineMeta.AnimeSeason, DeleteLocalAnimeSeason);
	}

	@SuppressWarnings("nls")
	private String getAnimeStudioChange(UpdateMetadataTableElement e) {
		return getStringListChange(e, e.getLocalAnimeStudio(), (e.OnlineMeta == null) ? null : e.OnlineMeta.AnimeStudio, DeleteLocalAnimeStudio);
	}

	@SuppressWarnings("nls")
	private String getStringListChange(UpdateMetadataTableElement e, CCStringList local, CCStringList online, boolean replace) {
		if (online == null || online.isEmpty()) return "";
		if (!e.supportsAnime()) return "N/A";
		if (local == null) local = CCStringList.EMPTY;

		if (replace) {
			int gnew = 0;
			int gdel = 0;

			for (String o : online) if (!local.contains(o)) gnew++;
			for (String l : local)  if (!online.contains(l)) gdel++;

			if (gnew == 0 && gdel == 0) return "-";

			return "+" + gnew + " " + "-" + gdel;
		} else {
			int gnew = 0;

			for (String o : online) if (!local.contains(o)) gnew++;

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
