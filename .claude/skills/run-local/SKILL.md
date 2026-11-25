---
name: run-local
description: Start jClipCorn inside the ccc container on a copy of the local test database (`make run-local`), drive it with `ccc-display` and take screenshots. Use to visually verify UI changes (new columns, dialogs, settings, filter-tree entries, cover overlays) before committing, or whenever asked to run/start/screenshot the app.
---

# Run the app locally and look at it

The ccc container has a headless desktop (`ccc-display`: sway + XWayland). jClipCorn is a Swing app, so it runs there and can be screenshotted and clicked. Nothing opens on the user's screen.

## 1. Never run on `_local` itself — copy it

`_local/` is the user's real local database. Starting the app on it can migrate it to a newer DB version, writes history rows and leaves `*.~lock` files. `make run-local` points at `_local` directly, so do not use the make target; run the same gradle command against a copy in the scratchpad:

```bash
RUN=<scratchpad>/jcc-run
rm -rf "$RUN" && cp -r _local "$RUN"
rm -f "$RUN"/ClipCornDB/*.~lock     # copied locks from a running instance block the start
```

## 2. Prepare the state you want to see (optional)

Edit the copy with `python3` + `sqlite3` instead of clicking through dialogs:

- Main data: `$RUN/ClipCornDB/ClipCornDB.db` (`MOVIES`, `SERIES`, …).
- Settings: `$RUN/ClipCornDB/ClipCornUserData.db`, table `PROPERTIES (PKEY, PVALUE, LAST_CHANGED)`. A missing row means the default. Example: `PROP_MAINFRAME_VISIBLE_COLUMNS` is a `;`-list of `MainFrameColumn` ids, bools are `true`/`false`.

The user's `_local` runs with locale `dl_DL` ("Denglisch"): check that new texts look right in that file too.

## 3. Start

```bash
ccc-display start
ccc-display launch ./gradlew --no-daemon run -PrunWorkingDir="$RUN" --args=-prev-cover-cache   # prints "pid N log FILE"
ccc-display wait-window "jClipCorn v" 180
```

- `--no-daemon` is required: Gradle daemons are shared with sibling ccc containers via `~/.gradle` + host networking, so with a daemon the app may run in another container (still visible here, but `kill` cannot reach it).
- The window appears before the table is filled. Wait until the launch log contains `Filters loaded`, then give it a few more seconds before the first screenshot.
- Do not run `./gradlew test` at the same time — both use `build/`.

## 4. Look and interact

```bash
ccc-display screenshot                      # full screen 1440x900, pixels = click coordinates
ccc-display screenshot --window "REGEX"     # one window; add the printed offset to its coordinates
ccc-display click X Y [right] [--double]
ccc-display drag X1 Y1 X2 Y2                # e.g. the table's horizontal scrollbar at y≈839
ccc-display move X Y                        # hover (cover overlays, tooltips)
ccc-display windows | focus REGEX | key Return | type TEXT
```

Read the PNG path it prints to see the screen. Useful spots in the main window: filter tree on the left (x≈90), cover preview bottom-left (hover it to see "only on hover" overlays), settings = gear in the toolbar (x≈364, y≈44), tab "Main Fenster".

## 5. Stop

```bash
ccc-display close "jClipCorn v"
ccc-display focus "beenden" && ccc-display key Return    # "Wirklich beenden?" appears if the log has errors
timeout 60 bash -c 'while ccc-display windows | grep -q de-jClipCorn; do :; done'
```

- Clicking the dialog's button often misses; focus + `Return` is reliable.
- Wait until no jClipCorn window is left before starting again — a second instance fails with a FATAL ERROR window because the database is locked.
- The `GradleWrapperMain` processes stay behind as zombies (`Z` state). Harmless.

## Known noise

With `-prev-cover-cache`, opening a series preview logs `Could not load Cover … File damaged ?` and `SQLException: ResultSet already requested` (parallel cover loads in `JCoverChooser` share one prepared statement in `CCDatabase.getCoverPreviewOrNull`). That is a pre-existing race, not caused by the change under test — but it is what makes the exit dialog appear.
