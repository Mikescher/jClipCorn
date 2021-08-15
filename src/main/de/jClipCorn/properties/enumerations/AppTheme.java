package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.stream.CCStreams;

@SuppressWarnings("HardCodedStringLiteral")
public enum AppTheme implements ContinoousEnum<AppTheme>
{
	METAL                         ( 0, AppThemePackage.DEFAULT,   false, "Metal",                           "javax.swing.plaf.metal.MetalLookAndFeel"),
	WINDOWS                       ( 1, AppThemePackage.DEFAULT,   false, "Windows",                         "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"),
	WINDOWS_CLASSIC               ( 2, AppThemePackage.DEFAULT,   false, "Windows Classic",                 "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"),
	GTK                           ( 3, AppThemePackage.DEFAULT,   false, "GTK",                             "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"),
	AQUA                          ( 4, AppThemePackage.DEFAULT,   false, "Aqua",                            "com.apple.laf.AquaLookAndFeel"),
	MOTIF                         ( 5, AppThemePackage.DEFAULT,   false, "Motif",                           "com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
	NIMBUS                        ( 6, AppThemePackage.DEFAULT,   false, "Nimbus",                          "javax.swing.plaf.nimbus.NimbusLookAndFeel"),

	FLATLAF_LIGHT                 ( 7, AppThemePackage.FLATLAF,   false, "FlatLaf: Flat Light",             "com.formdev.flatlaf.FlatLightLaf"),
	FLATLAF_DARK                  ( 8, AppThemePackage.FLATLAF,   true,  "FlatLaf: Flat Dark",              "com.formdev.flatlaf.FlatDarkLaf"),
	FLATLAF_INTELLIJ              ( 9, AppThemePackage.FLATLAF,   false, "FlatLaf: Flat IntelliJ",          "com.formdev.flatlaf.FlatIntelliJLaf"),
	FLATLAF_DARCULA               (10, AppThemePackage.FLATLAF,   true,  "FlatLaf: Flat Darcula",           "com.formdev.flatlaf.FlatDarculaLaf"),

	RADIANCE_AUTUMN               (11, AppThemePackage.RADIANCE, false, "Radiance: Autumn",               "org.pushingpixels.substance.api.skin.AutumnSkin"),
	RADIANCE_BUSINESS             (12, AppThemePackage.RADIANCE, false, "Radiance: Business",             "org.pushingpixels.substance.api.skin.BusinessSkin"),
	RADIANCE_BUSINESS_BLACK_STEEL (13, AppThemePackage.RADIANCE, false, "Radiance: Business Black Steel", "org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin"),
	RADIANCE_BUSINESS_BLUE_STEEL  (14, AppThemePackage.RADIANCE, false, "Radiance: Business Blue Steel",  "org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin"),
	RADIANCE_CERULEAN             (15, AppThemePackage.RADIANCE, false, "Radiance: Cerulean",             "org.pushingpixels.substance.api.skin.CeruleanSkin"),
	RADIANCE_CREME                (16, AppThemePackage.RADIANCE, false, "Radiance: Creme",                "org.pushingpixels.substance.api.skin.CremeSkin"),
	RADIANCE_CREME_COFFEE         (17, AppThemePackage.RADIANCE, false, "Radiance: Creme Coffee",         "org.pushingpixels.substance.api.skin.CremeCoffeeSkin"),
	RADIANCE_DUST                 (18, AppThemePackage.RADIANCE, false, "Radiance: Dust",                 "org.pushingpixels.substance.api.skin.DustSkin"),
	RADIANCE_DUST_COFFEE          (19, AppThemePackage.RADIANCE, false, "Radiance: Dust Coffee",          "org.pushingpixels.substance.api.skin.DustCoffeeSkin"),
	RADIANCE_GEMINI               (20, AppThemePackage.RADIANCE, false, "Radiance: Gemini",               "org.pushingpixels.substance.api.skin.GeminiSkin"),
	RADIANCE_GRAPHITE             (21, AppThemePackage.RADIANCE, true,  "Radiance: Graphite",             "org.pushingpixels.substance.api.skin.GraphiteSkin"),
	RADIANCE_GRAPHITE_AQUA        (22, AppThemePackage.RADIANCE, true,  "Radiance: Graphite Aqua",        "org.pushingpixels.substance.api.skin.GraphiteAquaSkin"),
	RADIANCE_GRAPHITE_CHALK       (23, AppThemePackage.RADIANCE, true,  "Radiance: Graphite Chalk",       "org.pushingpixels.substance.api.skin.GraphiteChalkSkin"),
	RADIANCE_GRAPHITE_ELECTRIC    (24, AppThemePackage.RADIANCE, true,  "Radiance: Graphite Electric",    "org.pushingpixels.substance.api.skin.GraphiteElectricSkin"),
	RADIANCE_GRAPHITE_GLASS       (25, AppThemePackage.RADIANCE, true,  "Radiance: Graphite Glass",       "org.pushingpixels.substance.api.skin.GraphiteGlassSkin"),
	RADIANCE_GRAPHITE_GOLD        (26, AppThemePackage.RADIANCE, true,  "Radiance: Graphite Gold",        "org.pushingpixels.substance.api.skin.GraphiteGoldSkin"),
	RADIANCE_GRAPHITE_SIENNA      (27, AppThemePackage.RADIANCE, true,  "Radiance: Graphite Sienna",      "org.pushingpixels.substance.api.skin.GraphiteSiennaSkin"),
	RADIANCE_GRAPHITE_SUNSET      (28, AppThemePackage.RADIANCE, true,  "Radiance: Graphite Sunset",      "org.pushingpixels.substance.api.skin.GraphiteSunsetSkin"),
	RADIANCE_GREEN_MAGIC          (29, AppThemePackage.RADIANCE, false, "Radiance: Green Magic",          "org.pushingpixels.substance.api.skin.GreenMagicSkin"),
	RADIANCE_MAGELLAN             (30, AppThemePackage.RADIANCE, false, "Radiance: Magellan",             "org.pushingpixels.substance.api.skin.MagellanSkin"),
	RADIANCE_MARINER              (31, AppThemePackage.RADIANCE, false, "Radiance: Mariner",              "org.pushingpixels.substance.api.skin.MarinerSkin"),
	RADIANCE_MIST_AQUA            (32, AppThemePackage.RADIANCE, false, "Radiance: Mist Aqua",            "org.pushingpixels.substance.api.skin.MistAquaSkin"),
	RADIANCE_MIST_SILVER          (33, AppThemePackage.RADIANCE, false, "Radiance: Mist Silver",          "org.pushingpixels.substance.api.skin.MistSilverSkin"),
	RADIANCE_MODERATE             (34, AppThemePackage.RADIANCE, false, "Radiance: Moderate",             "org.pushingpixels.substance.api.skin.ModerateSkin"),
	RADIANCE_NEBULA               (35, AppThemePackage.RADIANCE, false, "Radiance: Nebula",               "org.pushingpixels.substance.api.skin.NebulaSkin"),
	RADIANCE_NEBULA_AMETHYST      (36, AppThemePackage.RADIANCE, false, "Radiance: Nebula Amethyst",      "org.pushingpixels.substance.api.skin.NebulaAmethystSkin"),
	RADIANCE_NEBULA_BRICKWALL     (37, AppThemePackage.RADIANCE, false, "Radiance: Nebula Brick Wall",    "org.pushingpixels.substance.api.skin.NebulaBrickWallSkin"),
	RADIANCE_NIGHT_SHADE          (38, AppThemePackage.RADIANCE, true,  "Radiance: Night Shade",          "org.pushingpixels.substance.api.skin.NightShadeSkin"),
	RADIANCE_RAVEN                (39, AppThemePackage.RADIANCE, true,  "Radiance: Raven",                "org.pushingpixels.substance.api.skin.RavenSkin"),
	RADIANCE_SAHARA               (40, AppThemePackage.RADIANCE, false, "Radiance: Sahara",               "org.pushingpixels.substance.api.skin.SaharaSkin"),
	RADIANCE_SENTINEL             (41, AppThemePackage.RADIANCE, false, "Radiance: Sentinel",             "org.pushingpixels.substance.api.skin.SentinelSkin"),
	RADIANCE_TWILIGHT             (42, AppThemePackage.RADIANCE, true,  "Radiance: Twilight",             "org.pushingpixels.substance.api.skin.TwilightSkin");

	private final static String[] NAMES;
	static { NAMES = CCStreams.iterate(values()).map(p -> p.displayName).toArray(new String[0]); }

	private final int id;
	private final AppThemePackage themepackage;
	private final boolean isdark;
	private final String displayName;
	private final String lnfClassName;

	private static final EnumWrapper<AppTheme> wrapper = new EnumWrapper<>(METAL, p -> p.id, AppTheme::shouldShowInComboBox);

	AppTheme(int id, AppThemePackage pack, boolean dark, String name, String classname)
	{
		this.id           = id;
		this.themepackage = pack;
		this.isdark       = dark;
		this.displayName  = name;
		this.lnfClassName = classname;
	}
	
	public static EnumWrapper<AppTheme> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}
	
	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String[] getList() {
		return NAMES;
	}
	
	@Override
	public String asString() {
		return displayName;
	}

	@Override
	public AppTheme[] evalues() {
		return AppTheme.values();
	}

	private boolean shouldShowInComboBox() {
		return LookAndFeelManager.listInstalledLookAndFeels().contains(this);
	}

	public String getClassName() {
		return lnfClassName;
	}

	public AppThemePackage getThemePackage() {
		return themepackage;
	}

	public boolean isDark() {
		return isdark;
	}
}
