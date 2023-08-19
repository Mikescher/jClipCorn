package de.jClipCorn.gui.frames.updateCodecFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;

public class UpdateCodecTable extends JCCSimpleTable<UpdateCodecTableElement> {
	private static final long serialVersionUID = 3308858204018846266L;
	
	private final UpdateCodecFrame owner;

	@DesignCreate
	private static UpdateCodecTable designCreate() { return new UpdateCodecTable(new UpdateCodecFrame(ICCWindow.Dummy.frame(), CCMovieList.createStub())); }

	public UpdateCodecTable(UpdateCodecFrame owner) {
		super(owner);
		this.owner = owner;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<UpdateCodecTableElement> configureColumns() {
		JCCSimpleColumnList<UpdateCodecTableElement> r = new JCCSimpleColumnList<>(this);

		r.add("")
		 .withSize("auto")
		 .withIcon(UpdateCodecTableElement::getStatusIcon)
		 .withTooltip(e -> Str.limit(Str.firstLine(Str.trim(e.getStatusText())), 128))
		 .sortable();

		r.add("UpdateCodecFrame.Table.ColumnTitle")
		 .withSize("*,min=auto,max=600,priority=max")
		 .withText(UpdateCodecTableElement::getFullDisplayTitle)
		 .sortable();

		r.add("UpdateCodecFrame.Table.ColumnLangOld")
		 .withSize("auto")
		 .withText(e -> e.getOldLanguage().toShortOutputString(Str.Empty))
		 .withIcon(e -> e.getOldLanguage().getIcon())
		 .sortable();

		r.add("UpdateCodecFrame.Table.ColumnLangNew")
		 .withSize("auto")
		 .withText(e -> e.getNewLanguage().toShortOutputString(Str.Empty))
		 .withIcon(e -> e.getNewLanguage().isEmpty() ? Resources.ICN_TRANSPARENT.get16x16() : e.getNewLanguage().getIcon())
		 .sortable();

		r.add("UpdateCodecFrame.Table.ColumnSubsOld")
		 .withSize("auto")
		 .withIcon(e -> e.getOldSubtitles().getIcon(-1))
		 .sortable();

		r.add("UpdateCodecFrame.Table.ColumnSubsNew")
		 .withSize("auto")
		 .withIcon(e -> e.getNewSubtitles().getIcon(-1))
		 .sortable();

		r.add("UpdateCodecFrame.Table.ColumnLengthOld")
		 .withSize("auto")
		 .withText(UpdateCodecTableElement::getOldLengthStr)
		 .sortable();

		r.add("UpdateCodecFrame.Table.ColumnLengthNew")
		 .withSize("auto")
		 .withText(UpdateCodecTableElement::getNewLengthStr)
		 .sortable();

		r.add("UpdateCodecFrame.Table.ColumnQualityOld")
		 .withSize("auto")
		 .withText(   e -> e.getOldMediaInfo().getCategory(e.getSourceGenres()).map(CCQualityCategory::getLongText).orElse(Str.Empty))
		 .withIcon(   e -> e.getOldMediaInfo().getCategory(e.getSourceGenres()).map(CCQualityCategory::getIcon).orElse(null))
		 .withTooltip(e -> e.getOldMediaInfo().getCategory(e.getSourceGenres()).map(CCQualityCategory::getTooltip).orElse(Str.Empty));

		r.add("UpdateCodecFrame.Table.ColumnQualityNew")
		 .withSize("auto")
		 .withText(   e -> e.getNewMediaInfo().getCategory(e.getSourceGenres()).map(CCQualityCategory::getLongText).orElse(Str.Empty))
		 .withIcon(   e -> e.getNewMediaInfo().getCategory(e.getSourceGenres()).map(CCQualityCategory::getIcon).orElse(null))
		 .withTooltip(e -> e.getNewMediaInfo().getCategory(e.getSourceGenres()).map(CCQualityCategory::getTooltip).orElse(Str.Empty));
		
		return r;
	}

	@Override
	protected void OnDoubleClickElement(UpdateCodecTableElement element) {
		element.preview(owner);
	}

	@Override
	protected void OnSelectElement(UpdateCodecTableElement element) {
		owner.setSelection(element);
	}

	@Override
	protected boolean isMultiselect() {
		return true;
	}
}
