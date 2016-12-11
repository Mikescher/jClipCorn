package de.jClipCorn.gui.frames.updateMetadataFrame;

import java.util.ArrayList;
import java.util.List;

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
				"@LocalScore",
				null,
				e -> e.Element.getOnlinescore().getIcon(),
				e -> e.Element.getOnlinescore().asInt() + "/10"));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"@OnlineScore",
				null,
				e -> (e.OnlineMeta != null && e.OnlineMeta.getOnlineScore() != null) ? (e.OnlineMeta.getOnlineScore().getIcon()) : (null),
						e -> (e.OnlineMeta != null && e.OnlineMeta.getOnlineScore() != null) ? (e.OnlineMeta.getOnlineScore().asInt() + "/10") : (null)));
		
		return r;
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
