package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public abstract class ComparisonMatch {

	abstract String getTypeStr();

	abstract boolean getNeedsDelete();
	abstract boolean getNeedsUpdateCover();
	abstract boolean getNeedsUpdateFile();
	abstract boolean getNeedsUpdateMetadata();
	abstract boolean getNeedsCreateNew();

	abstract ICCDatabaseStructureElement getLocal();
	abstract ICCDatabaseStructureElement getExtern();

	abstract List<Tuple<IEProperty, IEProperty>> getMetaDiff();

	public String getStrAction()
	{
		var r = new ArrayList<String>();

		if (getNeedsCreateNew())      r.add("FullCopy");
		if (getNeedsDelete())         r.add("Delete");
		if (getNeedsUpdateMetadata()) r.add("UpdateData");
		if (getNeedsUpdateCover())    r.add("CopyCover");
		if (getNeedsUpdateFile())     r.add("CopyVideo");

		return "[" + CCStreams.iterate(r).stringjoin(e -> e, ", ") + "]";
	}

	public boolean getNeedsAnything()
	{
		return getNeedsDelete() || getNeedsUpdateCover() || getNeedsUpdateFile() || getNeedsUpdateMetadata() || getNeedsCreateNew();
	}

	public Opt<Integer> getLocID()
	{
		return Opt.ofNullable(getLocal()).map(ICCDatabaseStructureElement::getLocalID);
	}

	public Opt<Integer> getExtID()
	{
		return Opt.ofNullable(getExtern()).map(ICCDatabaseStructureElement::getLocalID);
	}

	public Opt<String> getLocTitle()
	{
		return Opt.ofNullable(getLocal()).map(ICCDatabaseStructureElement::getQualifiedTitle);
	}

	public Opt<String> getExtTitle()
	{
		return Opt.ofNullable(getExtern()).map(ICCDatabaseStructureElement::getQualifiedTitle);
	}

	public String getDiffStr() {
		if (getLocal() == null) return "[Delete]";
		if (getExtern() == null) return "[Copy]";

		var propLoc = CCStreams.iterate(getLocal().getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var propExt = CCStreams.iterate(getExtern().getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);

		return CCStreams.iterate(getMetaDiff())
				.map(p -> Str.format("[{0}]\n  Local  := {1}\n  Extern := {2}\n", p.Item1.getName(), p.Item1.serializeToString(), p.Item2.serializeToString()))
				.stringjoin(e -> e, "\n");
	}
}
