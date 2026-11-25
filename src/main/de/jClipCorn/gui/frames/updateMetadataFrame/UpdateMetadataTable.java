package de.jClipCorn.gui.frames.updateMetadataFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateMetadataTable extends JCCSimpleTable<UpdateMetadataTableElement> {
	private static final long serialVersionUID = 3308858204018846266L;

	public static final String GROUP_DETAILS = "details"; //$NON-NLS-1$

	private final UpdateMetadataFrame owner;

	public boolean DeleteLocalGenres;
	public boolean DeleteLocalReferences;
	public boolean DeleteLocalAnimeSeason;
	public boolean DeleteLocalAnimeStudio;
	public boolean OverwriteLocalShowStatus;

	@DesignCreate
	private static UpdateMetadataTable designCreate() { return new UpdateMetadataTable(new UpdateMetadataFrame(ICCWindow.Dummy.frame(), CCMovieList.createStub())); }

	public UpdateMetadataTable(UpdateMetadataFrame owner) {
		super(owner);
		this.owner = owner;

		setGroupVisible(GROUP_DETAILS, false);
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
		 .withForeground(e -> isLargeScoreChange(e) ? Color.RED : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnChangedGenres")
		 .withSize("auto")
		 .withText(this::getGenreChange)
		 .withTooltip(this::getGenreTooltip)
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

		r.add("UpdateMetadataFrame.Table.ColumnChangedShowStatus")
		 .withSize("auto")
		 .withText(this::getShowStatusChange)
		 .withTooltip(this::getShowStatusTooltip)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalScore")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withIcon(e -> e.supportsScore() ? e.getLocalScore().getIcon() : null)
		 .withTooltip(e -> e.supportsScore() ? e.getLocalScore().getDisplayString() : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineScore")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withIcon(e -> (e.OnlineMeta != null && e.OnlineMeta.OnlineScore != null) ? (e.OnlineMeta.OnlineScore.getIcon()) : (null))
		 .withTooltip(e -> (e.OnlineMeta != null && e.OnlineMeta.OnlineScore != null) ? (e.OnlineMeta.OnlineScore.getDisplayString()) : (null))
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalGenres")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> e.supportsGenres() ? e.getLocalGenres().asSortedString() : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineGenres")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null && e.OnlineMeta.Genres != null) ? e.OnlineMeta.Genres.asSortedString() : (null))
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalReferences")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> e.getOnlineReference().toSourceConcatString())
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineReferences")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null) ? e.OnlineMeta.getFullReference().toSourceConcatString() : (null))
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalAnimeSeason")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> { var v = e.getLocalAnimeSeason(); return (v != null && !v.isEmpty()) ? v.toString() : null; })
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineAnimeSeason")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null && e.OnlineMeta.AnimeSeason != null && !e.OnlineMeta.AnimeSeason.isEmpty()) ? e.OnlineMeta.AnimeSeason.toString() : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalAnimeStudio")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> { var v = e.getLocalAnimeStudio(); return (v != null && !v.isEmpty()) ? v.toString() : null; })
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineAnimeStudio")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> (e.OnlineMeta != null && e.OnlineMeta.AnimeStudio != null && !e.OnlineMeta.AnimeStudio.isEmpty()) ? e.OnlineMeta.AnimeStudio.toString() : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnLocalShowStatus")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> e.supportsShowStatus() ? e.getLocalShowStatus().asString() : null)
		 .sortable();

		r.add("UpdateMetadataFrame.Table.ColumnOnlineShowStatus")
		 .inGroup(GROUP_DETAILS)
		 .withSize("auto")
		 .withText(e -> (e.OnlineShowStatus != null) ? e.OnlineShowStatus.asString() : null)
		 .sortable();

		return r;
	}

	public boolean isScoreChange(UpdateMetadataTableElement e) {
		if (!e.supportsScore()) return false;
		if (e.OnlineMeta == null || e.OnlineMeta.OnlineScore == null) return false;
		return !CCOnlineScore.isEqual(e.getLocalScore(), e.OnlineMeta.OnlineScore);
	}

	/** Online score differs by more than 20% (relative to the local score). */
	public boolean isLargeScoreChange(UpdateMetadataTableElement e) {
		if (!isScoreChange(e)) return false;

		var l = e.getLocalScore().getRatio();
		var o = e.OnlineMeta.OnlineScore.getRatio();
		if (l <= 0) return false;

		return Math.abs(o - l) > 0.2 * l;
	}

	public List<CCGenre> getGenresToAdd(UpdateMetadataTableElement e) {
		List<CCGenre> r = new ArrayList<>();
		if (!e.supportsGenres() || e.OnlineMeta == null || e.OnlineMeta.Genres == null) return r;

		CCGenreList local = e.getLocalGenres();
		for (CCGenre online : e.OnlineMeta.Genres.iterate()) {
			if (!local.includes(online)) r.add(online);
		}

		if (!DeleteLocalGenres) {
			int free = CCGenreList.getMaxListSize() - local.getGenreCount();
			while (r.size() > Math.max(free, 0)) r.remove(r.size() - 1);
		}

		return r;
	}

	public List<CCGenre> getGenresToRemove(UpdateMetadataTableElement e) {
		List<CCGenre> r = new ArrayList<>();
		if (!DeleteLocalGenres) return r;
		if (!e.supportsGenres() || e.OnlineMeta == null || e.OnlineMeta.Genres == null) return r;

		for (CCGenre local : e.getLocalGenres().iterate()) {
			if (!e.OnlineMeta.Genres.includes(local)) r.add(local);
		}

		return r;
	}

	public boolean isGenreChange(UpdateMetadataTableElement e) {
		return !getGenresToAdd(e).isEmpty() || !getGenresToRemove(e).isEmpty();
	}

	public boolean isReferenceChange(UpdateMetadataTableElement e) {
		if (e.OnlineMeta == null) return false;

		CCOnlineReferenceList onlineref = e.OnlineMeta.getFullReference();
		CCOnlineReferenceList localref  = e.getOnlineReference();

		if (onlineref == null || !onlineref.isMainSet()) return false;

		if (DeleteLocalReferences) return !localref.equalsIgnoreAdditionalOrder(onlineref);

		for (CCSingleOnlineReference r : onlineref) if (!CCStreams.iterate(localref).contains(r)) return true;
		return false;
	}

	public boolean isAnimeSeasonChange(UpdateMetadataTableElement e) {
		if (!e.supportsAnime() || e.OnlineMeta == null) return false;
		return hasStringListChange(e.getLocalAnimeSeason(), e.OnlineMeta.AnimeSeason, DeleteLocalAnimeSeason);
	}

	public boolean isAnimeStudioChange(UpdateMetadataTableElement e) {
		if (!e.supportsAnime() || e.OnlineMeta == null) return false;
		return hasStringListChange(e.getLocalAnimeStudio(), e.OnlineMeta.AnimeStudio, DeleteLocalAnimeStudio);
	}

	private static boolean hasStringListChange(CCStringList local, CCStringList online, boolean replace) {
		if (online == null || online.isEmpty()) return false;
		if (local == null) local = CCStringList.EMPTY;

		for (String o : online) if (!local.contains(o)) return true;
		if (replace) {
			for (String l : local) if (!online.contains(l)) return true;
		}

		return false;
	}

	/** Whether updating the show-status would change this element, honoring {@link #OverwriteLocalShowStatus}. */
	public boolean isShowStatusChange(UpdateMetadataTableElement e) {
		if (!e.supportsShowStatus() || e.OnlineShowStatus == null) return false;

		var local = e.getLocalShowStatus();
		if (local == e.OnlineShowStatus) return false;

		return OverwriteLocalShowStatus || local == CCTVShowStatus.UNKNOWN;
	}

	public boolean applyScore(UpdateMetadataTableElement e) {
		if (!isScoreChange(e)) return false;
		e.setScore(e.OnlineMeta.OnlineScore);
		return true;
	}

	public boolean applyGenres(UpdateMetadataTableElement e) {
		if (!isGenreChange(e)) return false;

		if (DeleteLocalGenres) {
			e.setGenres(e.OnlineMeta.Genres);
		} else {
			for (CCGenre g : getGenresToAdd(e)) e.tryAddGenre(g);
		}
		return true;
	}

	public boolean applyReferences(UpdateMetadataTableElement e) {
		if (!isReferenceChange(e)) return false;

		CCOnlineReferenceList onlineref = e.OnlineMeta.getFullReference();

		if (DeleteLocalReferences) {
			e.setOnlineReference(onlineref);
		} else {
			CCOnlineReferenceList localref = e.getOnlineReference();
			for (CCSingleOnlineReference ref : onlineref) {
				if (!CCStreams.iterate(localref).contains(ref)) localref = localref.addAdditional(ref);
			}
			e.setOnlineReference(localref);
		}
		return true;
	}

	public boolean applyAnimeSeason(UpdateMetadataTableElement e) {
		if (!isAnimeSeasonChange(e)) return false;
		e.setAnimeSeason(mergeStringList(e.getLocalAnimeSeason(), e.OnlineMeta.AnimeSeason, DeleteLocalAnimeSeason));
		return true;
	}

	public boolean applyAnimeStudio(UpdateMetadataTableElement e) {
		if (!isAnimeStudioChange(e)) return false;
		e.setAnimeStudio(mergeStringList(e.getLocalAnimeStudio(), e.OnlineMeta.AnimeStudio, DeleteLocalAnimeStudio));
		return true;
	}

	public boolean applyShowStatus(UpdateMetadataTableElement e) {
		if (!isShowStatusChange(e)) return false;
		e.setShowStatus(e.OnlineShowStatus);
		return true;
	}

	private static CCStringList mergeStringList(CCStringList local, CCStringList online, boolean replace) {
		if (replace) return online;

		CCStringList result = (local == null) ? CCStringList.EMPTY : local;
		for (String o : online) if (!result.contains(o)) result = result.getAdd(o);
		return result;
	}

	@SuppressWarnings("nls")
	private String getShowStatusChange(UpdateMetadataTableElement e) {
		if (!e.supportsShowStatus() || e.OnlineShowStatus == null) return "";
		if (!isShowStatusChange(e)) return "-";

		return e.getLocalShowStatus().asString() + " -> " + e.OnlineShowStatus.asString();
	}

	private String getShowStatusTooltip(UpdateMetadataTableElement e) {
		if (!e.supportsShowStatus() || e.OnlineShowStatus == null) return null;
		if (isShowStatusChange(e)) return null;

		var local = e.getLocalShowStatus();
		if (local == e.OnlineShowStatus) return null;

		return LocaleBundle.getFormattedString("UpdateMetadataFrame.Table.TooltipShowStatusNotOverwritten", local.asString(), e.OnlineShowStatus.asString()); //$NON-NLS-1$
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

		if (isScoreChange(e))
			return "[=] " + e.getLocalScore().getDisplayString() + " -> " + e.OnlineMeta.OnlineScore.getDisplayString();

		return "-";
	}

	@SuppressWarnings("nls")
	private String getGenreChange(UpdateMetadataTableElement e) {
		if (!e.supportsGenres()) return "";
		if (e.OnlineMeta == null) return "";
		if (e.OnlineMeta.Genres == null) return "ERR";

		int gnew = getGenresToAdd(e).size();
		int gdel = getGenresToRemove(e).size();

		if (gnew == 0 && gdel == 0) return "-";

		return DeleteLocalGenres ? ("+" + gnew + " " + "-" + gdel) : ("+" + gnew);
	}

	@SuppressWarnings("nls")
	private String getGenreTooltip(UpdateMetadataTableElement e) {
		var add = getGenresToAdd(e);
		var del = getGenresToRemove(e);
		if (add.isEmpty() && del.isEmpty()) return null;

		StringBuilder b = new StringBuilder("<html>");
		if (!add.isEmpty()) b.append(LocaleBundle.getFormattedString("UpdateMetadataFrame.Table.TooltipGenresAdd", CCStreams.iterate(add).stringjoin(CCGenre::asString, ", ")));
		if (!add.isEmpty() && !del.isEmpty()) b.append("<br>");
		if (!del.isEmpty()) b.append(LocaleBundle.getFormattedString("UpdateMetadataFrame.Table.TooltipGenresRemove", CCStreams.iterate(del).stringjoin(CCGenre::asString, ", ")));
		b.append("</html>");
		return b.toString();
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

	private String getAnimeSeasonChange(UpdateMetadataTableElement e) {
		return getStringListChange(e, e.getLocalAnimeSeason(), (e.OnlineMeta == null) ? null : e.OnlineMeta.AnimeSeason, DeleteLocalAnimeSeason);
	}

	private String getAnimeStudioChange(UpdateMetadataTableElement e) {
		return getStringListChange(e, e.getLocalAnimeStudio(), (e.OnlineMeta == null) ? null : e.OnlineMeta.AnimeStudio, DeleteLocalAnimeStudio);
	}

	@SuppressWarnings("nls")
	private String getStringListChange(UpdateMetadataTableElement e, CCStringList local, CCStringList online, boolean replace) {
		if (online == null || online.isEmpty()) return "";
		if (!e.supportsAnime()) return "N/A";
		if (local == null) local = CCStringList.EMPTY;

		int gnew = 0;
		int gdel = 0;

		for (String o : online) if (!local.contains(o)) gnew++;
		if (replace) for (String l : local) if (!online.contains(l)) gdel++;

		if (gnew == 0 && gdel == 0) return "-";

		return replace ? ("+" + gnew + " " + "-" + gdel) : ("+" + gnew);
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
