package de.jClipCorn.test;

import de.jClipCorn.features.databaseErrors.DatabaseErrorType;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.stream.CCStreams;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
@RunWith(JUnitParamsRunner.class)
public class TestTranslations extends ClipCornBaseTest
{
	private static final Pattern REGEX_LB_1 = Pattern.compile("LocaleBundle\\.getString\\(\"(.*?)\"\\)");
	private static final Pattern REGEX_LB_2 = Pattern.compile("LocaleBundle\\.getDeformattedString\\(\"(.*?)\"\\)");
	private static final Pattern REGEX_LB_3 = Pattern.compile("LocaleBundle\\.getFormattedString\\(\"(.*?)\"");
	private static final Pattern REGEX_LB_4 = Pattern.compile("LocaleBundle\\.getMFFormattedString\\(\"(.*?)\"");
	private static final Pattern REGEX_LB_5 = Pattern.compile("LocaleBundle\\.getStringOrDefault\\(\"(.*?)\"");

	private static final Pattern REGEX_JFD_1 = Pattern.compile("new\\s*FormMessage\\(\\s*null,\\s*\"(.*?)\"\\s*\\)");

	private Collection<Object[]> javaFiles()
	{
		return FilesystemUtils
				.getWorkingDirectory()
				.listRecursiveBreadthFirst()
				.filter(p -> p.getExtension().equalsIgnoreCase("java"))
				.map(p -> new Object[]{p.toAbsolutePathString().replace(FilesystemUtils.getWorkingDirectory().toAbsolutePathString()+"/", ""), p})
				.toList();
	}

	private Collection<Object[]> jfdFiles()
	{
		return FilesystemUtils
				.getWorkingDirectory()
				.listRecursiveBreadthFirst()
				.filter(p -> p.getExtension().equalsIgnoreCase("jfd"))
				.map(p -> new Object[]{p.toAbsolutePathString().replace(FilesystemUtils.getWorkingDirectory().toAbsolutePathString()+"/", ""), p})
				.toList();
	}

	@Test
	@Parameters(method = "javaFiles")
	@TestCaseName("findMissingJavaTranslations[ {0} ]")
	public void findMissingJavaTranslations(String _f, FSPath file) throws IOException
	{
		var loc1 = loadFromRes("/de/jClipCorn/gui/localization/locale.properties");
		var loc2 = loadFromRes("/de/jClipCorn/gui/localization/locale_de_DE.properties");
		var loc3 = loadFromRes("/de/jClipCorn/gui/localization/locale_dl_DL.properties");
		var loc4 = loadFromRes("/de/jClipCorn/gui/localization/locale_en_US.properties");
		var locs = new Properties[]{ loc1, loc2, loc3, loc4 };

		var rexs = new Pattern[]{REGEX_LB_1, REGEX_LB_2, REGEX_LB_3, REGEX_LB_4, REGEX_LB_5};

		var txt = file.readAsUTF8TextFile();

		for (var rex : rexs)
		{
			for (var mr : rex.matcher(txt).results().collect(Collectors.toList()))
			{
				var ident = mr.group(1);

				if (ident.equals("Settingsframe.tabbedPnl.")) continue;

				for (int i = 0; i < locs.length; i++)
				{
					var loc = locs[i];
					assertTrue(String.format("translation of '%s' in %d must exist", ident, i), existsAndNotEmpty(loc, ident));
				}
			}
		}
	}

	@Test
	@Parameters(method = "jfdFiles")
	@TestCaseName("findMissingJGoodiesTranslations[ {0} ]")
	public void findMissingJGoodiesTranslations(String _f, FSPath file) throws IOException
	{
		var loc1 = loadFromRes("/de/jClipCorn/gui/localization/locale.properties");
		var loc2 = loadFromRes("/de/jClipCorn/gui/localization/locale_de_DE.properties");
		var loc3 = loadFromRes("/de/jClipCorn/gui/localization/locale_dl_DL.properties");
		var loc4 = loadFromRes("/de/jClipCorn/gui/localization/locale_en_US.properties");
		var locs = new Properties[]{ loc1, loc2, loc3, loc4 };

		var txt = file.readAsUTF8TextFile();

		for (var mr : REGEX_JFD_1.matcher(txt).results().collect(Collectors.toList()))
		{
			var ident = mr.group(1);

			for (int i = 0; i < locs.length; i++)
			{
				var loc = locs[i];
				assertTrue(String.format("translation of '%s' in %d must exist", ident, i), existsAndNotEmpty(loc, ident));
			}
		}
	}

	@Test
	public void testNoMissingResourceBundleEntries() throws IOException
	{
		var loc1 = loadFromRes("/de/jClipCorn/gui/localization/locale.properties");
		var loc2 = loadFromRes("/de/jClipCorn/gui/localization/locale_de_DE.properties");
		var loc3 = loadFromRes("/de/jClipCorn/gui/localization/locale_dl_DL.properties");
		var loc4 = loadFromRes("/de/jClipCorn/gui/localization/locale_en_US.properties");
		var locs = new Properties[]{ loc1, loc2, loc3, loc4 };

		for (int i = 0; i < locs.length; i++) {
			for (var key : locs[i].keySet()) {
				for (int j = 0; j < locs.length; j++) {
					var trans = locs[j].get(key);
					assertNotNull(String.format("translation of %s in %d must exist", key, j), trans);
					assertNotEquals(String.format("translation of %s in %d must not be empty", key, j), trans, Str.Empty);
					assertTrue(String.format("translation of %s in %d must not be whitespace", key, j), existsAndNotEmpty(locs[j], key));
				}
			}
		}
	}

	@Test
	public void testNoInvalidEncoding() throws IOException
	{
		var rex = Pattern.compile("^(\\s|[A-Za-z0-9]|[!-/]|[:-@]|[\\[-`]|[{-~]|[ÄÖÜäöüß]|[Ø])*$");

		var loc1 = loadFromRes("/de/jClipCorn/gui/localization/locale.properties");
		var loc2 = loadFromRes("/de/jClipCorn/gui/localization/locale_de_DE.properties");
		var loc3 = loadFromRes("/de/jClipCorn/gui/localization/locale_dl_DL.properties");
		var loc4 = loadFromRes("/de/jClipCorn/gui/localization/locale_en_US.properties");
		var locs = new Properties[]{ loc1, loc2, loc3, loc4 };

		for (int i = 0; i < locs.length; i++)
		{
			var loc = locs[i];

			for (var e : loc.entrySet())
			{
				var v = (String)e.getValue();
				assertTrue(String.format("translation of %s in %d contains invalid characters: '%s'", e.getKey(), i, v), rex.matcher(v).matches());
			}
		}
	}

	@Test
	public void testDatabaseErrorTypeTranslations() throws IllegalAccessException, IOException {
		var loc1 = loadFromRes("/de/jClipCorn/gui/localization/locale.properties");
		var loc2 = loadFromRes("/de/jClipCorn/gui/localization/locale_de_DE.properties");
		var loc3 = loadFromRes("/de/jClipCorn/gui/localization/locale_dl_DL.properties");
		var loc4 = loadFromRes("/de/jClipCorn/gui/localization/locale_en_US.properties");
		var locs = new Properties[]{ loc1, loc2, loc3, loc4 };

		var fields = CCStreams
				.iterate(DatabaseErrorType.class.getDeclaredFields())
				.filter(f -> Modifier.isPublic(f.getModifiers()))
				.filter(f -> Modifier.isFinal(f.getModifiers()))
				.filter(f -> Modifier.isStatic(f.getModifiers()))
				.filter(f -> f.getType().equals(DatabaseErrorType.class));

		for (var f : fields)
		{
			var det = (DatabaseErrorType)f.get(null);

			for (int i = 0; i < locs.length; i++)
			{
				var loc = locs[i];

				assertTrue(String.format("translation of %s in DatabaseErrorType[%d][Error] must exist", det.getType(), i), existsAndNotEmpty(loc, String.format("CheckDatabaseDialog.Error.ERR_%02d", det.getType())));
				assertTrue(String.format("translation of %s in DatabaseErrorType[%d][Errornames] must exist", det.getType(), i), existsAndNotEmpty(loc, String.format("CheckDatabaseDialog.Errornames.ERR_%02d", det.getType())));
			}
		}
	}

	@Test
	public void testEnumStrings() throws IOException, ClassNotFoundException, IllegalAccessException
	{
		var rex1 = Pattern.compile("^package ([A-Za-z0-9.]+)$");
		var rex2 = Pattern.compile("^public enum ([A-Za-z0-9_]) implements ContinoousEnum.*$");

		var jfiles = FilesystemUtils
				.getWorkingDirectory()
				.append("src", "main")
				.listRecursiveBreadthFirst()
				.filter(p -> p.getExtension().equalsIgnoreCase("java"))
				.toList();

		for (var file : jfiles) {

			var txt = file.readAsUTF8TextFile();

			var m1 = rex1.matcher(txt);
			var m2 = rex2.matcher(txt);

			if (!m1.matches()) continue;
			if (!m2.matches()) continue;

			var pkg = m1.group(1);
			var cls = m2.group(1);

			var javaclass = Class.forName(pkg + "." + cls);

			var fields = CCStreams
					.iterate(javaclass.getDeclaredFields())
					.filter(f -> Modifier.isPublic(f.getModifiers()))
					.filter(f -> Modifier.isFinal(f.getModifiers()))
					.filter(f -> Modifier.isStatic(f.getModifiers()))
					.filter(f -> f.getType().equals(javaclass));

			for (var f : fields)
			{
				var obj = (ContinoousEnum<?>)f.get(null);

				assertFalse(String.format("translation of Enum::asString(%s | %s) must exist", cls, f.getName()), Str.isNullOrWhitespace(obj.asString()));
			}
		}
	}

	@Test
	public void testUserDataProblemTranslations() throws IOException, IllegalAccessException {
		var loc1 = loadFromRes("/de/jClipCorn/gui/localization/locale.properties");
		var loc2 = loadFromRes("/de/jClipCorn/gui/localization/locale_de_DE.properties");
		var loc3 = loadFromRes("/de/jClipCorn/gui/localization/locale_dl_DL.properties");
		var loc4 = loadFromRes("/de/jClipCorn/gui/localization/locale_en_US.properties");
		var locs = new Properties[]{ loc1, loc2, loc3, loc4 };

		var fields = CCStreams
				.iterate(UserDataProblem.class.getDeclaredFields())
				.filter(f -> Modifier.isPublic(f.getModifiers()))
				.filter(f -> Modifier.isFinal(f.getModifiers()))
				.filter(f -> Modifier.isStatic(f.getModifiers()))
				.filter(f -> f.getType().equals(int.class));

		for (var f : fields)
		{
			var pid = (int)f.get(null);
			var ident = String.format("UserDataErrors.ERROR_%02d", pid);

			for (int i = 0; i < locs.length; i++)
			{
				var loc = locs[i];

				assertTrue(String.format("translation of UserDataErrors<%d>[%s] in %d must exist", pid, f.getName(), i), existsAndNotEmpty(loc, ident));
			}
		}
	}

	private Properties loadFromRes(String rp) throws IOException {
		var txt = SimpleFileUtils.readTextResource(rp, TestTranslations.class);
		var prp = new Properties();
		prp.load(new StringReader(txt));
		return prp;
	}

	private boolean existsAndNotEmpty(Properties loc, Object key) {
		if (!loc.containsKey(key)) return false;
		if (Str.isNullOrWhitespace((String)loc.get(key))) return false;
		return true;
	}
}
