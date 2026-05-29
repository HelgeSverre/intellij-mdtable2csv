---
title: IntelliJ Markdown table PSI normalizes ragged rows (extra cells are dropped)
date: 2026-05-29
category: integration-issues
module: intellij-plugin
problem_type: integration_issue
component: tooling
symptoms:
  - "A Markdown table row with more cells than the header exposes only header-width cells via the MarkdownTable PSI"
  - "A planned keep-all-extra-cells behavior for ragged rows cannot be implemented because the over-long cells never reach the extractor"
root_cause: wrong_api
resolution_type: code_fix
severity: medium
tags: [intellij-platform, markdown, psi, gfm, tables, plugin]
---

# IntelliJ Markdown table PSI normalizes ragged rows (extra cells are dropped)

## Problem

When reading a GFM table through the bundled IntelliJ Markdown plugin's PSI (`org.intellij.plugins.markdown`), cells beyond the header's column count are dropped at parse time — before your code ever sees them. A plugin therefore cannot preserve the "extra" cells of an over-long row.

## Symptoms

- A row like `| 1 | 2 | 3 | 4 |` under a 3-column header is exposed as only **3** `MarkdownTableCell` children, not 4.
- Short rows (fewer cells than the header) *do* arrive with their actual smaller cell count, so the asymmetry is easy to miss — short-row handling looks correct while over-long-row handling silently "works" by losing data.
- A ragged-row unit test fails with something like `expected:<[..., [1, 2, 3, 4]]> but was:<[..., [1, 2, 3]]>`.

## What Didn't Work

- Specifying "rows with more cells than the header keep all of them; no content dropped" and implementing a pad-or-keep branch. The pad-short half works, but the keep-extra half is unreachable: `PsiTreeUtil.getChildrenOfTypeAsList(row, MarkdownTableCell::class.java)` only returns the cells the parser kept, and the parser truncates to the header width.

## Solution

Treat the header row's cell count as the table width and normalize every row to it — pad short rows, and accept that over-long rows already arrive truncated:

```kotlin
val rows = PsiTreeUtil.getChildrenOfTypeAsList(table, MarkdownTableRow::class.java)
if (rows.isEmpty()) return emptyList()
val width = cellsOf(rows.first()).size                 // header defines the width
rows.map { row -> padToWidth(cellsOf(row), width) }    // pad short; long rows are already == width
```

Iterating `MarkdownTableRow` children also excludes the `|---|` separator for free: it is a `MarkdownTableSeparatorRow` **leaf**, not a `MarkdownTableRow`, so it never appears among the rows and needs no special-casing.

## Why This Works

The IntelliJ Markdown parser implements GFM table semantics, where cells beyond the header count are not part of the table and are discarded during parsing. The PSI reflects the parsed (spec-normalized) tree, not the raw source text, so the extra cells never become `MarkdownTableCell` nodes. Re-parsing the raw row text to recover them would produce non-spec output and is rarely what a user wants — aligning to the parser is both simpler and correct.

## Prevention

- Write a `BasePlatformTestCase` + `myFixture.configureByText("t.md", ...)` fixture test for ragged rows **before** finalizing extraction expectations. It surfaces the parser's normalization immediately instead of at integration time.
- In plans/specs for GFM tables, do not promise "keep extra cells" — the parser layer makes it impossible. Specify "normalize to the header width (pad short rows; over-long rows are truncated by the parser)" instead.

## Related Issues

- `docs/solutions/tooling-decisions/intellij-settings-ui-configurable-vs-kotlin-ui-dsl.md` — another learning from the same plugin build.
