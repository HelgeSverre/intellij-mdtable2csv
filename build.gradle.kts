import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
}

// group and version are read from gradle.properties and applied to the project automatically.

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2025.2.6.2")

        // Bundled Markdown plugin — provides the Markdown PSI (tables, rows, cells) we read from.
        bundledPlugin("org.intellij.plugins.markdown")

        testFramework(TestFrameworkType.Platform)

        pluginVerifier()
        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        version = project.version.toString()

        // since-build only; until-build is intentionally omitted for broad forward compatibility.
        ideaVersion {
            sinceBuild = "252"
            untilBuild = provider { null }
        }

        // Pull the change notes for the version being built from CHANGELOG.md.
        val changelog = project.changelog
        changeNotes = project.provider {
            with(changelog) {
                renderItem(
                    (getOrNull(project.version.toString()) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // Mark pre-1.0 builds as a non-default release channel (e.g. 0.1.0 -> "beta").
        channels = providers.gradleProperty("version")
            .map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

// Launch the sandbox IDE with a project open by default, so the plugin can be
// dogfooded on real Markdown immediately. Defaults to ~/code/crescat; override
// with:  ./gradlew runIde -PrunIdeProject=/absolute/path/to/project
val runIdeProject: Provider<String> = providers.gradleProperty("runIdeProject")
    .orElse(providers.systemProperty("user.home").map { "$it/code/crescat" })

tasks.runIde {
    args(runIdeProject.get())
}
