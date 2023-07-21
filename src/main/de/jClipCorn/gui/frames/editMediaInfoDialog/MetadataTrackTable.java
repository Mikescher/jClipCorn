package de.jClipCorn.gui.frames.editMediaInfoDialog;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.ITrackMetadata;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;

public class MetadataTrackTable extends JCCSimpleTable<ITrackMetadata> {
	@DesignCreate
	private static MetadataTrackTable designCreate() { return new MetadataTrackTable(ICCWindow.Dummy.frame()); }

	public MetadataTrackTable(ICCWindow f) {
		super(f);
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<ITrackMetadata> configureColumns() {
		JCCSimpleColumnList<ITrackMetadata> r = new JCCSimpleColumnList<>(this);

		r.add("@#")
				.withSize("auto,min=16")
				.withText(p -> String.valueOf(p.getTypeIndex()))
				.unsortable();

		r.add(Str.Empty)
				.withSize("auto,min=24")
				.withIcon(p -> p.calcCCDBLanguage().map(CCDBLanguage::getIcon).orElseOrErr(null, Resources.ICN_TABLE_LANGUAGE_NONE.get()))
				.unsortable();

		r.add("@Type")
				.withSize("auto,min=48")
				.withText(ITrackMetadata::getType)
				.unsortable();

		r.add("@Lang")
				.withSize("auto,min=86")
				.withText(p -> p.getLanguageText().orElseOrErr(Str.Empty, "(error)"))
				.unsortable();

		r.add("@Title")
				.withSize("*,min=auto")
				.withText(p -> p.getTitle().orElseOrErr(Str.Empty, "(error)"))
				.unsortable();

		return r;
	}

	@Override
	protected void OnDoubleClickElement(ITrackMetadata element) {
		// nothing
	}

	@Override
	protected void OnSelectElement(ITrackMetadata element) {
		// nothing
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
