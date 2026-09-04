package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;

import java.util.HashSet;
import java.util.Set;

public class CompareDatabaseRuleset {

	public final Set<CCUUID> SkipLocal  = new HashSet<>();
	public final Set<CCUUID> SkipExtern = new HashSet<>();

	public final Set<Tuple<CCUUID, CCUUID>> Match = new HashSet<>();

	public final Set<CCUUID> KeepCoverLocal  = new HashSet<>();
	public final Set<CCUUID> KeepCoverExtern = new HashSet<>();
	public       boolean     KeepCoverGlobal = false;

	public final Set<CCUUID> KeepFilesLocal  = new HashSet<>();
	public final Set<CCUUID> KeepFilesExtern = new HashSet<>();
	public       boolean     KeepFilesGlobal = false;

	public final Set<CCUUID> KeepMetaLocal  = new HashSet<>();
	public final Set<CCUUID> KeepMetaExtern = new HashSet<>();
	public       boolean     KeepMetaGlobal = false;

	public final Set<Tuple<String, CCUUID>> KeepSpecificMetaLocal  = new HashSet<>();
	public final Set<Tuple<String, CCUUID>> KeepSpecificMetaExtern = new HashSet<>();
	public final Set<String>                KeepSpecificMetaGlobal = new HashSet<>();

	public final Set<CCUUID> KeepEntryExtern = new HashSet<>();
	public       boolean     KeepEntryGlobal = false;

	public final Set<CCUUID> PreventEntryLocal  = new HashSet<>();
	public       boolean     PreventEntryGlobal = false;

	private CompareDatabaseRuleset(){}

	@SuppressWarnings("nls")
	public static CompareDatabaseRuleset parse(String str) throws Exception
	{
		var r = new CompareDatabaseRuleset();

		int linenum = 0;
		for (var refline: str.split("\\r?\\n"))
		{
			linenum++;

			var line = refline.trim().toLowerCase();

			if (line.contains("//")) line = line.substring(0, line.indexOf("//")).trim();
			if (line.contains("#"))  line = line.substring(0, line.indexOf("#" )).trim();

			if (Str.isNullOrWhitespace(line)) continue;

			var split = line.split("\\s+");

			if (split.length == 2 && Str.equals(split[0], "skip") && split[1].startsWith("local:"))
			{
				r.SkipLocal.add(CCUUID.parse(split[1].replace("local:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "skip") && split[1].startsWith("extern:"))
			{
				r.SkipExtern.add(CCUUID.parse(split[1].replace("extern:", "")));
			}

			else if (split.length == 3 && Str.equals(split[0], "match") && split[1].startsWith("local:") && split[2].startsWith("extern:"))
			{
				r.Match.add(Tuple.Create(CCUUID.parse(split[1].replace("local:", "")), CCUUID.parse(split[2].replace("extern:", ""))));
			}
			else if (split.length == 3 && Str.equals(split[0], "match") && split[1].startsWith("extern:") && split[2].startsWith("local:"))
			{
				r.Match.add(Tuple.Create(CCUUID.parse(split[2].replace("local:", "")), CCUUID.parse(split[1].replace("extern:", ""))));
			}

			else if (split.length == 2 && Str.equals(split[0], "keep_cover") && split[1].startsWith("local:"))
			{
				r.KeepCoverLocal.add(CCUUID.parse(split[1].replace("local:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_cover") && split[1].startsWith("extern:"))
			{
				r.KeepCoverExtern.add(CCUUID.parse(split[1].replace("extern:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_cover") && Str.equals(split[1], "*"))
			{
				r.KeepCoverGlobal = true;
			}

			else if (split.length == 2 && Str.equals(split[0], "keep_files") && split[1].startsWith("local:"))
			{
				r.KeepFilesLocal.add(CCUUID.parse(split[1].replace("local:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_files") && split[1].startsWith("extern:"))
			{
				r.KeepFilesExtern.add(CCUUID.parse(split[1].replace("extern:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_files") && Str.equals(split[1], "*"))
			{
				r.KeepFilesGlobal = true;
			}

			else if (split.length == 2 && Str.equals(split[0], "keep_meta") && split[1].startsWith("local:"))
			{
				r.KeepMetaLocal.add(CCUUID.parse(split[1].replace("local:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_meta") && split[1].startsWith("extern:"))
			{
				r.KeepMetaExtern.add(CCUUID.parse(split[1].replace("extern:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_meta") && Str.equals(split[1], "*"))
			{
				r.KeepMetaGlobal = true;
			}

			else if (split.length == 3 && Str.equals(split[0], "keep_meta") && split[2].startsWith("local:"))
			{
				r.KeepSpecificMetaLocal.add(Tuple.Create(split[1], CCUUID.parse(split[2].replace("local:", ""))));
			}
			else if (split.length == 3 && Str.equals(split[0], "keep_meta") && split[2].startsWith("extern:"))
			{
				r.KeepSpecificMetaExtern.add(Tuple.Create(split[1], CCUUID.parse(split[2].replace("extern:", ""))));
			}
			else if (split.length == 3 && Str.equals(split[0], "keep_meta") && Str.equals(split[2], "*"))
			{
				r.KeepSpecificMetaGlobal.add(split[1]);
			}

			else if (split.length == 2 && Str.equals(split[0], "keep_entry") && split[1].startsWith("extern:"))
			{
				r.KeepEntryExtern.add(CCUUID.parse(split[1].replace("extern:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_entry") && Str.equals(split[1], "*"))
			{
				r.KeepEntryGlobal = true;
			}

			else if (split.length == 2 && Str.equals(split[0], "prevent_entry") && split[1].startsWith("local:"))
			{
				r.PreventEntryLocal.add(CCUUID.parse(split[1].replace("local:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "prevent_entry") && Str.equals(split[1], "*"))
			{
				r.PreventEntryGlobal = true;
			}

			else
			{
				throw new Exception("Could not parse line " + linenum + ": '" + line);
			}
		}

		return r;
	}

	public boolean ShouldSkipLoc(CCUUID locid) {
		return SkipLocal.contains(locid);
	}

	public boolean ShouldSkipExt(CCUUID extid) {
		return SkipExtern.contains(extid);
	}

	public boolean IsMatch(CCUUID locid, CCUUID extid) {
		return CCStreams.iterate(Match).any(p -> p.Item1.equals(locid) && p.Item2.equals(extid));
	}

	public boolean ShouldUpdateCover(CCUUID locid, CCUUID extid)
	{
		if (KeepCoverGlobal) return false;
		if (KeepCoverLocal .contains(locid)) return false;
		if (KeepCoverExtern.contains(extid)) return false;

		return true;
	}

	public boolean ShouldUpdateFiles(CCUUID locid, CCUUID extid)
	{
		if (KeepFilesGlobal) return false;
		if (KeepFilesLocal .contains(locid)) return false;
		if (KeepFilesExtern.contains(extid)) return false;

		return true;
	}

	public boolean ShouldUpdateMetadata(CCUUID locid, CCUUID extid, IEProperty locprop, IEProperty extprop)
	{
		if (KeepMetaGlobal) return false;
		if (KeepMetaLocal .contains(locid)) return false;
		if (KeepMetaExtern.contains(extid)) return false;

		// rule lines are normalized to lowercase, property names are PascalCase
		if (CCStreams.iterate(KeepSpecificMetaGlobal).any(p -> Str.equalsIgnoreCase(p, locprop.getName()))) return false;
		if (CCStreams.iterate(KeepSpecificMetaLocal) .any(p -> Str.equalsIgnoreCase(p.Item1, locprop.getName()) && p.Item2.equals(locid))) return false;
		if (CCStreams.iterate(KeepSpecificMetaExtern).any(p -> Str.equalsIgnoreCase(p.Item1, extprop.getName()) && p.Item2.equals(extid))) return false;

		return true;
	}

	public boolean ShouldDeleteExtern(CCUUID extid)
	{
		if (KeepEntryGlobal) return false;
		if (KeepEntryExtern.contains(extid)) return false;

		return true;
	}

	public boolean ShouldAddLocal(CCUUID locid)
	{
		if (PreventEntryGlobal) return false;
		if (PreventEntryLocal.contains(locid)) return false;

		return true;
	}
}
