package de.jClipCorn.util.helper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SkinInfo;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;

public class LookAndFeelManager {
	public final static int ID_LNF_WINDOWS = 0;
	public final static int ID_LNF_METAL   = 1;

	private static boolean isSubstance = false;
	private static boolean isMetal = false;
	private static boolean isWindows = false;
	
	private static List<Entry<String, SkinInfo>> substanceLookAndFeelCache = null;
	
	private static void setLookAndFeel(String cls) {
		try {
			UIManager.setLookAndFeel(cls);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			CCLog.addError(e);
		}
	}
	
	public static void setLookAndFeel(int propertynumber) {
		switch (propertynumber) {
		case 0:
			setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			isSubstance = false;
			isMetal = false;
			isWindows = true;
			break;
		case 1:
			setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			isSubstance = false;
			isMetal = true;
			isWindows = false;
			break;
		default:
			String subLNF = getSubstanceLookAndFeel(propertynumber - 2);
			if (subLNF == null) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.LNFNotFound", propertynumber)); //$NON-NLS-1$
			} else {
				SubstanceLookAndFeel.setSkin(subLNF);
			}
			isSubstance = true;
			isMetal = false;
			isWindows = false;
			break;
		}
	}
	
	public static int getLookAndFeelCount() {
		return 2 + getSubstanceLookAndFeelEntryList().size();
	}
	
	private static String getSubstanceLookAndFeel(int n) {
		List<Entry<String, SkinInfo>> slfl = getSubstanceLookAndFeelEntryList();

		if (n >= 0  && n < slfl.size()) return slfl.get(n).getValue().getClassName();
		return null;
	}
	
	public static List<String> getLookAndFeelList() {
		List<String> v = new Vector<>();
		v.add(LocaleBundle.getString("CCProperties.LoonAndFeel.Opt0")); //$NON-NLS-1$
		v.add(LocaleBundle.getString("CCProperties.LoonAndFeel.Opt1")); //$NON-NLS-1$
		v.addAll(getSubstanceLookAndFeelList());
		return v;
	}
	
	public static Vector<String> getLookAndFeelVector() {
		return new Vector<>(getLookAndFeelList());
	}
	
	private static List<String> getSubstanceLookAndFeelList() {
		List<String> v = new Vector<>();
		
		for (Entry<String, SkinInfo> entry : getSubstanceLookAndFeelEntryList()) {
			v.add(entry.getValue().getDisplayName());
		}
		
		return v;
	}

	public static boolean isSubstance() {
		return isSubstance;
	}

	public static boolean isMetal() {
		return isMetal;
	}

	public static boolean isWindows() {
		return isWindows;
	}
	
	public static void printAllColorKeys() {
		List<String> colorKeys = new ArrayList<>();
		Set<Entry<Object, Object>> entries = UIManager.getLookAndFeelDefaults().entrySet();
		for (Entry<?, ?> entry : entries) {
			if (entry.getValue() instanceof Color) {
				colorKeys.add((String) entry.getKey());
			}
		}

		// sort the color keys
		Collections.sort(colorKeys);

		// print the color keys
		for (String colorKey : colorKeys) {
			CCLog.addDebug(colorKey);
		}
	}
	
	private static List<Entry<String, SkinInfo>> getSubstanceLookAndFeelEntryList() {
		if (substanceLookAndFeelCache == null) {
			substanceLookAndFeelCache = new ArrayList<>();
			
			for (Entry<String, SkinInfo> entry : SubstanceLookAndFeel.getAllSkins().entrySet()) {
				substanceLookAndFeelCache.add(entry);
			}
		}
		
		return substanceLookAndFeelCache;
	}
}
