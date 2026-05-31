package de.jClipCorn.properties.types;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.stream.CCStreams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A per-host frame size: a default/fallback {@link FrameSizeVar} plus an optional list of host-specific overrides.
 * <p>
 * Resolution (see {@link #resolve()}) mirrors {@link BinaryPathVar#getPathAndArgs()}: the override matching the
 * current host wins, otherwise the default is used.
 * <p>
 * Serialization is backward compatible: without overrides the value is stored as the plain {@code [w,h]} form
 * (identical to the legacy single-value format); with overrides a JSON object is stored.
 */
public class HostFrameSizeVar {

	public static class SingleHostFrameSize {
		public final String       Hostname;
		public final FrameSizeVar Size;
		public SingleHostFrameSize(String hostname, FrameSizeVar size) {
			Hostname = hostname;
			Size     = size;
		}
	}

	public final FrameSizeVar              Default;
	public final List<SingleHostFrameSize> Hosts;

	public HostFrameSizeVar(FrameSizeVar def) {
		this(def, Collections.emptyList());
	}

	public HostFrameSizeVar(FrameSizeVar def, List<SingleHostFrameSize> hosts) {
		Default = def;
		Hosts   = Collections.unmodifiableList(hosts);
	}

	/**
	 * @return the override for the current host if one exists, otherwise the default value.
	 */
	public FrameSizeVar resolve() {
		var hostname = ApplicationHelper.getHostname();

		for (var h : Hosts) {
			if (!Str.isNullOrWhitespace(h.Hostname) && h.Hostname.equalsIgnoreCase(hostname)) return h.Size;
		}

		return Default;
	}

	/**
	 * @return the override stored for the given host, or null if there is none.
	 */
	public FrameSizeVar getForHost(String hostname) {
		for (var h : Hosts) {
			if (!Str.isNullOrWhitespace(h.Hostname) && h.Hostname.equalsIgnoreCase(hostname)) return h.Size;
		}
		return null;
	}

	@Override
	public String toString() {
		if (Hosts.isEmpty()) return Default.toString();
		return Default + " " + CCStreams.iterate(Hosts).map(h -> "[" + h.Hostname + "]" + h.Size).stringjoin(p -> p, ", "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@SuppressWarnings("nls")
	public String serialize() {
		if (Hosts.isEmpty()) return Default.serialize();

		var jroot = new JSONObject();

		var jdef = new JSONArray();
		jdef.put(Default.Width);
		jdef.put(Default.Height);
		jroot.put("def", jdef);

		var jarr = new JSONArray();
		for (var h : Hosts) {
			if (Str.isNullOrWhitespace(h.Hostname)) continue;
			var jobj = new JSONObject();
			jobj.put("host", h.Hostname);
			jobj.put("w", h.Size.Width);
			jobj.put("h", h.Size.Height);
			jarr.put(jobj);
		}
		jroot.put("hosts", jarr);

		return jroot.toString();
	}

	@SuppressWarnings("nls")
	public static HostFrameSizeVar parse(String val) throws Exception {
		if (val == null) throw new Exception("null HostFrameSizeVar value");

		val = val.trim();

		if (val.startsWith("[")) {
			// legacy / default-only format: [w,h]
			return new HostFrameSizeVar(parseSize(val));
		}

		JSONObject root = new JSONObject(new JSONTokener(val));

		var jdef  = root.getJSONArray("def");
		var def   = new FrameSizeVar(jdef.getInt(0), jdef.getInt(1));

		var hosts = new ArrayList<SingleHostFrameSize>();
		if (root.has("hosts")) {
			var jarr = root.getJSONArray("hosts");
			for (int i = 0; i < jarr.length(); i++) {
				var jobj = jarr.getJSONObject(i);
				var host = jobj.getString("host");
				if (Str.isNullOrWhitespace(host)) continue;
				hosts.add(new SingleHostFrameSize(host, new FrameSizeVar(jobj.getInt("w"), jobj.getInt("h"))));
			}
		}

		return new HostFrameSizeVar(def, hosts);
	}

	@SuppressWarnings("nls")
	private static FrameSizeVar parseSize(String val) throws Exception {
		if (!val.startsWith("[") || !val.endsWith("]")) throw new Exception("invalid FrameSizeVar value '" + val + "'");

		val = val.substring(1, val.length() - 1);

		String[] sval = val.split(",");
		if (sval.length != 2) throw new Exception("invalid FrameSizeVar value '" + val + "'");

		return new FrameSizeVar(Integer.parseInt(sval[0].trim()), Integer.parseInt(sval[1].trim()));
	}
}
