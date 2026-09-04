---
name: create-db-migration
description: Write a schema migration for one of jClipCorn's three SQLite databases (ClipCornDB.db, ClipCornUserData.db, ClipCornHistory.db) — the migration class, its registration, the version bump, DatabaseStructure, and the test. Use when asked to add/rename/remove a database column or table, change a column's type/nullability/stored format, bump a DB version, or when a schema change makes an existing database unreadable.
---

# Write a database migration

jClipCorn opens **three** SQLite files, each with its own version counter and its own migrator. Getting the wrong one is the most common mistake, so decide first:

| File | Version constant | Migrator | Migration class |
|---|---|---|---|
| `ClipCornDB.db` (shared, objective) | `Main.DBVERSION` (`36`) | `DatabaseMigrator` | `Migration_XX_YY extends DBMigration` |
| `ClipCornUserData.db` (per user: ratings, tags, viewed history, filters, settings) | `Main.USERDATA_DBVERSION` (`1`) | `UserDataDatabaseMigrator` | `UserDataMigration_XX_YY extends UserDataMigration` |
| `ClipCornHistory.db` (change archive) | `HistoryDatabaseMigrator.HISTORYDB_VERSION` (`2`) | `HistoryDatabaseMigrator` | none — a hand-written ladder |

The first two share **one JDBC connection**: `ClipCornUserData.db` is `ATTACH`ed as `userdata`, `ClipCornDB.db` is `main`. Both hold tables of the same name (`MOVIES`, `SERIES`, `SEASONS`, `EPISODES`, `INFO`, `TEMP`, `HISTORY`), so **every table reference in a migration must be schema-qualified** — an unqualified name silently resolves to `main`. `ClipCornHistory.db` has its own `Connection` and unqualified names.

Read the prior art before starting — each of these is a complete, small example of one kind of change:

| Commit | Migration | What it does |
|---|---|---|
| `3e214d86` | `Migration_31_32` | add a column to an existing table |
| `1e3b7a64` | `Migration_30_31` | add a whole table (`PROPERTIES`) and move data into it |
| `751baa04` | `Migration_32_33` | move columns from one table to another |
| `b97bb7d1` | `Migration_29_30` | change the stored *format* of existing values |
| `1b05da2d` | `Migration_33_34` | change nullability — needs a column rebuild |
| `73248775` | `HistoryDatabaseMigrator` | the history database and its migrator |
| `d1603c02` | `Migration_34_35`, `Migration_35_36`, `HistoryIdRewriter` | change a primary key, split one file into two, rewrite the history archive |

## Step 1 — a migration of the main database

### 1.1 The class

`src/main/de/jClipCorn/database/migration/Migration_36_37.java`. Copy `Migration_33_34.java` — it is the smallest complete example.

```java
public class Migration_36_37 extends DBMigration {

	public Migration_36_37(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override public String getFromVersion() { return "36"; }
	@Override public String getToVersion()   { return "37"; }

	@Override
	protected boolean backupAndRestoreTrigger() { return true; }

	@Override
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v36 -> v37] ...");
		...
		return new ArrayList<>();
	}
}
```

- The version strings are **literals**, never `Main.DBVERSION` — the chain must still describe 36→37 when the constant is at 40.
- A class-level javadoc saying *what* changes and *why* the SQL looks the way it does is the house style; every migration since `Migration_29_30` has one.
- `run()` returns `UpgradeAction`s to be executed once the movie list is loaded (`CCMovieList.fireOnAfterLoad` → `DatabaseMigrator.onAfterConnect`). Return `new ArrayList<>()` if you need none — most do.
- Override `backupAndRestoreTrigger()` → `true` whenever you drop, rename or rebuild a column or table (see trap 3). The other hooks — `runInTransaction()`, `disableForeignKeysDuringMigration()`, `vacuumAfterRun()` — default to `true` and are rarely overridden.

### 1.2 Register it

`DatabaseMigrator.MIGRRATION_LIST` (yes, the typo is in the field name), appended at the end:

```java
			Migration_35_36::new,
			Migration_36_37::new
```

A missing registration is not a compile error — it surfaces at runtime as `no migration found to migrate from db-version 36`.

### 1.3 Bump `Main.DBVERSION`

`src/main/de/jClipCorn/Main.java`, to `"37"`. The migrator loops until the file's `INFO.VERSION_DB` equals this constant.

### 1.4 Update `DatabaseStructure` to the new shape

`src/main/de/jClipCorn/database/driver/DatabaseStructure.java` — the `COL_*` definitions, the `TAB_*` table definition that lists them, `TABLES_MAIN` / `TABLES_USERDATA`, and `INFOKEYS` / `INFOKEYS_USERDATA` for new INFO keys.

This file is the schema for a **freshly created** database (`SQLBuilder.createSchema`); your migration SQL is the schema for an **upgraded** one. They must produce the same thing — same column names, same types, same `NOT NULL`, same `DEFAULT`. A mismatch shows up much later as a broken prepared statement or a failed `CCDatabaseValidator` run, not as a test failure.

### 1.5 Wire the column through the rest of the layer

Only for column additions/removals:

- `Statements.java` — the prepared select/insert/update per table.
- The `EProperty` declaration on `CCMovie` / `CCSeries` / `CCSeason` / `CCEpisode` (`EStringProp`, `EIntProp`, `EUUIDProp`, …).
- `CCDatabase.update*FromResultSet` and `update*InDatabase` (plus the `…UserDataFromResultSet` half if the column is user data).
- `ETargetDatabase` on the property if it is not `MAIN` — `TestDatabaseSplit` asserts that the property-level targets and the physical column split agree, in both directions.
- New user-facing text → `LocaleBundle` key in **all four** files under `res/de/jClipCorn/gui/localization/`.
- A new persisted field usually also means bumping `Main.JXMLVER` and adding a `DatabaseXMLImportImpl_V<n>`.

### 1.6 What `DBMigration.migrate()` already does for you

- `BEGIN TRANSACTION` … `COMMIT`, with `ROLLBACK` on any exception out of `run()`.
- The version bump `UPDATE <info-table> SET IVALUE='37' WHERE IKEY='VERSION_DB'` **inside** that transaction — a crash mid-migration therefore leaves the old version behind and the migration simply re-runs. Do not bump the version yourself in `run()`.
- Drops every `JCCTRIGGER_*` before `run()` when `backupAndRestoreTrigger()` is true. `DatabaseMigrator` recreates them **after the whole chain**, and only if `userdata.INFO.HISTORY_ENABLED = '1'`.
- `VACUUM <schema>` afterwards.

Two things it does *not* do:

- **The automatic pre-migration backup does not include `cover/`.** `DatabaseMigrator.tryUpgrade()` calls `BackupManager.createMigrationBackup()`, whose zip filter keeps only the files directly inside `ClipCornDB/` — and restoring a backup deletes the whole database directory before unzipping. Any filesystem work your migration does must therefore be **idempotent and resumable** on its own (`Migration_34_35.renameCoverFiles`: an already-renamed target counts as done).
- **It does not run from `--validate-db`.** The migrator itself is headless-safe (its dialogs fall back to stdout via `DialogHelper`), but `Main` opens the database read-only for `--validate-db` and `tryUpgrade()` bails out on the readonly flag. To migrate a real database offline, drive `new Migration_36_37(db, …).migrate()` from a throwaway JUnit test.

## Step 2 — a migration of the user database

Same shape, three substitutions:

1. `UserDataMigration_01_02 extends UserDataMigration` — that base class exists only to point `getInfoTable()` at `TAB_UD_INFO`, so the version bump lands in `userdata.INFO` and stays inside the transaction. Everything else is `DBMigration`.
2. Register in `UserDataDatabaseMigrator.MIGRATION_LIST` (currently empty — there is a commented placeholder line showing the form).
3. Bump `Main.USERDATA_DBVERSION`, and update the `TAB_UD_*` definitions in `DatabaseStructure`.

Differences from the main chain worth knowing:

- It runs in `CCDatabase.driverConnect()` **after** `upgrader.tryUpgrade()` and after `ensureUserDataDatabase()`, so when it starts the user file always exists and is at least v1.
- No dialog and no `System.exit` inside the migrator: `tryUpgrade(RefParam<String>)` returns `false` and hands back a localized message, which `CCDatabase.upgradeUserDataDatabase()` turns into one `CCLog.addFatalError`.
- A user database **newer** than `Main.USERDATA_DBVERSION` is refused up front with `LogMessage.UserDataDatabaseTooNew` instead of falling into "no migration found".
- Qualify everything with `userdata.` — `INSERT INTO MOVIES` writes to the shared file.

Related bookkeeping, don't break it: `userdata.INFO.VERSION_MAINDB` records the main-DB version the user file was last used with. `CCDatabase.validateUserDataMainVersion()` refuses to start when the main database is *older* than that record (that is a `ClipCornDB.db` from an outdated installation — re-running a main migration over an already-migrated user database is what turns into an unstartable app), and `updateUserDataMainVersion()` refreshes the record on every connect.

## Step 3 — a migration of the history database

`ClipCornHistory.db` has no `DBMigration` chain. `HistoryDatabaseMigrator.tryUpgrade()` is a hand-written ladder:

```java
while (!version.equals(HISTORYDB_VERSION)) {
	if (version.equals("1")) { migrate_01_02(); version = "2"; }
	else throw new Exception("no migration found to migrate history-db from version " + version);

	setDBVersion(version);
}
```

Add a `migrate_0N_0M()` with its own `BEGIN`/`COMMIT`/`ROLLBACK` (copy `migrate_01_02`), extend the ladder, bump `HISTORYDB_VERSION`. The create-time schema lives in `CCHistoryDatabase` — keep the two in sync. If the change also affects the *staging* `HISTORY` tables inside `main`/`userdata`, factor the work out into a shared class the main-DB migration can call too (that is what `HistoryIdRewriter` is: one rewrite used by both `Migration_34_35` and `HistoryDatabaseMigrator`).

## Step 4 — SQLite traps that already bit this project

1. **Changing a primary key, a type or a `NOT NULL` needs a table rebuild.** SQLite's `ALTER TABLE` cannot do any of them in place. Two working patterns: rebuild the whole table (create `X_NEW` → `INSERT … SELECT` → `DROP X` → `ALTER TABLE X_NEW RENAME TO X`, see `Migration_34_35.rebuildTable`), or rebuild a single column (rename to `_OLD` → add the new one → `UPDATE` → drop `_OLD`, see `Migration_33_34.makeColumnNullable`). The physical column order changes; that is fine, the schema is compared by name (`CCSQLTableDef.isEqual`).

2. **Derive the new layout from `PRAGMA table_info`, not from hardcoded DDL.** Earlier migrations have already moved columns to the end of their tables, so a hand-written `CREATE TABLE` will not match the database you are actually given. Carry column 4 (`dflt_value`) across as well — a silently dropped `DEFAULT` bites on the first `INSERT` that omits the column.

3. **`ALTER TABLE … DROP COLUMN` fails while a trigger or index references the column.** All the history triggers reference every column of every tracked table, so any drop/rename needs `backupAndRestoreTrigger()` → `true`. In `Migration_35_36` that override is load-bearing, not cosmetic.

4. **A `TEXT PRIMARY KEY` does not imply `NOT NULL`** (unlike `INTEGER PRIMARY KEY`). Always spell out `NOT NULL PRIMARY KEY`.

5. **`COALESCE` when a formerly-nullable column becomes `NOT NULL`.** `col <> ?` evaluates to NULL — not true, not false — for a NULL `col`, so a `WHERE` that filters "changed rows" silently drops them, and a `SELECT col` then fails the new `NOT NULL`. `Migration_35_36.copyUserRows` wraps every comparison *and* every selected value in `COALESCE(col, <default>)`.

6. **`INSERT OR REPLACE` deletes and re-inserts the row.** Production sets `PRAGMA recursive_triggers = true`, so that fires the BEFORE-DELETE *and* AFTER-INSERT history triggers — a remove+add pair per column instead of one guarded update. Use `INSERT INTO t (…) VALUES (…) ON CONFLICT(<pk>) DO UPDATE SET x=excluded.x`. `MemoryDatabase` sets `recursive_triggers` too, so a test can see this.

7. **`CAST('<uuid>' AS INTEGER)` is `0`, and so is `CAST('' AS INTEGER)`.** Any rewrite that maps old integer values must be guarded with `expr <> '' AND expr NOT GLOB '*[^0-9]*'` (`HistoryIdRewriter.isPlainInteger`) — otherwise a re-run after a crash remaps already-converted rows onto the id-0 value. The same guard is what makes the pass idempotent.

8. **WAL must stay off.** SQLite only guarantees an atomic commit across attached databases in rollback-journal mode, and `main` + `userdata` are committed together. Nothing sets `journal_mode` today; keep it that way (there is a comment next to the pragmas in `SQLiteDatabase.open()`).

9. **`PRAGMA foreign_keys` inside a transaction is a silent no-op.** `DBMigration.migrate()` issues it right after `BEGIN`, so `disableForeignKeysDuringMigration()` does nothing at all. It happens not to matter — the app never turns FK enforcement on — but do not build on it. If you really need deferred constraints, use `PRAGMA defer_foreign_keys = '1'`, which *does* work inside a transaction (`Migration_16_17`).

10. **Spell out the column list.** `INSERT INTO userdata.X SELECT * FROM main.X` works exactly until the two tables' column orders differ. Name the columns on both sides.

11. **Two `COVERS` rows can share a `FILENAME`** (`ERROR_DUPLICATE_REFERENCES_COVER_FILE` exists for it). Anything that renames or moves a cover file must copy instead of move when the source name is used more than once.

12. **SQLite strips the schema qualifier when it stores trigger DDL.** `CREATE TRIGGER main.X …` comes back from `sqlite_master` as `CREATE TRIGGER X …`, and `CCDatabaseHistory.testTrigger()` compares the text verbatim — hence `createTriggerStatements()` returning `(name, executableSQL, storedSQL)`. Relevant if a migration regenerates triggers itself.

13. **`%d` locale strings blow up on a non-number.** If the migration changes the *type* of a value that ends up in a `LogMessage.*` template, switch that template to `%s` in all four locale files. `IllegalFormatConversionException` thrown out of a catch block is how the UUID migration would have shipped.

## Step 5 — the test

Follow `TestUUIDMigration` (34→35), `TestSplitMigration` (35→36) or `TestUserDataMigration` (the user chain).

```java
private static MemoryDatabase createV36Database() throws Exception {
	MemoryDatabase db = new MemoryDatabase();
	assertTrue(db.createNewDatabase(FSPath.Empty, "TEST"));
	// ... rewind the current schema to the old shape ...
	return db;
}

new Migration_36_37(db, FSPath.Empty, "ClipCornDB", false).migrate();
```

- `MemoryDatabase.createNewDatabase` builds the **current** schema — both `main` and `userdata`, with `recursive_triggers` on. So the fixture is written backwards: `DROP` / `ALTER` / `CREATE` the current tables back into the *old* shape, then migrate forward. `TestSplitMigration.createV35Database` is the model.
- The fixture only needs the columns the migration actually touches, as long as the migration derives its layout from `PRAGMA table_info`. Both existing ones say so in their class comment.
- Assert on `PRAGMA table_info(...)` for shape and on plain `SELECT`s for content — including the rows that must **not** move.

What these fixtures do *not* cover, and what you have to check another way:

- **The filesystem.** `databaseDirectory` is `FSPath.Empty`, so cover renames take the "no cover directory" branch. `TestUUIDMigration.testCoverRenameIsResumable` builds a real temp dir for that.
- **`DatabaseMigrator` itself** — the chain, the backup, the dialog, the trigger restore. Nothing tests those.
- **Volume and real data.** Verify against a copy of the real `ClipCornDB` separately, from a throwaway JUnit test (loading a 23 MB database twice in one JVM needs `test { maxHeapSize = '3g' }`; the 512 MB default OOMs).

Two things that will bite in the test JVM:

- `ClipCornBaseTest.@After` asserts `!CCLog.hasErrors()` — a migration that logs a `CCLog.addError` fails the test even if the assertions passed.
- `CCLog.addFatalError` calls `System.exit` **even in unit-test mode**. Never let a tested code path reach it; return a message and let the caller do the logging (that is why `UserDataDatabaseMigrator.tryUpgrade` takes a `RefParam<String>`).

## Step 6 — build and test

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :compileJava --console=plain
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew test --console=plain
```

The host default JDK is too new for the build. `make run-tests` runs the same suite in Docker but builds a ~1 GB context — prefer the gradle invocation above.

Commit one logical change per commit, one line, `<next-version> <Type>: <description>` with `<Type>` ∈ `{Bugfix, Feature, Task, Other}`.

## Checklist

- [ ] Right database picked; every table reference schema-qualified (`main.` / `userdata.`).
- [ ] `Migration_XX_YY` created, version strings are literals, class javadoc explains what and why.
- [ ] Registered in `DatabaseMigrator.MIGRRATION_LIST` / `UserDataDatabaseMigrator.MIGRATION_LIST` (or the `HistoryDatabaseMigrator` ladder).
- [ ] `Main.DBVERSION` / `Main.USERDATA_DBVERSION` / `HISTORYDB_VERSION` bumped.
- [ ] `DatabaseStructure` updated so a fresh database and a migrated one end up identical (types, `NOT NULL`, `DEFAULT`).
- [ ] `backupAndRestoreTrigger()` → `true` if any column or table is dropped, renamed or rebuilt.
- [ ] No version bump inside `run()`; no reliance on `PRAGMA foreign_keys`; no `INSERT OR REPLACE`; no `SELECT *`.
- [ ] Filesystem work is idempotent and resumable (the migration backup has no `cover/`).
- [ ] Column wired through `Statements`, the element `EProperty`, `CCDatabase.update*`, `ETargetDatabase`; new text in all four locale files.
- [ ] Test in the `TestUUIDMigration` / `TestSplitMigration` style; nothing in it can reach `CCLog.addFatalError` or `CCLog.addError`.
- [ ] `./gradlew :compileJava` clean and the full suite green (Java 17).
