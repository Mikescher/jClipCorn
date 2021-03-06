package de.jClipCorn.util.formatter;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class FileSizeFormatter {
	private static final String[] UNITS = new String[] { 
		"B", 	//$NON-NLS-1$
		"KB", 	//$NON-NLS-1$
		"MB", 	//$NON-NLS-1$
		"GB",  	//$NON-NLS-1$
		"TB",  	//$NON-NLS-1$
		"PB",  	//$NON-NLS-1$
		"EB" 	//$NON-NLS-1$
		};

	public static String format(long bytes) {
		if (bytes <= 0) {
			return "0"; //$NON-NLS-1$
		}
		int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(bytes / Math.pow(1024, digitGroups)) + " " + UNITS[digitGroups]; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String formatPrecise(CCFileSize fs) {
		var bytes = fs.getBytes();

		if (bytes <= 0) {
			return "0"; //$NON-NLS-1$
		}
		int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
		return new DecimalFormat("0.####").format(bytes / Math.pow(1024, digitGroups)) + " " + UNITS[digitGroups]; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String format(CCFileSize size) {
		return format(size.getBytes());
	}

	@SuppressWarnings("nls")
	public static String formatBytes(CCFileSize value) {
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator(' ');
		formatter.setDecimalFormatSymbols(symbols);

		return formatter.format(value.getBytes()) + " bytes";
	}
}
