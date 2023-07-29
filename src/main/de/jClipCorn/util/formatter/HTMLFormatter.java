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
			if (headerLine != null) sb.append("<b>").append(StringEscapeUtils.escapeHtml4(headerLine)).append("</b>").append("<br/>").append("<br/>");
			for (var line : SimpleFileUtils.splitLines(v))
			{
				sb.append(StringEscapeUtils.escapeHtml4(line).replaceAll(" ", "&nbsp;")).append("<br/>").append("\n");
			}
		}
		sb.append("</html>");

		return sb.toString();
	}

}
