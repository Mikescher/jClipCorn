---
name: add-language
description: Add a new spoken-language entry to jClipCorn and the ClipCornWebPanel (the CCDBLanguage enum, its flag icon, locale strings, MediaInfo/FFProbe parsing aliases, and the web panel's icon + LANGUAGES list). Use when asked to add a language, resolve an "Unknown audio language" warning/log, teach the metadata scanner to recognize a language code, or add a flag for a language.
---

# Add a new language to jClipCorn

A "language" in jClipCorn is a value of the `CCDBLanguage` enum. Adding one touches **6 places in jClipCorn** that must stay in sync: a flag icon, `Resources.java`, `CCDBLanguage.java`, the 4 locale files, and the parser in `MetadataRunner.java` — **plus the sibling `ClipCornWebPanel` repo** (its icon + language list). Skipping any one breaks the build, a test, metadata scanning, or the web panel display.

Look at prior art before starting — these commits each add languages the exact way this skill describes:
`74844ad` (2 langs), `062bc5c` (8 langs), `f4e8850` (Kannada), `a9b64f5` (Basque).

## Step 0 — pre-checks: are the external dirs mounted?

Two directories live outside the repo and may be unmounted. Check **both** before touching anything, and **stop and ask the user to mount** whichever is missing (do not fabricate icons or skip the web panel):

```bash
# 1) FatCow icon set — source of the flag PNGs (note the literal " [4000]" in the name)
test -d "/home/mike/Programming/_Global/Iconsets/FatCow [4000]/16x16" && echo "ICONSET: MOUNTED" || echo "ICONSET: MISSING"

# 2) ClipCornWebPanel — sibling repo that also renders languages
test -d "../ClipCornWebPanel/src" && echo "WEBPANEL: MOUNTED" || echo "WEBPANEL: MISSING"
```

Flag PNGs come from `…/FatCow [4000]/16x16/flag_<country>.png`. Don't download/fabricate flags — the whole set must be present so the chosen flag matches the project's visual style.

## Step 1 — decide language(s) and pick a flag country

We add a **language**, but the icon is a **flag**, and flags belong to **countries**. Pick the "primary" country for the language: the one country where it is *the* official/dominant language (Albania→Albanian, Georgia→Georgian). If it is official in several, pick the most iconic / most populous home country.

For each language gather:

| field | source | example (Kazakh) |
|-------|--------|------------------|
| ISO 639-1 (2-letter) | wikipedia ISO 639-1 list | `kk` |
| ISO 639-2/B and /T (3-letter) | loc.gov iso639-2 code list | `kaz` |
| English name | | `Kazakh` |
| German name | | `Kasachisch` |
| native name(s) | wikipedia | `Қазақ тілі`, `Qazaq tili` |
| primary country flag | FatCow `flag_<country>.png` | `flag_kazakhstan.png` |

References: <https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes>, <https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes>, <https://www.loc.gov/standards/iso639-2/php/code_list.php>.

### Is it really a *new* language, or a variant of an existing one?

Written variants / macrolanguage members often should map to an **existing** enum value, not a new one. Example: Norwegian **Nynorsk** (`nn`) and **Bokmål** (`nb`) are both stored as the single `NORWEGIAN` entry — the fix for an unknown `nn` was just adding parser aliases pointing at `CCDBLanguage.NORWEGIAN`, **not** a new enum value or icon. When in doubt (Nynorsk, Cantonese vs Chinese, Farsi variants…), check whether the base language already exists and prefer an alias. Ask the user if it's genuinely ambiguous.

## Step 2 — find the next id

The enum ids are contiguous. Find the current maximum:

```bash
grep -nE '^\t[A-Z].*\([0-9]+, "' src/main/de/jClipCorn/database/databaseElement/columnTypes/CCDBLanguage.java | tail -1
```

New languages take `maxId+1, maxId+2, …`. The id is also the icon number (`language_<id>`).

> ⚠️ **64-language boundary.** `CCDBLanguage.asBitMask()` is `1L << id`, which only holds 64 bits (ids 0–63). Persistence is **not** affected — the DB stores the Language column as a JSON array of int ids (`asJSONArray`/`fromJSONArray`, a `VARCHAR`), so ids ≥ 64 save/load fine. But `asBitMask` (and thus `CCDBLanguageSet.compare()`'s sort key + `Migration_11_12`) collides `id 64` with `id 0`. Adding an id-64 language works, but note this in your summary. Going meaningfully past 64 languages would require replacing the bitmask/`compare()` with an id-list comparison first.

## Step 3 — the flag icon

Copy the country flag into the language-icon folder as `language_<id>_16x16.png`:

```bash
D="/home/mike/Programming/_Global/Iconsets/FatCow [4000]/16x16"
R="res/icons/table/language"
cp "$D/flag_kazakhstan.png" "$R/language_62_16x16.png"
file "$R/language_62_16x16.png"   # expect: PNG image data, 16 x 16
```

The `_16x16` file is the checked-in source; the build resolves the `/icons/table/language/language_<id>.png` reference used in code — just mirror the existing pattern, no extra wiring.

## Step 4 — `Resources.java`

`src/main/de/jClipCorn/gui/resources/Resources.java`. Two edits:

1. Declare the ref next to the others:
   ```java
   public final static IconRef ICN_TABLE_LANGUAGE_62 		= register16x16Icon("/icons/table/language/language_62.png");
   ```
2. Append it to the `ICN_TABLE_LANGUAGE[]` array (keep the trailing comma and row wrapping).

## Step 5 — `CCDBLanguage.java`

`src/main/de/jClipCorn/database/databaseElement/columnTypes/CCDBLanguage.java`. Add an enum constant (the previous last constant's `;` moves down). Constructor is `(int id, String shortName, String longName, String localeKey, IconRef icon)` — **5 args**:

```java
	KAZAKH    (62, "KAZ", "Kazakh",     "CCMovieLanguage.Kazakh",     Resources.ICN_TABLE_LANGUAGE_62),
```

- `shortName` = 3-letter ISO 639-2 code (uppercase), and it **must be unique** among all entries (`findByShortString` relies on it).
- `localeKey` = `CCMovieLanguage.<EnglishName>` — this exact key must exist in every locale file (step 6).

## Step 6 — locale files (all four)

`res/de/jClipCorn/gui/localization/`. Add the key `CCMovieLanguage.<EnglishName>` to **every** file, right after the previous language's line:

- `locale.properties` → **German** value
- `locale_de_DE.properties` → **German** value
- `locale_dl_DL.properties` → **German** value
- `locale_en_US.properties` → **English** value

```properties
# locale.properties / locale_de_DE.properties / locale_dl_DL.properties
CCMovieLanguage.Kazakh     = Kasachisch
# locale_en_US.properties
CCMovieLanguage.Kazakh     = Kazakh
```

`TestTranslations` enforces: every key exists & is non-empty in **all four** files (`testNoMissingResourceBundleEntries`), and every value matches an ASCII/German-umlaut charset (`testNoInvalidEncoding` — allows `A–Z a–z 0–9`, punctuation, `ÄÖÜäöüßØ` only). So **keep locale *values* ASCII/German** — native scripts go in the parser (step 7), never here.

## Step 7 — parser aliases in `MetadataRunner.java`

`src/main/de/jClipCorn/features/metadata/impl/MetadataRunner.java`, function `getLanguageOrNullFromIdent(...)` (a long `if (langval.equalsIgnoreCase(...)) return …;` chain ending in `return null;`). Add a blank-line-separated block **before `return null;`**, mirroring the existing entries. Be generous — MediaInfo/FFProbe/MP4Box emit the language in many forms, so cover:

- ISO 639-1, plus 639-2/B **and** /T (they differ for many langs, e.g. `alb`/`sqi`, `arm`/`hye`, `geo`/`kat`)
- English name and German name
- native name, romanized **and** in native script

```java
		if (langval.equalsIgnoreCase("kk"))                              return CCDBLanguage.KAZAKH;
		if (langval.equalsIgnoreCase("kaz"))                             return CCDBLanguage.KAZAKH;
		if (langval.equalsIgnoreCase("Kazakh"))                          return CCDBLanguage.KAZAKH;
		if (langval.equalsIgnoreCase("Kasachisch"))                      return CCDBLanguage.KAZAKH;
		if (langval.equalsIgnoreCase("Qazaq tili"))                      return CCDBLanguage.KAZAKH;
		if (langval.equalsIgnoreCase("Қазақ тілі")) return CCDBLanguage.KAZAKH; // Қазақ тілі
```

Notes:
- The source is compiled as **UTF-8** (`compileJava.options.encoding=UTF-8`), so native-script literals compile. Some legacy CJK entries use `\uXXXX` escapes instead — either is fine; escapes keep the `return` column aligned.
- If an "Unknown audio language" log gives you the exact strings the scanner saw (e.g. `['kk' | 'Kazak Tini']`), add **those literal strings** as aliases too — they're real-world observed values.
- For a **variant mapped to an existing language** (step 1), add the aliases pointing at the existing constant (e.g. `nn`, `Nynorsk`, `Norwegian Nynorsk` → `CCDBLanguage.NORWEGIAN`) near that language's existing block — no other steps needed.

## Step 8 — build & test

```bash
# fast compile check (host default java is too new; the build needs 17)
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :compileJava --console=plain

# full suite (runs in Docker, self-contained — includes TestTranslations + metadata tests)
JAVA_HOME=/usr/lib/jvm/java-17-openjdk make run-tests
```

The metadata scanner tests are generated from fixtures in `testres/media/demo_*` (`MAX_ID` in `TestMetadataRunner_Init`). Adding languages the fixtures don't use won't touch them; if a fixture *does* contain the new language, its previously-`assertOptError` assertion must be regenerated (see the commented `initMetadataRunner__*` generators in `TestMetadataRunner_Init`).

## Step 9 — the ClipCornWebPanel repo (`../ClipCornWebPanel`)

The web panel is a SvelteKit/TypeScript app that renders the same languages, keyed by the **numeric `CCDBLanguage` id**. It has its **own copy** of the icons and its **own** language-name list — both must be extended.

1. **Icon** — copy the same 16×16 flag into the panel's static dir (it uses the identical `language_<id>_16x16.png` name):
   ```bash
   cp "$R/language_62_16x16.png" "../ClipCornWebPanel/static/icons/language/language_62_16x16.png"
   ```
   `LanguageIcon.svelte` builds the src as `/icons/language/language_${id, 2-padded}_16x16.png`.

2. **Name** — `src/lib/constants.ts`, the `LANGUAGES` array. **It is indexed by id: `LANGUAGES[id]` is the display name and `{#each LANGUAGES as lang, i}` uses `i` as the id.** So the array must be **dense and in id order** — append your new name at exactly its id position; never leave a gap or reorder. Use the plain English name (matches the `CCMovieLanguage.<Name>` key suffix). If earlier ids are missing (the list has lagged behind before), fill the whole gap up to your new id — otherwise your entry lands at the wrong index.

3. **Verify:**
   ```bash
   cd ../ClipCornWebPanel && npm run check    # svelte-check → 0 errors
   ```

## Checklist

- [ ] Icon set **and** `../ClipCornWebPanel` mounted (step 0); flag chosen from the language's primary country.
- [ ] Not actually a variant of an existing language (else: parser aliases only).
- [ ] `flag_<country>.png` copied to `res/icons/table/language/language_<id>_16x16.png` (16×16 PNG).
- [ ] `Resources.java`: `ICN_TABLE_LANGUAGE_<id>` declared **and** added to the array.
- [ ] `CCDBLanguage.java`: enum entry, 5 args, unique 3-letter `shortName`, `;` moved to the new last line.
- [ ] Key `CCMovieLanguage.<EnglishName>` added to **all four** locale files (German in 3, English in en_US), values ASCII/German only.
- [ ] `MetadataRunner.java`: alias block before `return null;` covering codes + names + native forms + any observed log strings.
- [ ] `ClipCornWebPanel`: icon copied to `static/icons/language/`, name appended at its id index in `src/lib/constants.ts` `LANGUAGES` (dense/in-order), `npm run check` clean.
- [ ] `./gradlew :compileJava` clean and `make run-tests` green (Java 17).
