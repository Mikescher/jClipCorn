# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

jClipCorn is a portable movie and series manager application written in Java (Swing) that helps organize and manage personal movie collections. It uses SQLite for database storage and supports Windows and Linux platforms.

## Build Commands

### Building the Project
```bash
./gradlew build         # Build the project
./gradlew jar           # Create standard JAR
```

### Running Tests
```bash
./gradlew test          # Run all tests
./gradlew run-tests     # Run tests in Docker container
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

### Development Configuration
- Java version: 12+ (source/target compatibility)
- Build system: Gradle 8.3
- Test framework: JUnit 4 with JUnitParams
- UI framework: Swing with FlatLaf and Substance LAF support

### Version Management
- Version defined in `src/main/de/jClipCorn/Main.java` (VERSION constant)
- Beta flag controlled by BETA constant in Main.java
- Database version tracked separately (DBVERSION constant)

### Testing
Tests are located in `src/test/de/jClipCorn/test/` and use Docker for CI/CD testing (see Dockerfile_tests).

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

### Dependencies
Key libraries include:
- SQLite JDBC for database
- Apache Commons libraries for utilities
- JFreeChart for statistics
- HtmlUnit for web scraping
- FlatLaf and Radiance (Substance) for modern UI themes
- JDOM2/JSON/JSoup for data parsing