---
date: 2026-05-29
topic: markdown-table-to-csv
---

# Markdown Table to CSV — Requirements

## Summary

An IntelliJ editor action that converts the GFM Markdown table under the caret to CSV, offered through the editor's right-click menu with three targets: copy to clipboard, open in a new editor tab, and save to a `.csv` file. Cell text is preserved verbatim; the delimiter (comma/semicolon/tab) is a single saved preference.

---

## Problem Frame

Markdown tables are easy to write but awkward to get *out of* — moving their data into a spreadsheet or a data tool today means hand-reformatting pipes into commas, fixing quoting, and dealing with escaped characters by hand. The friction is small per table but recurring, and it happens right where the author already is: inside the editor. The plugin removes that step so a table can leave Markdown as well-formed CSV without leaving the editor.

---

## Key Decisions

- **Verbatim cells, not stripped.** Cell text is emitted exactly as authored (only table-mechanic escaping is normalized). This is lossless and predictable; a "strip formatting to plain text" mode is a deliberate later addition, not v1.
- **One saved delimiter preference, not a per-conversion choice.** The delimiter is set once (default comma) and all three actions use it. This keeps the menu to three simple items and serves `;`-locale spreadsheet users without a per-action prompt.
- **Three output targets, no bulk export.** Clipboard, new tab, and save-to-file cover the single-table workflow. Converting every table in a file at once is deferred.
- **Never silently drop data on ragged rows.** Short rows are padded to the header width; rows with extra cells keep them. Faithfulness beats producing a strict rectangle.
- **Context-menu activation gated on table position.** The action only appears when the caret is actually inside a Markdown table, so it doesn't clutter the menu elsewhere.

---

## Requirements

**Activation**

- R1. The plugin contributes an action group to the editor context (right-click) menu, enabled only when the caret is inside a GFM Markdown table; otherwise it is hidden or disabled.
- R2. Activation works in any file the bundled Markdown plugin parses as containing tables (e.g. `.md`, `.markdown`).

**Conversion model**

- R3. The header row and all body rows are converted. The alignment/separator row (`|---|:--:|`) is excluded from output.
- R4. Cell text is preserved verbatim, with one normalization: an escaped pipe `\|` becomes a literal `|`. Inline formatting (`**bold**`, `[label](url)`, `` `code` ``), `<br>`, and inline HTML are emitted as their literal source text — not rendered, stripped, or converted to newlines.
- R5. An empty cell produces an empty field.
- R6. For rows whose cell count differs from the header: rows with fewer cells are padded with empty trailing fields to the header width; rows with more cells keep all of them (that output line may exceed the header's column count). No cell content is dropped.

**Serialization**

- R7. Fields are joined with the configured delimiter using RFC 4180 quoting: a field is wrapped in double quotes only when it contains the delimiter, a double quote, CR, or LF; any embedded double quote is doubled (`"` → `""`).
- R8. Output is UTF-8 text.

**Output targets**

- R9. Copy to clipboard: place the CSV text on the system clipboard.
- R10. Open in new editor tab: open the CSV in a new in-memory editor document typed as `.csv`, with no file written to disk.
- R11. Save to file: present a save dialog defaulting to a `.csv` name and write the CSV to the chosen path.

**Delimiter preference**

- R12. The delimiter is a single persisted preference — comma, semicolon, or tab — defaulting to comma. All three output actions use the current value.
- R13. The preference is editable from a Settings/Preferences location.

---

## Acceptance Examples

- AE1. **Covers R4, R7 (special characters, comma delimiter).** A cell containing `a, b` serializes to `"a, b"`. A cell containing `say "hi"` serializes to `"say ""hi"""`. A cell containing a plain word serializes unquoted.
- AE2. **Covers R4 (escaped pipe).** A source cell written `A \| B` becomes the literal value `A | B`. With a comma/semicolon/tab delimiter the `|` needs no quoting, so it serializes as `A | B`.
- AE3. **Covers R4 (verbatim formatting).** A source cell `**bold** <br> more` serializes to the literal field `**bold** <br> more` on a single line — the `<br>` is not turned into an in-cell newline.
- AE4. **Covers R6 (ragged rows).** Given a 3-column header: a body row with 2 cells emits 3 fields (last empty); a body row with 4 cells emits 4 fields.
- AE5. **Covers R7, R12 (delimiter-relative quoting).** With the delimiter set to semicolon, a cell containing `;` is quoted while a cell containing `,` is not (the comma is just data when it isn't the delimiter).

---

## Scope Boundaries

**Deferred for later**

- Bulk export of every table in a file at once (and any multi-file / zip output).
- A "strip formatting to plain text" cell mode (`**bold**` → `bold`, `[label](url)` → `label`) as an alternative to verbatim.

**Outside this product's identity**

- Reverse conversion (CSV → Markdown table) and importing/opening external CSV files.
- Any spreadsheet-like editing of the table or the CSV.

---

## Dependencies / Assumptions

- Depends on the bundled Markdown plugin (`org.intellij.plugins.markdown`) for the table PSI; the dependency is already declared in the scaffold (`build.gradle.kts`, `src/main/resources/META-INF/plugin.xml`).
- Assumes the standard GFM table shape (header row + alignment row + body rows) as parsed by that plugin.

---

## Outstanding Questions (Deferred to Planning)

- Line-ending policy for serialized output (LF vs RFC 4180 CRLF; whether to honor the document's line-separator setting).
- Scope of the delimiter preference (application-level vs per-project) and exactly where it surfaces in Settings.
- Default filename for "Save to file" (derive from the source document name vs a fixed default).
- Caret-resolution rule when tables are adjacent or nested in other structures (assumed: the innermost enclosing table).
