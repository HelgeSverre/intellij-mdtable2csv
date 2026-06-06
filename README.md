# Markdown Table to CSV

[![Build](https://github.com/helgesverre/intellij-mdtable2csv/actions/workflows/build.yml/badge.svg)](https://github.com/helgesverre/intellij-mdtable2csv/actions/workflows/build.yml)

An IntelliJ Platform plugin that exports Markdown (GFM) tables to CSV without leaving the editor.
Place the caret inside a table, right-click **Markdown Table to CSV**, and convert it — copy to the
clipboard, open the result in a new tab, or save it to a `.csv` file.

## Features

- **Context-menu submenu** in the editor, shown only when the caret is inside a Markdown table.
- **Copy as CSV** — copy the table to the system clipboard.
- **Open in New CSV Tab** — render the table to a new in-memory editor tab (no disk write).
- **Save as CSV File…** — write the table to a `.csv` file via a save dialog.
- **Configurable delimiter** — comma, semicolon, or tab (Settings → Tools → Markdown Table to CSV).
- Correct RFC 4180 escaping for cells containing the delimiter, quotes, or newlines.

## Usage

1. Open a Markdown file and click inside any GFM table.
2. Right-click → **Markdown Table to CSV**, then choose **Copy as CSV**, **Open in New CSV Tab**, or
   **Save as CSV File…**.

The delimiter (comma, semicolon, or tab) is configurable under
**Settings → Tools → Markdown Table to CSV**.

## Installation

Once published, install from inside the IDE:
**Settings/Preferences → Plugins → Marketplace → search "Markdown Table to CSV" → Install**.

To install a local build, run `just build` (or `./gradlew buildPlugin`), then
**Settings/Preferences → Plugins → ⚙️ → Install Plugin from Disk…** and select the ZIP from
`build/distributions/`.

## How it works

- Kotlin + the [IntelliJ Platform Gradle Plugin](https://github.com/JetBrains/intellij-platform-gradle-plugin) 2.x.
- Reads tables from the bundled Markdown plugin's PSI (`org.intellij.plugins.markdown`).
- Targets IntelliJ Platform 2025.2 (`since-build 252`) and up, JVM toolchain 21.

## Development

This project uses [`just`](https://github.com/casey/just) as a command runner — run `just` to list
every recipe:

```bash
just test       # compile + run the test suite
just run        # launch a sandbox IDE with the plugin installed
just verify     # run the IntelliJ Plugin Verifier against recommended IDEs
just build      # produce a distributable ZIP under build/distributions
```

Every recipe wraps Gradle, so `./gradlew <task>` works too if you don't have `just`. See
[RELEASING.md](RELEASING.md) for signing and publishing to the JetBrains Marketplace.

## License

[MIT](LICENSE).
