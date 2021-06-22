package de.jClipCorn.util.helper;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ClipboardUtilities {
	private static Clipboard systemClip = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	public static String getString() {
		Transferable transfer = systemClip.getContents( null );
		
		String data;
		try {
			data = (String) transfer.getTransferData( DataFlavor.stringFlavor );
		} catch (UnsupportedFlavorException | IOException e) {
			return null;
		}
		
		return data;
	}
	
	public static String getURL() {
		Transferable transfer = systemClip.getContents( null );
		
		String data;
		try {
			data = (String) transfer.getTransferData( DataFlavor.stringFlavor );
		} catch (UnsupportedFlavorException | IOException e) {
			return null;
		}
		
		try {
			new URL(data);
		} catch (MalformedURLException e) {
			return null;
		}
		
		return data;
	}
	
	public static BufferedImage getImage() {
		Transferable transfer = systemClip.getContents( null );
		
		BufferedImage image;
		
		try {
			image = (BufferedImage)transfer.getTransferData(DataFlavor.imageFlavor);
		} catch (UnsupportedFlavorException | IOException e) {
			return null;
		}
		
		return image;
	}

	public static void setString(String cb) {
		systemClip.setContents(new StringSelection(cb), null);
	}
}
