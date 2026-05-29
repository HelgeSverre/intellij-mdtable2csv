# Markdown Table to CSV

An IntelliJ Platform plugin that exports Markdown (GFM) tables to CSV from inside the editor.
Right-click within a table and convert it to CSV — open the result in a new tab or save it to a `.csv` file.

> Status: early scaffold. The conversion action is not implemented yet — see [the plan](#development).

## Features (planned)

- **Context-menu action** in the editor, enabled only when the caret is inside a Markdown table.
- **Open as CSV** — render the table to a new in-editor CSV tab (no disk write).
- **Save as CSV** — write the table to a `.csv` file via a save dialog.
- Correct RFC 4180 escaping for cells containing commas, quotes, and newlines.

## Tech

- Kotlin + the [IntelliJ Platform Gradle Plugin](https://github.com/JetBrains/intellij-platform-gradle-plugin) 2.x.
- Reads tables from the bundled Markdown plugin's PSI (`org.intellij.plugins.markdown`).
- Targets IntelliJ Platform 2025.2 (`since build 252`), JVM toolchain 21.

## Development

```bash
./gradlew runIde     # launch a sandbox IDE with the plugin installed
./gradlew check      # run tests
./gradlew buildPlugin # produce a distributable ZIP under build/distributions
```

This project was scaffolded from the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).
