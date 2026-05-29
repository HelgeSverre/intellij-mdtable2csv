---
title: Prefer a plain Configurable over Kotlin UI DSL buttonsGroup.bind for small IntelliJ settings
date: 2026-05-29
category: tooling-decisions
module: intellij-plugin
problem_type: tooling_decision
component: tooling
applies_when:
  - "Building an IntelliJ plugin Settings page without fast SDK introspection or quick IDE runs"
  - "Binding a small enum preference (a handful of radio buttons) to a PersistentStateComponent"
tags: [intellij-platform, settings, configurable, kotlin-ui-dsl, plugin]
---

# Prefer a plain Configurable over Kotlin UI DSL buttonsGroup.bind for small IntelliJ settings

## Context

Building a Settings page for an IntelliJ plugin to bind a small enum preference (a CSV delimiter: comma / semicolon / tab) to an application-level `PersistentStateComponent`. The Kotlin UI DSL v2 `buttonsGroup { ... }.bind(...)` looked like the idiomatic choice, but its binding overload could not be verified by inspection and cost two full compile cycles to get wrong.

## Guidance

When you cannot quickly introspect the IntelliJ SDK (no reliable autocomplete against the resolved platform, and Gradle compile cycles are slow because they pull the platform), prefer implementing `com.intellij.openapi.options.Configurable` directly with plain Swing for small settings surfaces, instead of guessing Kotlin UI DSL binding overloads.

```kotlin
class MyConfigurable : Configurable {
    private val settings get() = MySettings.getInstance()
    private val buttons = LinkedHashMap<MyEnum, JRadioButton>()

    override fun getDisplayName() = "..."
    override fun createComponent(): JComponent { /* JRadioButton + ButtonGroup in a JPanel */ }
    override fun isModified() = selected() != settings.value
    override fun apply() { settings.value = selected() }
    override fun reset() { buttons.forEach { (v, b) -> b.isSelected = v == settings.value } }
}
```

## Why This Matters

Each wrong guess at a DSL binding signature costs a full Gradle compile — tens of seconds to minutes when the IntelliJ platform SDK is on the classpath. The plain `Configurable` path uses only stable, decades-old Swing + `Configurable` APIs, compiles first try, and is trivially testable: `isModified`/`apply`/`reset` are pure logic over the components. The user-visible UX is identical (radio buttons under the chosen settings group).

## When to Apply

- Small settings surfaces (a few fields) where the Kotlin UI DSL's brevity is not worth the binding-signature risk.
- Environments where you cannot verify the DSL API against the resolved SDK version before committing (headless agents, slow first builds).

## Examples

Before — did not compile (wrong `bind` shape; the overload wanted a property plus an explicit type, and a lambda form also tripped the configuration cache):

```kotlin
buttonsGroup(label) {
    MyEnum.entries.forEach { row { radioButton(it.displayName, it) } }
}.bind(settings::delimiter)   // error: "No value passed for parameter 'type'"
```

After — plain `Configurable`, compiled first try (see the Guidance snippet).

If you do choose the Kotlin UI DSL, the working enum-radio shape is `buttonsGroup { ... }.bind(prop, MyEnum::class.java)` — but verify it against your exact SDK version first, and avoid capturing build-script references inside argument-provider lambdas (configuration-cache will reject them).

## Related

- `docs/solutions/integration-issues/intellij-markdown-table-psi-ragged-rows.md` — another learning from the same plugin build.
