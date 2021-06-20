package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;

import java.util.HashSet;
import java.util.Set;

public class CompareDatabaseRuleset {

	public final Set<Integer> SkipLocal  = new HashSet<>();
	public final Set<Integer> SkipExtern = new HashSet<>();

	public final Set<Tuple<Integer, Integer>> Match = new HashSet<>();

	public final Set<Integer> KeepCoverLocal  = new HashSet<>();
	public final Set<Integer> KeepCoverExtern = new HashSet<>();
	public       boolean      KeepCoverGlobal = false;

	public final Set<Integer> KeepFilesLocal  = new HashSet<>();
	public final Set<Integer> KeepFilesExtern = new HashSet<>();
	public       boolean      KeepFilesGlobal = false;

	public final Set<Integer> KeepMetaLocal  = new HashSet<>();
	public final Set<Integer> KeepMetaExtern = new HashSet<>();
	public       boolean      KeepMetaGlobal = false;

	public final Set<Tuple<String, Integer>> KeepSpecificMetaLocal  = new HashSet<>();
	public final Set<Tuple<String, Integer>> KeepSpecificMetaExtern = new HashSet<>();
	public final Set<String>                 KeepSpecificMetaGlobal = new HashSet<>();

	public final Set<Integer> KeepEntryExtern = new HashSet<>();
	public       boolean      KeepEntryGlobal = false;

	public final Set<Integer> PreventEntryLocal  = new HashSet<>();
	public       boolean      PreventEntryGlobal = false;

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
				r.SkipLocal.add(Integer.parseInt(split[1].replace("local:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "skip") && split[1].startsWith("extern:"))
			{
				r.SkipExtern.add(Integer.parseInt(split[1].replace("extern:", "")));
			}

			else if (split.length == 3 && Str.equals(split[0], "match") && split[1].startsWith("local:") && split[2].startsWith("extern:"))
			{
				r.Match.add(Tuple.Create(Integer.parseInt(split[1].replace("local:", "")), Integer.parseInt(split[2].replace("extern:", ""))));
			}
			else if (split.length == 3 && Str.equals(split[0], "match") && split[1].startsWith("extern:") && split[2].startsWith("local:"))
			{
				r.Match.add(Tuple.Create(Integer.parseInt(split[2].replace("local:", "")), Integer.parseInt(split[1].replace("extern:", ""))));
			}

			else if (split.length == 2 && Str.equals(split[0], "keep_cover") && split[1].startsWith("local:"))
			{
				r.KeepCoverLocal.add(Integer.parseInt(split[1].replace("local:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_cover") && split[1].startsWith("extern:"))
			{
				r.KeepCoverExtern.add(Integer.parseInt(split[1].replace("extern:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_cover") && Str.equals(split[1], "*"))
			{
				r.KeepCoverGlobal = true;
			}

			else if (split.length == 2 && Str.equals(split[0], "keep_files") && split[1].startsWith("local:"))
			{
				r.KeepFilesLocal.add(Integer.parseInt(split[1].replace("local:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_files") && split[1].startsWith("extern:"))
			{
				r.KeepFilesExtern.add(Integer.parseInt(split[1].replace("extern:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_files") && Str.equals(split[1], "*"))
			{
				r.KeepFilesGlobal = true;
			}

			else if (split.length == 2 && Str.equals(split[0], "keep_meta") && split[1].startsWith("local:"))
			{
				r.KeepMetaLocal.add(Integer.parseInt(split[1].replace("local:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_meta") && split[1].startsWith("extern:"))
			{
				r.KeepMetaExtern.add(Integer.parseInt(split[1].replace("extern:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_meta") && Str.equals(split[1], "*"))
			{
				r.KeepMetaGlobal = true;
			}

			else if (split.length == 3 && Str.equals(split[0], "keep_meta") && split[2].startsWith("local:"))
			{
				r.KeepSpecificMetaLocal.add(Tuple.Create(split[1], Integer.parseInt(split[2].replace("local:", ""))));
			}
			else if (split.length == 3 && Str.equals(split[0], "keep_meta") && split[2].startsWith("extern:"))
			{
				r.KeepSpecificMetaExtern.add(Tuple.Create(split[1], Integer.parseInt(split[2].replace("extern:", ""))));
			}
			else if (split.length == 3 && Str.equals(split[0], "keep_meta") && Str.equals(split[2], "*"))
			{
				r.KeepSpecificMetaGlobal.add(split[1]);
			}

			else if (split.length == 2 && Str.equals(split[0], "keep_entry") && split[1].startsWith("extern:"))
			{
				r.KeepEntryExtern.add(Integer.parseInt(split[1].replace("extern:", "")));
			}
			else if (split.length == 2 && Str.equals(split[0], "keep_entry") && Str.equals(split[1], "*"))
			{
				r.KeepEntryGlobal = true;
			}

			else if (split.length == 2 && Str.equals(split[0], "prevent_entry") && split[1].startsWith("local:"))
			{
				r.PreventEntryLocal.add(Integer.parseInt(split[1].replace("extern:", "")));
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

	public boolean ShouldSkipLoc(int locid) {
		return SkipLocal.contains(locid);
	}

	public boolean ShouldSkipExt(int extid) {
		return SkipExtern.contains(extid);
	}

	public boolean IsMatch(int locid, int extid) {
		return CCStreams.iterate(Match).any(p -> p.Item1.equals(locid) && p.Item2.equals(extid));
	}

	public boolean ShouldUpdateCover(int locid, int extid)
	{
		if (KeepCoverGlobal) return false;
		if (KeepCoverLocal .contains(locid)) return false;
		if (KeepCoverExtern.contains(extid)) return false;

		return true;
	}

	public boolean ShouldUpdateFiles(int locid, int extid)
	{
		if (KeepFilesGlobal) return false;
		if (KeepFilesLocal .contains(locid)) return false;
		if (KeepFilesExtern.contains(extid)) return false;

		return true;
	}

	public boolean ShouldUpdateMetadata(int locid, int extid, IEProperty locprop, IEProperty extprop)
	{
		if (KeepMetaGlobal) return false;
		if (KeepMetaLocal .contains(locid)) return false;
		if (KeepMetaExtern.contains(extid)) return false;

		if (KeepSpecificMetaGlobal.contains(locprop.getName())) return false;
		if (CCStreams.iterate(KeepSpecificMetaLocal) .any(p -> Str.equals(p.Item1, locprop.getName()) && p.Item2.equals(locid))) return false;
		if (CCStreams.iterate(KeepSpecificMetaExtern).any(p -> Str.equals(p.Item1, extprop.getName()) && p.Item2.equals(extid))) return false;

		return true;
	}

	public boolean ShouldDeleteExtern(int extid)
	{
		if (KeepEntryGlobal) return false;
		if (KeepEntryExtern.contains(extid)) return false;

		return true;
	}

	public boolean ShouldAddLocal(int locid)
	{
		if (PreventEntryGlobal) return false;
		if (PreventEntryLocal.contains(locid)) return false;

		return true;
	}
}
