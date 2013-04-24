package de.jClipCorn.util;

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
import de.jClipCorn.gui.log.CCLog;

public class LookAndFeelManager {
	private static boolean isSubstance = false;
	
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
			break;
		case 1:
			setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			isSubstance = false;
			break;
		default:
			String subLNF = getSubstanceLookAndFeel(propertynumber - 2);
			if (subLNF == null) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.LNFNotFound", propertynumber)); //$NON-NLS-1$
			} else {
				SubstanceLookAndFeel.setSkin(subLNF);
			}
			isSubstance = true;
			break;
		}
	}
	
	public static int getLookAndFeelCount() {
		return 2 + SubstanceLookAndFeel.getAllSkins().size();
	}
	
	private static String getSubstanceLookAndFeel(int n) {
		for (Entry<String, SkinInfo> entry : SubstanceLookAndFeel.getAllSkins().entrySet()) {
			if (n == 0) {
				return entry.getValue().getClassName();
			}
			n--;
		}
		return null;
	}
	
	public static List<String> getLookAndFeelList() {
		List<String> v = new Vector<>();
		v.add(LocaleBundle.getString("CCProperties.LoonAndFeel.Opt0")); //$NON-NLS-1$
		v.add(LocaleBundle.getString("CCProperties.LoonAndFeel.Opt1")); //$NON-NLS-1$
		v.addAll(getSubstanceLookAndFeelList());
		return v;
	}
	
	private static List<String> getSubstanceLookAndFeelList() {
		List<String> v = new Vector<>();
		
		for (Entry<String, SkinInfo> entry : SubstanceLookAndFeel.getAllSkins().entrySet()) {
			v.add(entry.getValue().getDisplayName());
		}
		
		return v;
	}

	public static boolean isSubstance() {
		return isSubstance;
	}
	
	public static void printAllColorKeys() {
		List<String> colorKeys = new ArrayList<String>();
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
			System.out.println(colorKey);
		}
	}
}
