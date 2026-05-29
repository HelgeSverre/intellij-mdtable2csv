import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2025.2.6.2")

        // Bundled Markdown plugin — provides the Markdown PSI (tables, rows, cells) we read from.
        bundledPlugin("org.intellij.plugins.markdown")

        testFramework(TestFrameworkType.Platform)
    }
}

// Launch the sandbox IDE with a project open by default, so the plugin can be
// dogfooded on real Markdown immediately. Defaults to ~/code/crescat; override
// with:  ./gradlew runIde -PrunIdeProject=/absolute/path/to/project
val runIdeProject: Provider<String> = providers.gradleProperty("runIdeProject")
    .orElse(providers.systemProperty("user.home").map { "$it/code/crescat" })

tasks.runIde {
    args(runIdeProject.get())
}
