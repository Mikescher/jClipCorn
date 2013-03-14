package de.jClipCorn.util;

import java.util.Map.Entry;
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
	
	public static Vector<String> getLookAndFeelList() {
		Vector<String> v = new Vector<>();
		v.add(LocaleBundle.getString("CCProperties.LoonAndFeel.Opt0")); //$NON-NLS-1$
		v.add(LocaleBundle.getString("CCProperties.LoonAndFeel.Opt1")); //$NON-NLS-1$
		v.addAll(getSubstanceLookAndFeelList());
		return v;
	}
	
	private static Vector<String> getSubstanceLookAndFeelList() {
		Vector<String> v = new Vector<>();
		
		for (Entry<String, SkinInfo> entry : SubstanceLookAndFeel.getAllSkins().entrySet()) {
			v.add(entry.getValue().getDisplayName());
		}
		
		return v;
	}

	public static boolean isSubstance() {
		return isSubstance;
	}
}
