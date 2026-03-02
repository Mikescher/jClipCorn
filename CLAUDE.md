
## Project Overview

jClipCorn is a portable movie and series manager application written in Java (Swing) that helps organize and manage personal movie+tvshow collections. 
It uses SQLite for database storage.

## Commands

### Building the Project
```bash
make build              # Build the project
make run-tests          # Run all tests
make jformdesigner      # Generate Java UI code from .jfd files (critical for UI changes, run after every change to jfd files)
```

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
