package de.jClipCorn.gui.frames.updateMetadataFrame;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

public class UpdateMetadataTable extends JCCSimpleTable<UpdateMetadataTableElement> {
	private static final long serialVersionUID = 3308858204018846266L;
	
	private final UpdateMetadataFrame owner;

	public UpdateMetadataTable(UpdateMetadataFrame owner) {
		super();
		this.owner = owner;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<UpdateMetadataTableElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<UpdateMetadataTableElement>> r = new ArrayList<>();
		
		r.add(new JCCSimpleColumnPrototype<>(
				"",
				null,
				e -> e.Element.getOnlineReference().getIcon16x16(),
				null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"@Titel",
				e -> e.Element.getFullDisplayTitle(),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"@ChangedScore",
				this::getScoreChange,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"@ChangedGenres",
				this::getGenreChange,
				null,
				null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"@LocalScore",
				null,
				e -> e.Element.getOnlinescore().getIcon(),
				e -> e.Element.getOnlinescore().asInt() + "/10"));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"@OnlineScore",
				null,
				e -> (e.OnlineMeta != null && e.OnlineMeta.getOnlineScore() != null) ? (e.OnlineMeta.getOnlineScore().getIcon()) : (null),
				e -> (e.OnlineMeta != null && e.OnlineMeta.getOnlineScore() != null) ? (e.OnlineMeta.getOnlineScore().asInt() + "/10") : (null)));

		r.add(new JCCSimpleColumnPrototype<>(
				"@LocalGenres",
				e -> e.Element.getGenres().asString(),
				null,
				null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"@OnlineGenres",
				e -> (e.OnlineMeta != null && e.OnlineMeta.Genres != null) ? e.OnlineMeta.Genres.asString() : (null),
				null,
				null));
		
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

		int gnew  = 0;
		
		for (CCGenre online : e.OnlineMeta.Genres.iterate()) {
			if (!e.Element.getGenres().includes(online)) gnew++;
		}
		
		if (gnew == 0) return "-";
		
		return "+" + gnew;
	}
	
	@Override
	protected int getColumnAdjusterMaxWidth() {
		return 400;
	}

	@Override
	protected void OnDoubleClickElement(UpdateMetadataTableElement element) {
		element.preview(owner);
	}

	@Override
	protected void OnSelectElement(UpdateMetadataTableElement element) {
		// NOP
	}

}
