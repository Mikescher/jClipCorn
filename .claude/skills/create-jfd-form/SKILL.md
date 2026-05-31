---
name: create-jfd-form
description: Create a new JFormDesigner window or dialog in jClipCorn from scratch — the .jfd form file, its generated Java, locale keys, and wiring. Use when asked to add a new frame/dialog/window, build a new UI screen, or create a .jfd form. (For converting an existing hand-coded frame, use migrate-ui-to-jfd instead.)
---

# Create a new JFormDesigner form

All new UI in jClipCorn is built as a JFormDesigner `.jfd` form plus a generated Java class. **Never hand-code Swing layout, and never edit the generated regions of the `.java` directly.** The `.jfd` is JFDML (a text format) — you author it directly with a text editor; the JFormDesigner CLI generates the matching Java.

## Anatomy

A form lives in its own package dir: `src/main/de/jClipCorn/gui/frames/<name>Frame/<Name>Frame.{jfd,java}`. The starting skeleton is `src/main/de/jClipCorn/gui/frames/_template/` (`Template.jfd` + `Template.java`) — copy it.

### Minimal .jfd (from Template.jfd)

```
JFDML JFormDesigner: "8.1.1.0.298" Java: "17.0.7" encoding: "UTF-8"

new FormModel {
	"i18n.bundlePackage": "de.jClipCorn.gui.localization"
	"i18n.bundleName": "locale"
	"i18n.keyPrefix": "MyNewFrame"
	contentType: "form/swing"
	root: new FormRoot {
		add( new FormWindow( "de.jClipCorn.gui.guiComponents.JCCFrame", new FormLayoutManager( class com.jgoodies.forms.layout.FormLayout ) {
			"$columnSpecs": "unrelgap, default:grow, unrelgap"
			"$rowSpecs": "unrelgap, default:grow, unrelgap"
		} ) {
			name: "this"
			"$sizePolicy": 1
			"title": new FormMessage( null, "MyNewFrame.this.title" )
			"defaultCloseOperation": 2
			// ... add components here ...
		}, new FormLayoutConstraints( null ) {
			"location": new java.awt.Point( 0, 0 )
			"size": new java.awt.Dimension( 650, 500 )
		} )
	}
}
```

## Decisions to make first

- **Window vs dialog:** windows extend `de.jClipCorn.gui.guiComponents.JCCFrame`; dialogs extend `JCCDialog`. Put the matching FQ class string in `FormWindow(...)`. Both take a `CCMovieList` in their constructor and provide `getMovieList()` and `ccprops()`.
- **keyPrefix** = the class name. This is what JFormDesigner uses to auto-name new i18n keys in its GUI, but in the text file every `FormMessage` carries the **full** key string explicitly.

## Layout: jGoodies FormLayout

Use `com.jgoodies.forms.layout.FormLayout` (the project standard). `$columnSpecs`/`$rowSpecs` are comma-separated specs. Common tokens:
- `default`, `default:grow`, `preferred`, fixed sizes, `min`
- gap columns/rows: `unrelgap` (≈ `$ugap`), `labelcompgap` (≈ `$lcgap`), `linegap` (≈ `$lgap`)
- A typical label/control screen: `"$columnSpecs": "unrelgap, default, labelcompgap, default:grow, unrelgap"` and a row per field separated by `linegap`.

Place a component with cell constraints:
```
add( new FormComponent( "javax.swing.JLabel" ) {
	name: "lblName"
	"text": new FormMessage( null, "MyNewFrame.lblName" )
}, new FormLayoutConstraints( class com.jgoodies.forms.layout.CellConstraints ) {
	"gridX": 2
	"gridY": 4
} )
```
Useful constraint keys: `gridX`, `gridY`, `gridWidth`, `gridHeight`, and alignment `"hAlign"`/`"vAlign": sfield com.jgoodies.forms.layout.CellConstraints FILL`.

**Group composites in nested panels.** A button bar or a "text field + `...` button" pair becomes a child `FormContainer( "javax.swing.JPanel", new FormLayoutManager(...) )` with its own column/row specs, then placed as one cell in the parent. (E.g. button bar: `"default, default:grow, default"` with the left button at gridX 1 and the right at gridX 3.)

## Components

- **Standard Swing:** `"javax.swing.JButton"`, `JLabel`, `JCheckBox`, `JTextField`, `JTextArea`, `JComboBox`, `JList`, `JPanel`, `JScrollPane`, `JSplitPane`, `JProgressBar`, etc. Set properties as quoted keys: `"editable": false`, `"lineWrap": true`, `"columns": 10`, `"horizontalAlignment": 0`.
- **Generics** (e.g. `JComboBox<String>`, `CCEnumComboBox<AppTheme>`): add
  ```
  auxiliary() { "JavaCodeGenerator.typeParameters": "String" }
  ```
- **Custom components needing constructor args:** add `customCreateCode`:
  ```
  add( new FormComponent( "de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox" ) {
  	name: "cbxTheme"
  	auxiliary() {
  		"JavaCodeGenerator.typeParameters": "AppTheme"
  		"JavaCodeGenerator.customCreateCode": "new ${field_type}(AppTheme.getWrapper());"
  	}
  } )
  ```
  Custom widgets already used in forms: `JReadableFSPathTextField`, `ReadableTextField`, `CCEnumComboBox`, `CoverLabelFullsize`, and various project tables. `grep -rl "<ComponentName>" --include=*.jfd src/` to find a working example.
- **A custom component must be compiled and on the classpath** (`build/classes/...`) before the generator can resolve it. `jClipCorn.jfdproj` lists the classpath/source roots; recursive generation over `src/.../frames/` picks up any new `.jfd` automatically — no per-file registration.

## Events

```
addEvent( new FormEvent( "java.awt.event.ActionListener", "actionPerformed", "onOkay", false ) )
```
The last boolean is *pass-the-event*: `false` → generates `e -> onOkay()`; `true` → `e -> onOkay(e)`. Write the matching handler method by hand in the `.java`.

## Localization (CRITICAL)

Every user-facing string is a `new FormMessage( null, "Frame.key" )` referencing a key in `res/de/jClipCorn/gui/localization/`. **Add each new key to ALL locale files:** `locale.properties` (default/English), `locale_en_US.properties`, `locale_de_DE.properties`, `locale_dl_DL.properties`. Never put a literal user-facing string in the `.jfd` (the bare `"..."` form is only acceptable for non-localized text like `"..."` on a chooser button). The designer i18n format generates `LocaleBundle.getString(${key})` (configured in `jClipCorn.jfdproj`).

## The Java side

1. Copy `Template.java`. Set package/class name, extend `JCCFrame`/`JCCDialog`.
2. Constructor: `super(ml); initComponents(); postInit(); setLocationRelativeTo(owner);`
3. Leave the two GEN regions as just their marker-comment pairs — the generator fills them:
   ```java
   private void initComponents() {
       // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
       // JFormDesigner - End of component initialization  //GEN-END:initComponents
   }
   // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
   // JFormDesigner - End of variables declaration  //GEN-END:variables
   ```
4. Put all dynamic setup (models, selections, enabling) in `postInit()`. If you set a combo/list selection in `postInit` and it has an action listener, guard the handler with an `isInitialized` flag (listeners are attached in `initComponents`, before `postInit`, so they fire during init).
5. Write the event handler methods referenced by `addEvent`.
6. Import every type the generated code will reference.

## Generate & build

```bash
make jformdesigner      # compiles, then regenerates ALL forms' Java from their .jfd
./gradlew :compileJava  # verify
```

On the very first generation of a brand-new form, the file references not-yet-generated fields, so the `:compileJava` inside `make jformdesigner` fails. Run the generator directly first (then `make jformdesigner` works for later edits):

```bash
java -classpath "./_utils/jfd-8.3.1-b470/lib/JFormDesigner.jar" \
     "com.jformdesigner.application.CommandLineMain" \
     --generate --recursive --verbose \
     "./jClipCorn.jfdproj" "./src/main/de/jClipCorn/gui/frames/<newDir>/"
```

Run `make jformdesigner` after **every** `.jfd` change — the generated Java is checked in and must stay in sync.

## Checklist

- [ ] `.jfd` copied from Template; correct base class in `FormWindow`.
- [ ] FormLayout specs; composites grouped into nested panels.
- [ ] Custom components have `typeParameters` / `customCreateCode` and are compiled.
- [ ] All UI strings are `FormMessage` keys, added to **all four** locale files.
- [ ] Events wired via `addEvent`; matching handler methods written.
- [ ] `.java` has empty GEN regions, `postInit()` for dynamic setup, `isInitialized` guard if needed.
- [ ] Generated and compiles clean; `make jformdesigner` re-run after the final `.jfd` edit.
