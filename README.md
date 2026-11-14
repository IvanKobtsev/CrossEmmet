# CrossEmmet

A Kotlin JetBrains plugin that expands CSS property initials, including logical and modern properties, in CSS, SCSS, and XCSS files.

## Usage

Type an abbreviation inside a declaration block and select its **CrossEmmet** completion, or invoke **Expand CrossEmmet Abbreviation** through Find Action. Assign a shortcut under Settings → Keymap by searching for that action.

| Input | Expansion |
| --- | --- |
| `mi16` | `margin-inline: 16px;` |
| `mis-8` | `margin-inline-start: -8px;` |
| `mi1.5rem` | `margin-inline: 1.5rem;` |
| `pbs0` | `padding-block-start: 0;` |
| `fg1` | `flex-grow: 1;` |
| `mis` | `margin-inline-start: ;` with the caret before `;` |

Abbreviations use the first letter of each hyphen-separated property word. Single-word properties use one letter. Collisions are intentional: completion lists all matches, and the action opens a chooser. For example, `mi` offers `margin-inline` and `max-inline-size`. This vocabulary differs from Emmet.

Numbers can be signed decimals. Lengths default to `px`, times to `ms`, and angles to `deg`. Unitless properties retain bare numbers; integer properties reject fractions. Explicit length units, percentages, time units, and angle units are accepted only for their corresponding categories. Unitless length zero stays `0`. Complex values, keywords, colors, functions, chained declarations, and custom aliases are not abbreviated in v1; expand the property alone and enter its value normally.

Expansion works in declaration positions, including nested SCSS blocks. It preserves existing semicolons and supports undo. Comments, strings, selectors, property values, interpolation, and multiple carets are excluded. Built-in Emmet remains available.

`.xcss` is associated with the IDE's existing CSS file type. Explicit user file associations remain authoritative; if another plugin owns the extension, choose the desired association in Settings → Editor → File Types. CrossEmmet does not implement a separate XCSS language.

See the generated [abbreviation catalog](docs/abbreviations.md).

## Build and install

GitHub Actions builds and verifies the plugin on every push and pull request, and can also be run manually. Download the `crossemmet-plugin` artifact from a successful CI run; plugin artifacts are retained for 30 days. Failed runs upload test and verifier diagnostics for 14 days.

Requires JDK 25 and JetBrains CSS support. The initial compatibility target is WebStorm 2026.2.2 / platform 262; other CSS-enabled IDEs require verification before claiming support.

```powershell
./gradlew.bat test buildPlugin generateCatalog
# Use an installed SDK instead of downloading WebStorm:
./gradlew.bat test buildPlugin '-PidePath=C:/Program Files/JetBrains/WebStorm 2026.2.2'
./gradlew.bat verifyPlugin
# Local verification without Marketplace lookups (after dependencies are cached):
./gradlew.bat verifyPlugin '-PidePath=C:/Program Files/JetBrains/WebStorm 2026.2.2' --offline
```

On macOS/Linux, use `sh ./gradlew`. Install `build/distributions/CrossEmmet-0.1.0.zip` using Settings → Plugins → gear → Install Plugin from Disk, then restart if requested. For development, run `./gradlew.bat runIde`.

The bundled catalog is versioned in `PropertyCatalog`; update it and run `generateCatalog` to refresh the reference. The pure Kotlin engine has no IDE dependency. Editor fixture tests cover CSS/SCSS/XCSS insertion and completion; engine tests cover parsing, collisions, units, and catalog integrity.
