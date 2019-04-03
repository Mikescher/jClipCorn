package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.ArrayList;
import java.util.Comparator;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCGenre implements ContinoousEnum<CCGenre> {
	GENRE_000(0x00), GENRE_001(0x01), GENRE_002(0x02), GENRE_003(0x03), GENRE_004(0x04), GENRE_005(0x05), GENRE_006(0x06), GENRE_007(0x07),
	GENRE_008(0x08), GENRE_009(0x09), GENRE_010(0x0A), GENRE_011(0x0B), GENRE_012(0x0C), GENRE_013(0x0D), GENRE_014(0x0E), GENRE_015(0x0F),
	GENRE_016(0x10), GENRE_017(0x11), GENRE_018(0x12), GENRE_019(0x13), GENRE_020(0x14), GENRE_021(0x15), GENRE_022(0x16), GENRE_023(0x17),
	GENRE_024(0x18), GENRE_025(0x19), GENRE_026(0x1A), GENRE_027(0x1B), GENRE_028(0x1C), GENRE_029(0x1D), GENRE_030(0x1E), GENRE_031(0x1F),
	GENRE_032(0x20), GENRE_033(0x21), GENRE_034(0x22), GENRE_035(0x23), GENRE_036(0x24), GENRE_037(0x25), GENRE_038(0x26), GENRE_039(0x27),
	GENRE_040(0x28), GENRE_041(0x29), GENRE_042(0x2A), GENRE_043(0x2B), GENRE_044(0x2C), GENRE_045(0x2D), GENRE_046(0x2E), GENRE_047(0x2F),
	GENRE_048(0x30), GENRE_049(0x31), GENRE_050(0x32), GENRE_051(0x33), GENRE_052(0x34), GENRE_053(0x35), GENRE_054(0x36), GENRE_055(0x37),
	GENRE_056(0x38), GENRE_057(0x39), GENRE_058(0x3A), GENRE_059(0x3B), GENRE_060(0x3C), GENRE_061(0x3D), GENRE_062(0x3E), GENRE_063(0x3F),
	GENRE_064(0x40), GENRE_065(0x41), GENRE_066(0x42), GENRE_067(0x43), GENRE_068(0x44), GENRE_069(0x45), GENRE_070(0x46), GENRE_071(0x47),
	GENRE_072(0x48), GENRE_073(0x49), GENRE_074(0x4A), GENRE_075(0x4B), GENRE_076(0x4C), GENRE_077(0x4D), GENRE_078(0x4E), GENRE_079(0x4F),
	GENRE_080(0x50), GENRE_081(0x51), GENRE_082(0x52), GENRE_083(0x53), GENRE_084(0x54), GENRE_085(0x55), GENRE_086(0x56), GENRE_087(0x57),
	GENRE_088(0x58), GENRE_089(0x59), GENRE_090(0x5A), GENRE_091(0x5B), GENRE_092(0x5C), GENRE_093(0x5D), GENRE_094(0x5E), GENRE_095(0x5F),
	GENRE_096(0x60), GENRE_097(0x61), GENRE_098(0x62), GENRE_099(0x63), GENRE_100(0x64), GENRE_101(0x65), GENRE_102(0x66), GENRE_103(0x67),
	GENRE_104(0x68), GENRE_105(0x69), GENRE_106(0x6A), GENRE_107(0x6B), GENRE_108(0x6C), GENRE_109(0x6D), GENRE_110(0x6E), GENRE_111(0x6F),
	GENRE_112(0x70), GENRE_113(0x71), GENRE_114(0x72), GENRE_115(0x73), GENRE_116(0x74), GENRE_117(0x75), GENRE_118(0x76), GENRE_119(0x77),
	GENRE_120(0x78), GENRE_121(0x79), GENRE_122(0x7A), GENRE_123(0x7B), GENRE_124(0x7C), GENRE_125(0x7D), GENRE_126(0x7E), GENRE_127(0x7F),
	GENRE_128(0x80), GENRE_129(0x81), GENRE_130(0x82), GENRE_131(0x83), GENRE_132(0x84), GENRE_133(0x85), GENRE_134(0x86), GENRE_135(0x87),
	GENRE_136(0x88), GENRE_137(0x89), GENRE_138(0x8A), GENRE_139(0x8B), GENRE_140(0x8C), GENRE_141(0x8D), GENRE_142(0x8E), GENRE_143(0x8F),
	GENRE_144(0x90), GENRE_145(0x91), GENRE_146(0x92), GENRE_147(0x93), GENRE_148(0x94), GENRE_149(0x95), GENRE_150(0x96), GENRE_151(0x97),
	GENRE_152(0x98), GENRE_153(0x99), GENRE_154(0x9A), GENRE_155(0x9B), GENRE_156(0x9C), GENRE_157(0x9D), GENRE_158(0x9E), GENRE_159(0x9F),
	GENRE_160(0xA0), GENRE_161(0xA1), GENRE_162(0xA2), GENRE_163(0xA3), GENRE_164(0xA4), GENRE_165(0xA5), GENRE_166(0xA6), GENRE_167(0xA7),
	GENRE_168(0xA8), GENRE_169(0xA9), GENRE_170(0xAA), GENRE_171(0xAB), GENRE_172(0xAC), GENRE_173(0xAD), GENRE_174(0xAE), GENRE_175(0xAF),
	GENRE_176(0xB0), GENRE_177(0xB1), GENRE_178(0xB2), GENRE_179(0xB3), GENRE_180(0xB4), GENRE_181(0xB5), GENRE_182(0xB6), GENRE_183(0xB7),
	GENRE_184(0xB8), GENRE_185(0xB9), GENRE_186(0xBA), GENRE_187(0xBB), GENRE_188(0xBC), GENRE_189(0xBD), GENRE_190(0xBE), GENRE_191(0xBF),
	GENRE_192(0xC0), GENRE_193(0xC1), GENRE_194(0xC2), GENRE_195(0xC3), GENRE_196(0xC4), GENRE_197(0xC5), GENRE_198(0xC6), GENRE_199(0xC7),
	GENRE_200(0xC8), GENRE_201(0xC9), GENRE_202(0xCA), GENRE_203(0xCB), GENRE_204(0xCC), GENRE_205(0xCD), GENRE_206(0xCE), GENRE_207(0xCF),
	GENRE_208(0xD0), GENRE_209(0xD1), GENRE_210(0xD2), GENRE_211(0xD3), GENRE_212(0xD4), GENRE_213(0xD5), GENRE_214(0xD6), GENRE_215(0xD7),
	GENRE_216(0xD8), GENRE_217(0xD9), GENRE_218(0xDA), GENRE_219(0xDB), GENRE_220(0xDC), GENRE_221(0xDD), GENRE_222(0xDE), GENRE_223(0xDF),
	GENRE_224(0xE0), GENRE_225(0xE1), GENRE_226(0xE2), GENRE_227(0xE3), GENRE_228(0xE4), GENRE_229(0xE5), GENRE_230(0xE6), GENRE_231(0xE7),
	GENRE_232(0xE8), GENRE_233(0xE9), GENRE_234(0xEA), GENRE_235(0xEB), GENRE_236(0xEC), GENRE_237(0xED), GENRE_238(0xEE), GENRE_239(0xEF),
	GENRE_240(0xF0), GENRE_241(0xF1), GENRE_242(0xF2), GENRE_243(0xF3), GENRE_244(0xF4), GENRE_245(0xF5), GENRE_246(0xF6), GENRE_247(0xF7),
	GENRE_248(0xF8), GENRE_249(0xF9), GENRE_250(0xFA), GENRE_251(0xFB), GENRE_252(0xFC), GENRE_253(0xFD), GENRE_254(0xFE), GENRE_255(0xFF);

	public final static int NO_GENRE = GENRE_000.asInt();
	
	@SuppressWarnings("nls")
	private static final String[] NAMES = {	
		LocaleBundle.getString("CCMovieGenre.Genre000"), LocaleBundle.getString("CCMovieGenre.Genre001"), LocaleBundle.getString("CCMovieGenre.Genre002"), LocaleBundle.getString("CCMovieGenre.Genre003"), 
		LocaleBundle.getString("CCMovieGenre.Genre004"), LocaleBundle.getString("CCMovieGenre.Genre005"), LocaleBundle.getString("CCMovieGenre.Genre006"), LocaleBundle.getString("CCMovieGenre.Genre007"),
		LocaleBundle.getString("CCMovieGenre.Genre008"), LocaleBundle.getString("CCMovieGenre.Genre009"), LocaleBundle.getString("CCMovieGenre.Genre010"), LocaleBundle.getString("CCMovieGenre.Genre011"),
		LocaleBundle.getString("CCMovieGenre.Genre012"), LocaleBundle.getString("CCMovieGenre.Genre013"), LocaleBundle.getString("CCMovieGenre.Genre014"), LocaleBundle.getString("CCMovieGenre.Genre015"),
		LocaleBundle.getString("CCMovieGenre.Genre016"), LocaleBundle.getString("CCMovieGenre.Genre017"), LocaleBundle.getString("CCMovieGenre.Genre018"), LocaleBundle.getString("CCMovieGenre.Genre019"),
		LocaleBundle.getString("CCMovieGenre.Genre020"), LocaleBundle.getString("CCMovieGenre.Genre021"), LocaleBundle.getString("CCMovieGenre.Genre022"), LocaleBundle.getString("CCMovieGenre.Genre023"),
		LocaleBundle.getString("CCMovieGenre.Genre024"), LocaleBundle.getString("CCMovieGenre.Genre025"), LocaleBundle.getString("CCMovieGenre.Genre026"), LocaleBundle.getString("CCMovieGenre.Genre027"),
		LocaleBundle.getString("CCMovieGenre.Genre028"), LocaleBundle.getString("CCMovieGenre.Genre029"), LocaleBundle.getString("CCMovieGenre.Genre030"), LocaleBundle.getString("CCMovieGenre.Genre031"),
		LocaleBundle.getString("CCMovieGenre.Genre032"), LocaleBundle.getString("CCMovieGenre.Genre033"), LocaleBundle.getString("CCMovieGenre.Genre034"), LocaleBundle.getString("CCMovieGenre.Genre035"),
		LocaleBundle.getString("CCMovieGenre.Genre036"), LocaleBundle.getString("CCMovieGenre.Genre037"), LocaleBundle.getString("CCMovieGenre.Genre038"), LocaleBundle.getString("CCMovieGenre.Genre039"), 
		LocaleBundle.getString("CCMovieGenre.Genre040"), LocaleBundle.getString("CCMovieGenre.Genre041"), LocaleBundle.getString("CCMovieGenre.Genre042"), LocaleBundle.getString("CCMovieGenre.Genre043"),
		LocaleBundle.getString("CCMovieGenre.Genre044"), LocaleBundle.getString("CCMovieGenre.Genre045"), LocaleBundle.getString("CCMovieGenre.Genre046"), LocaleBundle.getString("CCMovieGenre.Genre047"), 
		LocaleBundle.getString("CCMovieGenre.Genre048"), LocaleBundle.getString("CCMovieGenre.Genre049"), LocaleBundle.getString("CCMovieGenre.Genre050"), LocaleBundle.getString("CCMovieGenre.Genre051"),
		LocaleBundle.getString("CCMovieGenre.Genre052"), LocaleBundle.getString("CCMovieGenre.Genre053"), LocaleBundle.getString("CCMovieGenre.Genre054"), LocaleBundle.getString("CCMovieGenre.Genre055"),
		LocaleBundle.getString("CCMovieGenre.Genre056"),
	};
	
	private int id;
	
	private static EnumWrapper<CCGenre> wrapper = new EnumWrapper<>(GENRE_000);

	CCGenre(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCGenre> getWrapper() {
		return wrapper;
	}

	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		if (id < NAMES.length) {
			return NAMES[id];
		} else {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.UnknownGenreFound", id)); //$NON-NLS-1$
			return "Unknown Genre"; //$NON-NLS-1$
		}
	}

	@Override
	public String[] getList() {
		return NAMES;
	}

	public static String[] getTrimmedList() {
		ArrayList<String> res = new ArrayList<>();
		for (String s : getWrapper().getList()) {
			if (s.equals(LocaleBundle.getString("CCMovieGenre.Genre000"))) { //$NON-NLS-1$
				res.add(" "); //$NON-NLS-1$
			} else if (!s.isEmpty()) {
				res.add(s);
			}
		}
		return res.toArray(new String[0]);
	}

	@SuppressWarnings("nls")
	public static CCGenre[] parseFromIMDBName(String ng) {
		ng = ng.toLowerCase();

		if (ng.equals("katastrophenfilm"))       return new CCGenre[]{CCGenre.GENRE_001};
		if (ng.equals("road movie"))             return new CCGenre[]{CCGenre.GENRE_002};
		if (ng.equals("western"))                return new CCGenre[]{CCGenre.GENRE_003};
		if (ng.equals("italo-western"))          return new CCGenre[]{CCGenre.GENRE_004};
		if (ng.equals("heimatfilm"))             return new CCGenre[]{CCGenre.GENRE_005};
		if (ng.equals("thriller"))               return new CCGenre[]{CCGenre.GENRE_006};
		if (ng.equals("actionthriller"))         return new CCGenre[]{CCGenre.GENRE_007};
		if (ng.equals("psychothriller"))         return new CCGenre[]{CCGenre.GENRE_008};
		if (ng.equals("sci-fi"))                 return new CCGenre[]{CCGenre.GENRE_009};
		if (ng.equals("science-fiction"))        return new CCGenre[]{CCGenre.GENRE_009};
		if (ng.equals("science fiction"))        return new CCGenre[]{CCGenre.GENRE_009};
		if (ng.equals("comedy"))                 return new CCGenre[]{CCGenre.GENRE_010};
		if (ng.equals("komödie"))                return new CCGenre[]{CCGenre.GENRE_010};
		if (ng.equals("slapstick-comedy"))       return new CCGenre[]{CCGenre.GENRE_011};
		if (ng.equals("screwball-comedy"))       return new CCGenre[]{CCGenre.GENRE_012};
		if (ng.equals("gangsterfilm"))           return new CCGenre[]{CCGenre.GENRE_013};
		if (ng.equals("krimi"))                  return new CCGenre[]{CCGenre.GENRE_014};
		if (ng.equals("crime"))                  return new CCGenre[]{CCGenre.GENRE_014};
		if (ng.equals("kriegsfilm"))             return new CCGenre[]{CCGenre.GENRE_015};
		if (ng.equals("war"))                    return new CCGenre[]{CCGenre.GENRE_015};
		if (ng.equals("krieg"))                  return new CCGenre[]{CCGenre.GENRE_015};
		if (ng.equals("porno"))                  return new CCGenre[]{CCGenre.GENRE_016};
		if (ng.equals("softporno"))              return new CCGenre[]{CCGenre.GENRE_017};
		if (ng.equals("hardcore-porno"))         return new CCGenre[]{CCGenre.GENRE_018};
		if (ng.equals("action"))                 return new CCGenre[]{CCGenre.GENRE_019};
		if (ng.equals("trickfilm"))              return new CCGenre[]{CCGenre.GENRE_020};
		if (ng.equals("zeichentrickfilm"))       return new CCGenre[]{CCGenre.GENRE_021};
		if (ng.equals("zeichentrick"))           return new CCGenre[]{CCGenre.GENRE_021};
		if (ng.equals("anime"))                  return new CCGenre[]{CCGenre.GENRE_022};
		if (ng.equals("stop-motion-film"))       return new CCGenre[]{CCGenre.GENRE_023};
		if (ng.equals("puppentrickfilm"))        return new CCGenre[]{CCGenre.GENRE_024};
		if (ng.equals("claymation"))             return new CCGenre[]{CCGenre.GENRE_025};
		if (ng.equals("computeranimationsfilm")) return new CCGenre[]{CCGenre.GENRE_026};
		if (ng.equals("animation"))              return new CCGenre[]{CCGenre.GENRE_026};
		if (ng.equals("martial-arts-film"))      return new CCGenre[]{CCGenre.GENRE_027};
		if (ng.equals("samuraifilm"))            return new CCGenre[]{CCGenre.GENRE_028};
		if (ng.equals("horror"))                 return new CCGenre[]{CCGenre.GENRE_029};
		if (ng.equals("slasher"))                return new CCGenre[]{CCGenre.GENRE_030};
		if (ng.equals("teenhorror"))             return new CCGenre[]{CCGenre.GENRE_031};
		if (ng.equals("creature"))               return new CCGenre[]{CCGenre.GENRE_032};
		if (ng.equals("comingofage"))            return new CCGenre[]{CCGenre.GENRE_033};
		if (ng.equals("dokumentarfilm"))         return new CCGenre[]{CCGenre.GENRE_034};
		if (ng.equals("documentary"))            return new CCGenre[]{CCGenre.GENRE_034};
		if (ng.equals("abenteuer"))              return new CCGenre[]{CCGenre.GENRE_035};
		if (ng.equals("adventure"))              return new CCGenre[]{CCGenre.GENRE_035};
		if (ng.equals("action & adventure"))     return new CCGenre[]{CCGenre.GENRE_035};
		if (ng.equals("romanze"))                return new CCGenre[]{CCGenre.GENRE_036};
		if (ng.equals("romance"))                return new CCGenre[]{CCGenre.GENRE_036};
		if (ng.equals("mystery"))                return new CCGenre[]{CCGenre.GENRE_037};
		if (ng.equals("fantasy"))                return new CCGenre[]{CCGenre.GENRE_038};
		if (ng.equals("familie"))                return new CCGenre[]{CCGenre.GENRE_039};
		if (ng.equals("family"))                 return new CCGenre[]{CCGenre.GENRE_039};
		if (ng.equals("drama"))                  return new CCGenre[]{CCGenre.GENRE_040};
		if (ng.equals("anti-kriegsfilm"))        return new CCGenre[]{CCGenre.GENRE_041};
		if (ng.equals("biographie"))             return new CCGenre[]{CCGenre.GENRE_042};
		if (ng.equals("biography"))              return new CCGenre[]{CCGenre.GENRE_042};
		if (ng.equals("sport"))                  return new CCGenre[]{CCGenre.GENRE_043};
		if (ng.equals("musical"))                return new CCGenre[]{CCGenre.GENRE_044};
		if (ng.equals("musik"))                  return new CCGenre[]{CCGenre.GENRE_045};
		if (ng.equals("music"))                  return new CCGenre[]{CCGenre.GENRE_045};
		if (ng.equals("history"))                return new CCGenre[]{CCGenre.GENRE_046};
		if (ng.equals("film-noir"))              return new CCGenre[]{CCGenre.GENRE_047};
		if (ng.equals("kids"))                   return new CCGenre[]{CCGenre.GENRE_048};
		if (ng.equals("kinder"))                 return new CCGenre[]{CCGenre.GENRE_048};
		if (ng.equals("kinderfilm"))             return new CCGenre[]{CCGenre.GENRE_048};
		if (ng.equals("soap"))                   return new CCGenre[]{CCGenre.GENRE_049};
		if (ng.equals("soap opera"))             return new CCGenre[]{CCGenre.GENRE_049};
		if (ng.equals("seifenoper"))             return new CCGenre[]{CCGenre.GENRE_049};

		CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotParseGenre", ng));
		return new CCGenre[]{};
	}

	public static CCGenre[] parseFromTMDbID(int id) {
		switch (id)
		{
			case 28:    return new CCGenre[]{CCGenre.GENRE_019};                    // Action
			case 12:    return new CCGenre[]{CCGenre.GENRE_035};                    // Adventure
			case 16:    return new CCGenre[]{CCGenre.GENRE_026};                    // Animation
			case 35:    return new CCGenre[]{CCGenre.GENRE_010};                    // Comedy
			case 80:    return new CCGenre[]{CCGenre.GENRE_014};                    // Crime
			case 99:    return new CCGenre[]{CCGenre.GENRE_034};                    // Documentary
			case 18:    return new CCGenre[]{CCGenre.GENRE_040};                    // Drama
			case 10751: return new CCGenre[]{CCGenre.GENRE_039};                    // Family
			case 14:    return new CCGenre[]{CCGenre.GENRE_038};                    // Fantasy
			case 10769: return new CCGenre[]{};                                     // Foreign
			case 36:    return new CCGenre[]{CCGenre.GENRE_046};                    // History
			case 27:    return new CCGenre[]{CCGenre.GENRE_029};                    // Horror
			case 10402: return new CCGenre[]{CCGenre.GENRE_045};                    // Music
			case 9648:  return new CCGenre[]{CCGenre.GENRE_037};                    // Mystery
			case 10749: return new CCGenre[]{CCGenre.GENRE_036};                    // Romance
			case 878:   return new CCGenre[]{CCGenre.GENRE_009};                    // Science Fiction
			case 10770: return new CCGenre[]{};                                     // TV Movie
			case 53:    return new CCGenre[]{CCGenre.GENRE_006};                    // Thriller
			case 10752: return new CCGenre[]{CCGenre.GENRE_015};                    // War
			case 37:    return new CCGenre[]{CCGenre.GENRE_037};                    // Western
			case 10759: return new CCGenre[]{CCGenre.GENRE_035, CCGenre.GENRE_035}; // Action & Adventure
			case 10762: return new CCGenre[]{CCGenre.GENRE_048};                    // Kids
			case 10763: return new CCGenre[]{};                                     // News
			case 10764: return new CCGenre[]{};                                     // Reality
			case 10765: return new CCGenre[]{CCGenre.GENRE_009, CCGenre.GENRE_038}; // Sci-Fi & Fantasy
			case 10766: return new CCGenre[]{CCGenre.GENRE_049};                    // Soap
			case 10767: return new CCGenre[]{};                                     // Talk
			case 10768: return new CCGenre[]{CCGenre.GENRE_015};                    // War & Politics

			default:
				CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotParseGenreID", id)); //$NON-NLS-1$
				return new CCGenre[]{};
		}

	}

	@SuppressWarnings("nls")
	public static CCGenre[] parseFromMAL(String txt) {
		if (txt.equalsIgnoreCase("Action"))        return new CCGenre[]{CCGenre.GENRE_019};
		if (txt.equalsIgnoreCase("Adventure"))     return new CCGenre[]{CCGenre.GENRE_035};
		if (txt.equalsIgnoreCase("Cars"))          return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Comedy"))        return new CCGenre[]{CCGenre.GENRE_010};
		if (txt.equalsIgnoreCase("Dementia"))      return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Demons"))        return new CCGenre[]{CCGenre.GENRE_038};
		if (txt.equalsIgnoreCase("Drama"))         return new CCGenre[]{CCGenre.GENRE_040};
		if (txt.equalsIgnoreCase("Ecchi"))         return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Fantasy"))       return new CCGenre[]{CCGenre.GENRE_038};
		if (txt.equalsIgnoreCase("Game"))          return new CCGenre[]{CCGenre.GENRE_043};
		if (txt.equalsIgnoreCase("Harem"))         return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Hentai"))        return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Historical"))    return new CCGenre[]{CCGenre.GENRE_046};
		if (txt.equalsIgnoreCase("Horror"))        return new CCGenre[]{CCGenre.GENRE_029};
		if (txt.equalsIgnoreCase("Kids"))          return new CCGenre[]{CCGenre.GENRE_048};
		if (txt.equalsIgnoreCase("Magic"))         return new CCGenre[]{CCGenre.GENRE_038};
		if (txt.equalsIgnoreCase("Martial Arts"))  return new CCGenre[]{CCGenre.GENRE_027};
		if (txt.equalsIgnoreCase("Mecha"))         return new CCGenre[]{CCGenre.GENRE_054};
		if (txt.equalsIgnoreCase("Military"))      return new CCGenre[]{CCGenre.GENRE_015};
		if (txt.equalsIgnoreCase("Music"))         return new CCGenre[]{CCGenre.GENRE_044};
		if (txt.equalsIgnoreCase("Mystery"))       return new CCGenre[]{CCGenre.GENRE_037};
		if (txt.equalsIgnoreCase("Parody"))        return new CCGenre[]{CCGenre.GENRE_055};
		if (txt.equalsIgnoreCase("Police"))        return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Psychological")) return new CCGenre[]{CCGenre.GENRE_008};
		if (txt.equalsIgnoreCase("Romance"))       return new CCGenre[]{CCGenre.GENRE_036};
		if (txt.equalsIgnoreCase("Samurai"))       return new CCGenre[]{CCGenre.GENRE_027};
		if (txt.equalsIgnoreCase("School"))        return new CCGenre[]{CCGenre.GENRE_053};
		if (txt.equalsIgnoreCase("Sci-Fi"))        return new CCGenre[]{CCGenre.GENRE_009};
		if (txt.equalsIgnoreCase("Shoujo"))        return new CCGenre[]{CCGenre.GENRE_052};
		if (txt.equalsIgnoreCase("Shoujo Ai"))     return new CCGenre[]{CCGenre.GENRE_052};
		if (txt.equalsIgnoreCase("Shounen"))       return new CCGenre[]{CCGenre.GENRE_051};
		if (txt.equalsIgnoreCase("Shounen Ai"))    return new CCGenre[]{CCGenre.GENRE_051};
		if (txt.equalsIgnoreCase("Slice of Life")) return new CCGenre[]{CCGenre.GENRE_050};
		if (txt.equalsIgnoreCase("Space"))         return new CCGenre[]{CCGenre.GENRE_009};
		if (txt.equalsIgnoreCase("Sports"))        return new CCGenre[]{CCGenre.GENRE_043};
		if (txt.equalsIgnoreCase("Super Power"))   return new CCGenre[]{CCGenre.GENRE_038};
		if (txt.equalsIgnoreCase("Supernatural"))  return new CCGenre[]{CCGenre.GENRE_038};
		if (txt.equalsIgnoreCase("Thriller"))      return new CCGenre[]{CCGenre.GENRE_006};
		if (txt.equalsIgnoreCase("Vampire"))       return new CCGenre[]{CCGenre.GENRE_038};
		if (txt.equalsIgnoreCase("Yaoi"))          return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Yuri"))          return new CCGenre[]{};

		return new CCGenre[]{};
	}

	@SuppressWarnings("nls")
	public static CCGenre[] parseFromAniList(String txt) {
		if (txt.equalsIgnoreCase("Action"))          return new CCGenre[]{CCGenre.GENRE_019};
		if (txt.equalsIgnoreCase("Adventure"))       return new CCGenre[]{CCGenre.GENRE_035};
		if (txt.equalsIgnoreCase("Comedy"))          return new CCGenre[]{CCGenre.GENRE_010};
		if (txt.equalsIgnoreCase("Drama"))           return new CCGenre[]{CCGenre.GENRE_040};
		if (txt.equalsIgnoreCase("Ecchi"))           return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Fantasy"))         return new CCGenre[]{CCGenre.GENRE_038};
		if (txt.equalsIgnoreCase("Hentai"))          return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Horror"))          return new CCGenre[]{CCGenre.GENRE_029};
		if (txt.equalsIgnoreCase("Mahou Shoujo"))    return new CCGenre[]{};
		if (txt.equalsIgnoreCase("Mecha"))           return new CCGenre[]{CCGenre.GENRE_054};
		if (txt.equalsIgnoreCase("Music"))           return new CCGenre[]{CCGenre.GENRE_044};
		if (txt.equalsIgnoreCase("Mystery"))         return new CCGenre[]{CCGenre.GENRE_037};
		if (txt.equalsIgnoreCase("Psychological"))   return new CCGenre[]{CCGenre.GENRE_008};
		if (txt.equalsIgnoreCase("Romance"))         return new CCGenre[]{CCGenre.GENRE_036};
		if (txt.equalsIgnoreCase("Sci-Fi"))          return new CCGenre[]{CCGenre.GENRE_009};
		if (txt.equalsIgnoreCase("Slice of Life"))   return new CCGenre[]{CCGenre.GENRE_050};
		if (txt.equalsIgnoreCase("Sports"))          return new CCGenre[]{CCGenre.GENRE_043};
		if (txt.equalsIgnoreCase("Supernatural"))    return new CCGenre[]{CCGenre.GENRE_038};
		if (txt.equalsIgnoreCase("Thriller"))        return new CCGenre[]{CCGenre.GENRE_006};

		return new CCGenre[]{};
	}

	public boolean isEmpty() {
		return asInt() == NO_GENRE;
	}

	public boolean isValid() {
		return id >= 0 && id < CCGenre.values().length && id < NAMES.length;
	}
	
	public static Comparator<CCGenre> getTextComparator() {
		return Comparator.comparing(CCGenre::asString);
	}
	
	public static Comparator<CCGenre> getIDComparator() {
		return Comparator.comparingInt(CCGenre::asInt);
	}

	@Override
	public CCGenre[] evalues() {
		return CCGenre.values();
	}
}
