
## Project Overview

jClipCorn is a portable movie and series manager application written in Java (Swing) that helps organize and manage personal movie+tvshow collections. 
It uses SQLite for database storage.

## Commands

### Building the Project
```bash
make build              # Build the project
make run-tests          # Run all tests
make jformdesigner      # Generate Java UI code from .jfd files (critical for UI changes, run after every change to jfd files)
make run-local          # Run the app on the local test database in _local/
```

### Running the app and checking UI changes visually
Inside the ccc container the app can be started on a headless desktop (`ccc-display`), screenshotted and clicked.
After UI changes (new columns, dialogs, settings, filter entries, cover overlays) start the app and look at the result before committing.
Follow the `run-local` skill (`.claude/skills/run-local/SKILL.md`): it runs on a *copy* of `_local/`, because `_local/` is the user's real local database and starting on it can migrate it.

## Versions and commit messages

`CHANGELOG.txt` is generated from the commit messages, and jar names / version sorting depend on `Main.VERSION`, so both numbers have to follow one scheme:

| | Format | Example after release 1.11.2 |
|---|---|---|
| Release | tag `vX.Y.Z`, `Main.VERSION = "X.Y.Z.0"`, commit `X.Y.Z RELEASE` | `1.11.2.0` |
| Betas | `Main.VERSION = "X.Y.Z.N"`, N counts up from the last release (skill `beta-release`) | `1.11.2.1`, `1.11.2.2`, … |
| Commits | prefix = the *next* release `X.Y.(Z+1)` | `1.11.3 Feature: …` |
| Next release | `X.Y.(Z+1)` (or a minor/major jump) | `1.11.3.0` |

- Commit message format (enforced by the commit-msg hook): one line `X.Y.Z <Type>: <text>`, Type one of `Feature`, `Bugfix`, `Task`, `Update`, `Documentation`, … (see the hook regex). No body, no trailers (no Co-Authored-By): every line ends up in the changelog.
- Take the prefix from `git log -1 --format=%s` of the last non-release commit, or `X.Y.(Z+1)` right after an `X.Y.Z RELEASE` commit.
- Never change the `X.Y.Z` part of `Main.VERSION` by hand; only the `beta-release` and `release` skills touch it.
- Before every commit, check and warn the user (do not silently fix) if:
  - the prefix is not `X.Y.(Z+1)` relative to `X.Y.Z` in `Main.VERSION` (e.g. `Main.VERSION = 1.11.2.3` but prefix `1.11.2` or `1.11.4`),
  - the prefix looks like a typo (`10.11.3` instead of `1.11.3`),
  - `Main.VERSION`'s `X.Y.Z` is not the last release tag (`git describe --tags --abbrev=0`).

## Code Architecture

### Package Structure
- `de.jClipCorn.Main` - Application entry point
- `de.jClipCorn.database` - Database layer with SQLite integration
- `de.jClipCorn.gui` - Swing UI components and frames
  - `gui.mainFrame` - Main application window
  - `gui.frames` - Various dialog windows (settings, statistics, etc.)
  - `gui.guiComponents` - Custom UI components
- `de.jClipCorn.features` - Core features (logging, serialization, parsing, online services)
- `de.jClipCorn.properties` - Application configuration and settings management
- `de.jClipCorn.util` - Utility classes for filesystem, datetime, and helper functions

### Key Components
- **CCMovieList** - Central database management class
- **CCProperties** - Application settings and configuration
- **MainFrame** - Primary application window
- **Resources** - Resource management for icons and assets

### UI Development - CRITICAL
**NEVER EDIT JAVA UI FILES DIRECTLY IF THERE IS A CORRESPONDING .jfd FILE**

- UI forms are designed using JFormDesigner (.jfd files)
- The Java files (like AddMovieFrame.java, EditMovieFrame.java, etc.) contain generated code marked with `// JFormDesigner - DO NOT MODIFY`
- **Always edit the .jfd files instead of the Java files for UI changes**
- If UI changes are needed:
  1. Edit the .jfd file with the form designer
  2. Ask the user to regenerate the Java code from the .jfd file
  3. Never directly modify the generated Java UI code

This prevents UI changes from being lost when forms are regenerated.

### UI Text Localization - CRITICAL
**NEVER HARDCODE UI TEXT IN CODE OR JFD FILES**

- All UI-facing text must be translated via locale files in `res/de/jClipCorn/gui/localization/`
- Available locale files: `locale.properties` (default), `locale_en_US.properties`, `locale_de_DE.properties`, `locale_dl_DL.properties`
- **In Java files:** Use `LocaleBundle.getString("key")` or similar localization methods
- **In JFD files:** Use `new FormMessage( null, "FrameName.componentName.text" )` instead of hardcoded strings like `"text": "Hardcoded Text"`
- All UI text keys should follow the pattern: `FrameName.componentName.text` (e.g., `AddMovieFrame.lblTitle.text`)
- When adding new UI components, always add corresponding locale keys to ALL locale files

This ensures the application can be properly localized for different languages.
