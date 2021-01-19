package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.stream.CCStreams;

@SuppressWarnings("HardCodedStringLiteral")
public enum AppTheme implements ContinoousEnum<AppTheme>
{
	METAL                         ( 0, AppThemePackage.DEFAULT,   "Metal",                           "javax.swing.plaf.metal.MetalLookAndFeel"),
	WINDOWS                       ( 1, AppThemePackage.DEFAULT,   "Windows",                         "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"),
	WINDOWS_CLASSIC               ( 2, AppThemePackage.DEFAULT,   "Windows Classic",                 "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"),
	GTK                           ( 3, AppThemePackage.DEFAULT,   "GTK",                             "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"),
	AQUA                          ( 4, AppThemePackage.DEFAULT,   "Aqua",                            "com.apple.laf.AquaLookAndFeel"),
	MOTIF                         ( 5, AppThemePackage.DEFAULT,   "Motif",                           "com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
	NIMBUS                        ( 6, AppThemePackage.DEFAULT,   "Nimbus",                          "javax.swing.plaf.nimbus.NimbusLookAndFeel"),

	FLATLAF_LIGHT                 ( 7, AppThemePackage.FLATLAF,   "FlatLaf: Flat Light",             "com.formdev.flatlaf.FlatLightLaf"),
	FLATLAF_DARK                  ( 8, AppThemePackage.FLATLAF,   "FlatLaf: Flat Dark",              "com.formdev.flatlaf.FlatDarkLaf"),
	FLATLAF_INTELLIJ              ( 9, AppThemePackage.FLATLAF,   "FlatLaf: Flat IntelliJ",          "com.formdev.flatlaf.FlatIntelliJLaf"),
	FLATLAF_DARCULA               (10, AppThemePackage.FLATLAF,   "FlatLaf: Flat Darcula",           "com.formdev.flatlaf.FlatDarculaLaf"),

	RADIANCE_AUTUMN               (11, AppThemePackage.RADIANCE, "Radiance: Autumn",               "org.pushingpixels.substance.api.skin.AutumnSkin"),
	RADIANCE_BUSINESS             (12, AppThemePackage.RADIANCE, "Radiance: Business",             "org.pushingpixels.substance.api.skin.BusinessSkin"),
	RADIANCE_BUSINESS_BLACK_STEEL (13, AppThemePackage.RADIANCE, "Radiance: Business Black Steel", "org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin"),
	RADIANCE_BUSINESS_BLUE_STEEL  (14, AppThemePackage.RADIANCE, "Radiance: Business Blue Steel",  "org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin"),
	RADIANCE_CERULEAN             (15, AppThemePackage.RADIANCE, "Radiance: Cerulean",             "org.pushingpixels.substance.api.skin.CeruleanSkin"),
	RADIANCE_CREME                (16, AppThemePackage.RADIANCE, "Radiance: Creme",                "org.pushingpixels.substance.api.skin.CremeSkin"),
	RADIANCE_CREME_COFFEE         (17, AppThemePackage.RADIANCE, "Radiance: Creme Coffee",         "org.pushingpixels.substance.api.skin.CremeCoffeeSkin"),
	RADIANCE_DUST                 (18, AppThemePackage.RADIANCE, "Radiance: Dust",                 "org.pushingpixels.substance.api.skin.DustSkin"),
	RADIANCE_DUST_COFFEE          (19, AppThemePackage.RADIANCE, "Radiance: Dust Coffee",          "org.pushingpixels.substance.api.skin.DustCoffeeSkin"),
	RADIANCE_GEMINI               (20, AppThemePackage.RADIANCE, "Radiance: Gemini",               "org.pushingpixels.substance.api.skin.GeminiSkin"),
	RADIANCE_GRAPHITE             (21, AppThemePackage.RADIANCE, "Radiance: Graphite",             "org.pushingpixels.substance.api.skin.GraphiteSkin"),
	RADIANCE_GRAPHITE_AQUA        (22, AppThemePackage.RADIANCE, "Radiance: Graphite Aqua",        "org.pushingpixels.substance.api.skin.GraphiteAquaSkin"),
	RADIANCE_GRAPHITE_CHALK       (23, AppThemePackage.RADIANCE, "Radiance: Graphite Chalk",       "org.pushingpixels.substance.api.skin.GraphiteChalkSkin"),
	RADIANCE_GRAPHITE_ELECTRIC    (24, AppThemePackage.RADIANCE, "Radiance: Graphite Electric",    "org.pushingpixels.substance.api.skin.GraphiteElectricSkin"),
	RADIANCE_GRAPHITE_GLASS       (25, AppThemePackage.RADIANCE, "Radiance: Graphite Glass",       "org.pushingpixels.substance.api.skin.GraphiteGlassSkin"),
	RADIANCE_GRAPHITE_GOLD        (26, AppThemePackage.RADIANCE, "Radiance: Graphite Gold",        "org.pushingpixels.substance.api.skin.GraphiteGoldSkin"),
	RADIANCE_GRAPHITE_SIENNA      (27, AppThemePackage.RADIANCE, "Radiance: Graphite Sienna",      "org.pushingpixels.substance.api.skin.GraphiteSiennaSkin"),
	RADIANCE_GRAPHITE_SUNSET      (28, AppThemePackage.RADIANCE, "Radiance: Graphite Sunset",      "org.pushingpixels.substance.api.skin.GraphiteSunsetSkin"),
	RADIANCE_GREEN_MAGIC          (29, AppThemePackage.RADIANCE, "Radiance: Green Magic",          "org.pushingpixels.substance.api.skin.GreenMagicSkin"),
	RADIANCE_MAGELLAN             (30, AppThemePackage.RADIANCE, "Radiance: Magellan",             "org.pushingpixels.substance.api.skin.MagellanSkin"),
	RADIANCE_MARINER              (31, AppThemePackage.RADIANCE, "Radiance: Mariner",              "org.pushingpixels.substance.api.skin.MarinerSkin"),
	RADIANCE_MIST_AQUA            (32, AppThemePackage.RADIANCE, "Radiance: Mist Aqua",            "org.pushingpixels.substance.api.skin.MistAquaSkin"),
	RADIANCE_MIST_SILVER          (33, AppThemePackage.RADIANCE, "Radiance: Mist Silver",          "org.pushingpixels.substance.api.skin.MistSilverSkin"),
	RADIANCE_MODERATE             (34, AppThemePackage.RADIANCE, "Radiance: Moderate",             "org.pushingpixels.substance.api.skin.ModerateSkin"),
	RADIANCE_NEBULA               (35, AppThemePackage.RADIANCE, "Radiance: Nebula",               "org.pushingpixels.substance.api.skin.NebulaSkin"),
	RADIANCE_NEBULA_AMETHYST      (36, AppThemePackage.RADIANCE, "Radiance: Nebula Amethyst",      "org.pushingpixels.substance.api.skin.NebulaAmethystSkin"),
	RADIANCE_NEBULA_BRICKWALL     (37, AppThemePackage.RADIANCE, "Radiance: Nebula Brick Wall",    "org.pushingpixels.substance.api.skin.NebulaBrickWallSkin"),
	RADIANCE_NIGHT_SHADE          (38, AppThemePackage.RADIANCE, "Radiance: Night Shade",          "org.pushingpixels.substance.api.skin.NightShadeSkin"),
	RADIANCE_RAVEN                (39, AppThemePackage.RADIANCE, "Radiance: Raven",                "org.pushingpixels.substance.api.skin.RavenSkin"),
	RADIANCE_SAHARA               (40, AppThemePackage.RADIANCE, "Radiance: Sahara",               "org.pushingpixels.substance.api.skin.SaharaSkin"),
	RADIANCE_SENTINEL             (41, AppThemePackage.RADIANCE, "Radiance: Sentinel",             "org.pushingpixels.substance.api.skin.SentinelSkin"),
	RADIANCE_TWILIGHT             (42, AppThemePackage.RADIANCE, "Radiance: Twilight",             "org.pushingpixels.substance.api.skin.TwilightSkin");

	private final static String[] NAMES;
	static { NAMES = CCStreams.iterate(values()).map(p -> p.displayName).toArray(new String[0]); }

	private final int id;
	private final AppThemePackage themepackage;
	private final String displayName;
	private final String lnfClassName;

	private static final EnumWrapper<AppTheme> wrapper = new EnumWrapper<>(METAL, p -> p.id, AppTheme::shouldShowInComboBox);

	private AppTheme(int id, AppThemePackage pack, String name, String classname)
	{
		this.id           = id;
		this.themepackage = pack;
		this.displayName  = name;
		this.lnfClassName = classname;
	}
	
	public static EnumWrapper<AppTheme> getWrapper() {
		return wrapper;
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
}
