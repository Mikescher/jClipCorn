package de.jClipCorn.util.formatter;

import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.apache.commons.text.StringEscapeUtils;

public class HTMLFormatter
{
	public static String formatTooltip(String v) {
		return formatTooltip(null, v);
	}

	public static String formatTooltip(String headerLine, String v) {

		var sb = new StringBuilder();

		sb.append("<html>");
		{
			if (headerLine != null) sb.append("<b>").append(escape(headerLine)).append("</b>").append("<br/>").append("<br/>");
			appendLines(sb, v);
		}
		sb.append("</html>");

		return sb.toString();
	}

	public static String escape(String v) {
		return StringEscapeUtils.escapeHtml4(v);
	}

	public static void appendLines(StringBuilder sb, String v) {
		for (var line : SimpleFileUtils.splitLines(v))
		{
			sb.append(escape(line).replaceAll(" ", "&nbsp;")).append("<br/>").append("\n");
		}
	}

}
