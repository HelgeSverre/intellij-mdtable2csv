# mdtable2csv

IntelliJ plugin to convert a Markdown (GFM) table under the caret to CSV — copy
to clipboard, open in a new tab, or save to a `.csv` file. Kotlin + IntelliJ
Platform Gradle Plugin 2.x, depends on the bundled `org.intellij.plugins.markdown`.

## Build & test

```bash
./gradlew test         # compile + run the test suite
./gradlew runIde       # launch a sandbox IDE (opens ~/code/crescat by default)
./gradlew buildPlugin   # produce build/distributions/*.zip
```

## Documented solutions

`docs/solutions/` — documented solutions to past problems (bugs, conventions,
tooling decisions), organized by category with YAML frontmatter (`module`,
`tags`, `problem_type`). Relevant when implementing or debugging in documented
areas. Two learnings live there already: the Markdown table PSI ragged-row
behavior, and the Settings-UI `Configurable` vs Kotlin UI DSL decision.

## Project docs

- `docs/brainstorms/` — requirements docs (the "what")
- `docs/plans/` — implementation plans (the "how")
