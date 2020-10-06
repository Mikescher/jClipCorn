package de.jClipCorn.gui;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.enumerations.AppTheme;
import de.jClipCorn.properties.enumerations.AppThemePackage;
import de.jClipCorn.util.stream.CCStreams;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LookAndFeelManager {

	private static Set<AppTheme> _installedLookAndFeels = null;

	private static AppTheme currentAppTheme = AppTheme.METAL;

	public static Set<AppTheme> listInstalledLookAndFeels()
	{
		if (_installedLookAndFeels != null) return _installedLookAndFeels;

		_installedLookAndFeels = CCStreams
			.iterate(UIManager.getInstalledLookAndFeels())
			.map(UIManager.LookAndFeelInfo::getClassName)
			.append(CCStreams.iterate(SubstanceLookAndFeel.getAllSkins()).map(p -> p.getValue().getClassName()))
			.append(listInstalledFlatlafLookAndFeels())
			.map(p -> CCStreams.iterate(AppTheme.getWrapper().allValues()).firstOrNull(q -> q.getClassName().equals(p)))
			.filter(Objects::nonNull)
			.toSet();

		return _installedLookAndFeels;
	}

	private static List<String> listInstalledFlatlafLookAndFeels()
	{
		return CCStreams
				.iterate(AppTheme.values())
				.filter(p -> p.getThemePackage() == AppThemePackage.FLATLAF)
				.filter(p -> { try { Class.forName(p.getClassName()); return true; } catch( ClassNotFoundException e ) { return false; } })
				.map(AppTheme::getClassName)
				.toList();
	}

	public static void setLookAndFeel(AppTheme at, boolean updateWindows) {
		try
		{
			if (!listInstalledLookAndFeels().contains(at)) at = AppTheme.METAL;

			currentAppTheme = at;

			var atpack = at.getThemePackage();

			if (atpack == AppThemePackage.DEFAULT)
			{
				UIManager.setLookAndFeel(at.getClassName());
			}
			else if (atpack == AppThemePackage.SUBSTANCE)
			{
				SubstanceLookAndFeel.setSkin(at.getClassName());
			}
			else if (atpack == AppThemePackage.FLATLAF)
			{
				UIManager.setLookAndFeel(at.getClassName());
			}
			else
			{
				CCLog.addDefaultSwitchError(LookAndFeelManager.class, atpack);
			}

			if (updateWindows)
			{
				for (var window: Window.getWindows())
				{
					if (!window.getClass().getCanonicalName().contains("de.jClipCorn")) continue; //$NON-NLS-1$
					SwingUtilities.updateComponentTreeUI(window);
				}
			}
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			CCLog.addError(e);
		}
	}

	public static boolean isSubstance() {
		return currentAppTheme.getThemePackage() == AppThemePackage.SUBSTANCE;
	}

	public static boolean isWindows() {
		return currentAppTheme == AppTheme.WINDOWS;
	}

	public static boolean isMetal() {
		return currentAppTheme == AppTheme.METAL;
	}

	public static AppTheme getCurrentTheme() {
		return currentAppTheme;
	}
}
