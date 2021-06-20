package de.jClipCorn.gui.frames.applyPatchFrame;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.SimpleFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PatchExecState
{
	public int LastSuccessfulCtr = -1;
	public Map<String, String> Variables = new HashMap<>();

	private String _filename;

	public void load(String fn) throws Exception
	{
		LastSuccessfulCtr = -1;
		Variables.clear();

		if (!new File(fn).exists()) return;

		var txt = SimpleFileUtils.readUTF8TextFile(fn);

		for (var line : txt.split("\\r?\\n"))
		{
			if (Str.isNullOrWhitespace(txt)) continue;

			var split = line.split(":=");
			if (split.length != 2) throw new Exception("Cannot parse line in state: " + line);

			var key = split[0].trim();
			var val = split[1].trim();

			if (Str.equals(key, "[ctr]"))
			{
				LastSuccessfulCtr = Integer.parseInt(val);
			}
			else
			{
				Variables.put(key, val);
			}
		}

		_filename = fn;
	}

	public void save() throws IOException
	{
		StringBuilder r = new StringBuilder();
		r.append("[ctr] := ").append(LastSuccessfulCtr).append("\n");

		for (var e : Variables.entrySet())
		{
			r.append(e.getKey()).append(" := ").append(e.getValue()).append("\n");
		}

		SimpleFileUtils.writeTextFile(_filename, r.toString());
	}
}
