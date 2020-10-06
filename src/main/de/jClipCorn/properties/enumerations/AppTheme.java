package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.stream.CCStreams;

@SuppressWarnings("HardCodedStringLiteral")
public enum AppTheme implements ContinoousEnum<AppTheme>
{
	METAL                          ( 0, AppThemePackage.DEFAULT,   "Metal",                           "javax.swing.plaf.metal.MetalLookAndFeel"),
	WINDOWS                        ( 1, AppThemePackage.DEFAULT,   "Windows",                         "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"),
	WINDOWS_CLASSIC                ( 2, AppThemePackage.DEFAULT,   "Windows Classic",                 "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"),
	GTK                            ( 3, AppThemePackage.DEFAULT,   "GTK",                             "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"),
	AQUA                           ( 4, AppThemePackage.DEFAULT,   "Aqua",                            "com.apple.laf.AquaLookAndFeel"),
	MOTIF                          ( 5, AppThemePackage.DEFAULT,   "Motif",                           "com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
	NIMBUS                         ( 6, AppThemePackage.DEFAULT,   "Nimbus",                          "javax.swing.plaf.nimbus.NimbusLookAndFeel"),

	SUBSTANCE_AUTUMN               ( 7, AppThemePackage.SUBSTANCE, "Substance: Autumn",               "org.pushingpixels.substance.api.skin.AutumnSkin"),
	SUBSTANCE_BUSINESS             ( 8, AppThemePackage.SUBSTANCE, "Substance: Business",             "org.pushingpixels.substance.api.skin.BusinessSkin"),
	SUBSTANCE_BUSINESS_BLACK_STEEL ( 9, AppThemePackage.SUBSTANCE, "Substance: Business Black Steel", "org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin"),
	SUBSTANCE_BUSINESS_BLUE_STEEL  (10, AppThemePackage.SUBSTANCE, "Substance: Business Blue Steel",  "org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin"),
	SUBSTANCE_CERULEAN             (11, AppThemePackage.SUBSTANCE, "Substance: Cerulean",             "org.pushingpixels.substance.api.skin.CeruleanSkin"),
	SUBSTANCE_CHALLENGER_DEEP      (12, AppThemePackage.SUBSTANCE, "Substance: Challenger Deep",      "org.pushingpixels.substance.api.skin.ChallengerDeepSkin"),
	SUBSTANCE_CREME                (13, AppThemePackage.SUBSTANCE, "Substance: Creme",                "org.pushingpixels.substance.api.skin.CremeSkin"),
	SUBSTANCE_CREME_COFFEE         (14, AppThemePackage.SUBSTANCE, "Substance: Creme Coffee",         "org.pushingpixels.substance.api.skin.CremeCoffeeSkin"),
	SUBSTANCE_DUST                 (15, AppThemePackage.SUBSTANCE, "Substance: Dust",                 "org.pushingpixels.substance.api.skin.DustSkin"),
	SUBSTANCE_DUST_COFFEE          (16, AppThemePackage.SUBSTANCE, "Substance: Dust Coffee",          "org.pushingpixels.substance.api.skin.DustCoffeeSkin"),
	SUBSTANCE_EMERALD_DUSK         (17, AppThemePackage.SUBSTANCE, "Substance: Emerald Dusk",         "org.pushingpixels.substance.api.skin.EmeraldDuskSkin"),
	SUBSTANCE_GEMINI               (18, AppThemePackage.SUBSTANCE, "Substance: Gemini",               "org.pushingpixels.substance.api.skin.GeminiSkin"),
	SUBSTANCE_GRAPHITE             (19, AppThemePackage.SUBSTANCE, "Substance: Graphite",             "org.pushingpixels.substance.api.skin.GraphiteSkin"),
	SUBSTANCE_GRAPHITE_AQUA        (20, AppThemePackage.SUBSTANCE, "Substance: Graphite Aqua",        "org.pushingpixels.substance.api.skin.GraphiteAquaSkin"),
	SUBSTANCE_GRAPHITE_GLASS       (21, AppThemePackage.SUBSTANCE, "Substance: Graphite Glass",       "org.pushingpixels.substance.api.skin.GraphiteGlassSkin"),
	SUBSTANCE_MAGELLAN             (22, AppThemePackage.SUBSTANCE, "Substance: Magellan",             "org.pushingpixels.substance.api.skin.MagellanSkin"),
	SUBSTANCE_MARINER              (23, AppThemePackage.SUBSTANCE, "Substance: Mariner",              "org.pushingpixels.substance.api.skin.MarinerSkin"),
	SUBSTANCE_MIST_AQUA            (24, AppThemePackage.SUBSTANCE, "Substance: Mist Aqua",            "org.pushingpixels.substance.api.skin.MistAquaSkin"),
	SUBSTANCE_MIST_SILVER          (25, AppThemePackage.SUBSTANCE, "Substance: Mist Silver",          "org.pushingpixels.substance.api.skin.MistSilverSkin"),
	SUBSTANCE_MODERATE             (26, AppThemePackage.SUBSTANCE, "Substance: Moderate",             "org.pushingpixels.substance.api.skin.ModerateSkin"),
	SUBSTANCE_NEBULA               (27, AppThemePackage.SUBSTANCE, "Substance: Nebula",               "org.pushingpixels.substance.api.skin.NebulaSkin"),
	SUBSTANCE_NEBULA_BRICK_WALL    (28, AppThemePackage.SUBSTANCE, "Substance: Nebula Brick Wall",    "org.pushingpixels.substance.api.skin.NebulaBrickWallSkin"),
	SUBSTANCE_OFFICE_BLACK_2007    (29, AppThemePackage.SUBSTANCE, "Substance: Office Black 2007",    "org.pushingpixels.substance.api.skin.OfficeBlack2007Skin"),
	SUBSTANCE_OFFICE_BLUE_2007     (30, AppThemePackage.SUBSTANCE, "Substance: Office Blue 2007",     "org.pushingpixels.substance.api.skin.OfficeBlue2007Skin"),
	SUBSTANCE_OFFICE_SILVER_2007   (31, AppThemePackage.SUBSTANCE, "Substance: Office Silver 2007",   "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin"),
	SUBSTANCE_RAVEN                (32, AppThemePackage.SUBSTANCE, "Substance: Raven",                "org.pushingpixels.substance.api.skin.RavenSkin"),
	SUBSTANCE_SAHARA               (33, AppThemePackage.SUBSTANCE, "Substance: Sahara",               "org.pushingpixels.substance.api.skin.SaharaSkin"),
	SUBSTANCE_TWILIGHT             (34, AppThemePackage.SUBSTANCE, "Substance: Twilight",             "org.pushingpixels.substance.api.skin.TwilightSkin"),

	FLATLAF_LIGHT                  (35, AppThemePackage.FLATLAF,   "FlatLaf: Flat Light",             "com.formdev.flatlaf.FlatLightLaf"),
	FLATLAF_DARK                   (36, AppThemePackage.FLATLAF,   "FlatLaf: Flat Dark",              "com.formdev.flatlaf.FlatDarkLaf"),
	FLATLAF_INTELLIJ               (37, AppThemePackage.FLATLAF,   "FlatLaf: Flat IntelliJ",          "com.formdev.flatlaf.FlatIntelliJLaf"),
	FLATLAF_DARCULA                (38, AppThemePackage.FLATLAF,   "FlatLaf: Flat Darcula",           "com.formdev.flatlaf.FlatDarculaLaf");

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
