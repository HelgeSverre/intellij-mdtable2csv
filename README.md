# Markdown Table to CSV

An IntelliJ Platform plugin that exports Markdown (GFM) tables to CSV from inside the editor.
Place the caret inside a table, right-click, and convert it — copy to the clipboard, open the
result in a new tab, or save it to a `.csv` file.

## Features

- **Context-menu submenu** in the editor, shown only when the caret is inside a Markdown table.
- **Copy as CSV** — copy the table to the system clipboard.
- **Open in New CSV Tab** — render the table to a new in-memory editor tab (no disk write).
- **Save as CSV File…** — write the table to a `.csv` file via a save dialog.
- **Configurable delimiter** — comma, semicolon, or tab (Settings → Tools → Markdown Table to CSV).
- Correct RFC 4180 escaping for cells containing the delimiter, quotes, or newlines.

## Tech

- Kotlin + the [IntelliJ Platform Gradle Plugin](https://github.com/JetBrains/intellij-platform-gradle-plugin) 2.x.
- Reads tables from the bundled Markdown plugin's PSI (`org.intellij.plugins.markdown`).
- Targets IntelliJ Platform 2025.2 (`since-build 252`), JVM toolchain 21.

## Development

```bash
./gradlew test         # compile + run the test suite
./gradlew runIde       # launch a sandbox IDE with the plugin installed
./gradlew verifyPlugin # run the IntelliJ Plugin Verifier against recommended IDEs
./gradlew buildPlugin  # produce a distributable ZIP under build/distributions
```

### Publishing

Signing and publishing read their secrets from environment variables, so nothing sensitive lives in
the repo:

```bash
CERTIFICATE_CHAIN=…  PRIVATE_KEY=…  PRIVATE_KEY_PASSWORD=…  PUBLISH_TOKEN=…  ./gradlew publishPlugin
```

The first Marketplace upload must be done manually; automated `publishPlugin` releases apply only
after the plugin has been published once.

This project was scaffolded from the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).
