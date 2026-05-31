---
name: migrate-ui-to-jfd
description: Migrate an existing hand-coded Swing frame/dialog in jClipCorn to JFormDesigner (.jfd + generated Java). Use when asked to "migrate X to FormDev/JFormDesigner/jfd", convert a frame from absolute/manual layout to a .jfd form, or when continuing the [FORMDEV_MIG] migration effort tracked in Main.java.
---

# Migrate an existing Swing UI to JFormDesigner

jClipCorn is migrating every hand-coded Swing window to JFormDesigner. Each migration replaces manual `initGUI()`-style layout code with a `.jfd` form file and a regenerated `initComponents()` block. The progress checklist lives in `src/main/de/jClipCorn/Main.java` (search for `[ ] FrameName` lines). Past migrations are the git commits tagged `[FORMDEV_MIG]` — `git log --grep="FORMDEV_MIG"` and `git show <hash>` are the best reference for the exact diff shape.

## Mental model

- A `.jfd` file (JFDML text format) is the source of truth for layout. It sits next to the `.java` file in the same package directory, named identically (`FooFrame.jfd` ↔ `FooFrame.java`).
- The JFormDesigner CLI tool reads the `.jfd` and **rewrites only** the two generated regions inside the `.java`:
  - `//GEN-BEGIN:initComponents` … `//GEN-END:initComponents`
  - `//GEN-BEGIN:variables` … `//GEN-END:variables`
- Everything else in the `.java` (constructors, business logic, event handler methods, a `postInit()` for dynamic setup) is hand-written and preserved.

## Step-by-step

### 1. Read the existing frame and pick a reference

Read the target `.java`. Note: window type (JFrame vs JDialog), every component + field name, layout/bounds, event wiring, dynamic setup, and any custom components. Then find a previously-migrated form that uses the **same kinds of components** as a copy-paste reference:

```
ls src/main/**/*.jfd
grep -rl "CCEnumComboBox" --include=*.jfd src/      # find a jfd that already uses your custom component
```

Good general references: `inputErrorFrame/InputErrorDialog` (simple dialog, buttons, JList), `createSeriesFolderStructureFrame/CreateSeriesFolderStructureFrame` (FSPath field + chooser button, custom-create code), `addMovieFrame/AddMovieFrame` (CCEnumComboBox with type params). The empty starting point is `frames/_template/Template.jfd` + `Template.java`.

### 2. Change the base class

Hand-coded frames usually extend `JFrame`/`JDialog`. Migrated ones extend:
- `de.jClipCorn.gui.guiComponents.JCCFrame` (for windows) — constructor `super(CCMovieList ml)`
- `de.jClipCorn.gui.guiComponents.JCCDialog` (for dialogs) — constructor `super(CCMovieList ml)` or `super(ml, owner)`

These provide `getMovieList()`, `ccprops()` (returns `movielist.ccprops()`), the frame icon, and size-debug plumbing. **Do not re-declare `ccprops()`** — it's inherited from `ICCWindow`; a private override fails to compile (`cannot implement ccprops() in ICCWindow`).

If the old frame had no `CCMovieList` (e.g. runs before DB load), thread one through anyway — every call site that constructs it has access to a `CCMovieList`. Update the constructor signature and all callers accordingly.

The `.jfd`'s `FormWindow` class string must match the base class, e.g. `new FormWindow( "de.jClipCorn.gui.guiComponents.JCCDialog", ... )`.

### 3. Write the .jfd file

See the companion skill `create-jfd-form` for the full JFDML syntax reference. Key points for a migration:

- `i18n.keyPrefix` = the frame name. Set `bundlePackage`/`bundleName` to `de.jClipCorn.gui.localization` / `locale`.
- **Reuse the existing locale keys.** Do not invent new `.text`-suffixed keys if the old code already referenced keys like `InitialConfigFrame.btnStart`. Put the exact existing key in `new FormMessage( null, "InitialConfigFrame.btnStart" )`. Only add new keys (to ALL `res/.../locale*.properties` files) if the component had a hardcoded string.
- Convert absolute `setBounds(...)` layout into a `com.jgoodies.forms.layout.FormLayout` with `$columnSpecs`/`$rowSpecs`. Map label/control pairs to grid cells; group button bars and composite controls into nested `JPanel`s with their own FormLayout.
- Custom components: set the fully-qualified class string, plus `auxiliary()` entries — `"JavaCodeGenerator.typeParameters"` for generics and `"JavaCodeGenerator.customCreateCode"` (e.g. `"new ${field_type}(AppTheme.getWrapper());"`) when the constructor needs arguments.
- Wire events with `addEvent( new FormEvent( "java.awt.event.ActionListener", "actionPerformed", "methodName", <passEvent> ) )`. `false` generates `e -> methodName()`; `true` generates `e -> methodName(e)`.

### 4. Rewrite the .java skeleton

- Keep the constructor calling `initComponents(); postInit();` (mirror `Template.java`).
- Empty the two GEN regions to just their marker comment pair — the generator fills them:
  ```java
  private void initComponents() {
      // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
      // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // JFormDesigner - End of variables declaration  //GEN-END:variables
  ```
- Move dynamic setup (model population, `setSelected`, `setSelectedIndex`, enabling, dynamic text) out of the old `initGUI` into `postInit()`. **Field assignments and `addActionListener` happen in the generated `initComponents`.**
- Extract inline anonymous/lambda listeners into named methods that match the `addEvent` names in the `.jfd`.
- Import any types the generated code will reference (custom component classes, enum types used in `customCreateCode`).

### 5. ⚠️ Guard listeners that fire during init

In the old code, `addActionListener` was typically called **after** `setSelectedIndex`/`setSelectedItem`, so the handler did not fire during construction. After migration the listener is attached inside `initComponents` (before `postInit`), so setting the selection in `postInit` **will** fire the handler. Add a guard:

```java
private boolean isInitialized = false;
// ... in postInit(), set selections, then last line:
isInitialized = true;

private void updateLanguage() {
    if (!isInitialized) return;
    ...
}
```

### 6. Generate the Java

`make jformdesigner` runs `./gradlew :compileJava` **first**, then generates. During a migration the `.java` references fields that don't exist yet (GEN regions are empty), so that pre-compile step fails. Run the generator **directly** against the already-compiled classpath instead:

```bash
java -classpath "./_utils/jfd-8.3.1-b470/lib/JFormDesigner.jar" \
     "com.jformdesigner.application.CommandLineMain" \
     --generate --recursive --verbose \
     "./jClipCorn.jfdproj" \
     "./src/main/de/jClipCorn/gui/frames/<targetDir>/"
```

(Adjust the jfd tool version dir to whatever the `jformdesigner:` target in the `Makefile` references.) After this first generation succeeds, `make jformdesigner` works normally for subsequent edits.

### 7. Compile, update checklist, verify

```bash
./gradlew :compileJava
```

Fix any errors (usually missing imports or a stray `ccprops()` override). Then flip the frame's line in `Main.java` from `- [ ] FrameName` to `- [x] FrameName`.

Note: `./gradlew build` runs the full test suite. Some DB/serialization tests may be **pre-existing failures** unrelated to UI work — confirm with `git stash && ./gradlew test --tests <class> ; git stash pop` before blaming your change. A clean `:compileJava` plus a visual review of the generated `initComponents` is the real bar for a UI migration.

## Commit convention

`X.Y.Z Task: [FORMDEV_MIG] Migrate <FrameName> to FormDev` (match the existing tagged commits).

## Checklist

- [ ] Base class is `JCCFrame`/`JCCDialog`; `FormWindow` class string matches.
- [ ] `.jfd` reuses existing locale keys; no hardcoded UI strings.
- [ ] Custom components have correct `typeParameters` / `customCreateCode`.
- [ ] Listeners extracted to named methods; `isInitialized` guard added where init-time firing would misbehave.
- [ ] Generated directly with the JFormDesigner CLI (not the full make target on first run).
- [ ] `./gradlew :compileJava` is green.
- [ ] `Main.java` checklist line flipped to `[x]`.
