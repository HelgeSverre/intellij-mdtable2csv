# Markdown Table to CSV — IntelliJ plugin build & release tasks

# Show available recipes
default:
    @just --list

# Compile and run the test suite
test:
    ./gradlew test

# Launch a sandbox IDE with the plugin installed (optional: path to a project to open)
run project="":
    ./gradlew runIde {{ if project != "" { "-PrunIdeProject=" + project } else { "" } }}

# Run the IntelliJ Plugin Verifier against recommended IDEs
verify:
    ./gradlew verifyPlugin

# Build a distributable ZIP under build/distributions
build:
    ./gradlew buildPlugin

# Test + verify — the pre-release gate
check: test verify

# Build a signed ZIP (needs .secrets/signing.env)
sign:
    #!/usr/bin/env bash
    set -euo pipefail
    source .secrets/signing.env
    ./gradlew signPlugin

# Sign and publish to the JetBrains Marketplace (needs .secrets/signing.env + PUBLISH_TOKEN)
publish:
    #!/usr/bin/env bash
    set -euo pipefail
    source .secrets/signing.env
    ./gradlew publishPlugin

# Remove build artifacts
clean:
    ./gradlew clean
