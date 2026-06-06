# Releasing

How to sign and publish **Markdown Table to CSV** to the [JetBrains Marketplace](https://plugins.jetbrains.com).

The build is already wired for signing and publishing. `build.gradle.kts` reads four values
from the environment:

| Variable | Purpose | Secret? |
| --- | --- | --- |
| `CERTIFICATE_CHAIN` | Public signing certificate chain | Public cert |
| `PRIVATE_KEY` | Encrypted RSA signing key | **Secret** |
| `PRIVATE_KEY_PASSWORD` | Passphrase for the key | **Secret** |
| `PUBLISH_TOKEN` | Marketplace upload token | **Secret** |

## Signing keys

The signing identity lives in `.secrets/` at the repo root. **The whole folder is gitignored**
(see `.gitignore`) — it must never be committed.

| File | What it is |
| --- | --- |
| `.secrets/private.pem` | 4096-bit RSA key, AES-256 encrypted |
| `.secrets/private_key_password.txt` | The key passphrase |
| `.secrets/chain.crt` | Self-signed certificate (valid until 2031) |
| `.secrets/signing.env` | Helper that exports the three signing vars for local use |

> **Back up `.secrets/` somewhere safe** (e.g. a password manager). It is gitignored, so it exists
> nowhere else. Losing it means regenerating the key and re-signing future releases.

### Regenerating the keys (only if lost)

```bash
mkdir -p .secrets && chmod 700 .secrets
openssl rand -base64 32 | tr -d '\n' > .secrets/private_key_password.txt
PW=$(cat .secrets/private_key_password.txt)
openssl genpkey -aes-256-cbc -algorithm RSA -out .secrets/private.pem \
  -pkeyopt rsa_keygen_bits:4096 -pass pass:"$PW"
openssl req -key .secrets/private.pem -passin pass:"$PW" -new -x509 -days 1825 \
  -out .secrets/chain.crt \
  -subj "/CN=Helge Sverre/O=Helge Sverre/C=NO/emailAddress=helge.sverre@gmail.com"
chmod 600 .secrets/*
```

See the JetBrains [plugin signing docs](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html).

## GitHub Actions secrets

Releases run in CI (`.github/workflows/release.yml`), which reads the four secrets from the repo's
**Settings → Secrets and variables → Actions**.

| Secret | Status |
| --- | --- |
| `CERTIFICATE_CHAIN` | ✅ set |
| `PRIVATE_KEY` | ✅ set |
| `PRIVATE_KEY_PASSWORD` | ✅ set |
| `PUBLISH_TOKEN` | ⏳ **not set** — add before the first automated publish |

The three signing secrets were uploaded with:

```bash
gh secret set PRIVATE_KEY          < .secrets/private.pem
gh secret set CERTIFICATE_CHAIN    < .secrets/chain.crt
gh secret set PRIVATE_KEY_PASSWORD < .secrets/private_key_password.txt
```

Add the publish token once you have it (from
[plugins.jetbrains.com/author/me/tokens](https://plugins.jetbrains.com/author/me/tokens)):

```bash
gh secret set PUBLISH_TOKEN --body '<your-marketplace-token>'
```

## Local commands

```bash
./gradlew test          # compile + run tests
./gradlew verifyPlugin  # IntelliJ Plugin Verifier against recommended IDEs
./gradlew buildPlugin   # unsigned ZIP  -> build/distributions/*.zip

# Signing / publishing need the env vars — source the helper first:
source .secrets/signing.env && ./gradlew signPlugin     # signed ZIP -> *-signed.zip
source .secrets/signing.env && ./gradlew publishPlugin  # sign + upload (needs PUBLISH_TOKEN)
```

## Versioning and changelog

- The plugin version is `version` in `gradle.properties`.
- Add notes for the next release under the `## [Unreleased]` section of `CHANGELOG.md`. They are
  rendered into the plugin's change-notes automatically at build time.
- `./gradlew patchChangelog` moves `[Unreleased]` into a dated version section.
- The release channel is derived from the version: a plain version (e.g. `0.1.0`) publishes to the
  **default (stable)** channel; a pre-release suffix like `1.0.0-beta.1` publishes to `beta`.

## First release (manual)

The **first upload of a new plugin must be done by hand** and goes through JetBrains moderation
(typically a couple of business days). Automated publishing only works after that.

1. `./gradlew verifyPlugin` — confirm it passes.
2. `source .secrets/signing.env && ./gradlew signPlugin`.
3. Upload `build/distributions/mdtable2csv-<version>-signed.zip` at
   [plugins.jetbrains.com → Upload plugin](https://plugins.jetbrains.com/plugin/add). Listing
   metadata (name, description, vendor, change-notes, icon) is taken from the plugin.
4. Wait for approval.

## Subsequent releases (automated)

Once the plugin exists on Marketplace and `PUBLISH_TOKEN` is set, releases are driven by GitHub
Releases:

1. Bump `version` in `gradle.properties` and update `CHANGELOG.md`; push to `main`.
2. The **Build** workflow (`build.yml`) builds, tests, verifies, and prepares a **draft GitHub
   Release** with notes from the changelog.
3. Edit and **publish** that draft release on GitHub.
4. The **Release** workflow (`release.yml`) triggers on publish, runs `./gradlew publishPlugin`
   (signs + uploads to Marketplace), attaches the ZIP to the release, and opens a PR with the
   patched changelog.

## CI workflows

| Workflow | Trigger | Does |
| --- | --- | --- |
| `build.yml` | push / PR | Build, test, `verifyPlugin`, prepare draft release |
| `release.yml` | a GitHub Release is published | `publishPlugin` → Marketplace, upload asset, changelog PR |

## Compatibility status

`verifyPlugin` reports **Compatible** for IntelliJ IDEA `252` (the `since-build`), `253`, `261`,
and `262`. Builds `261`/`262` note two deprecated-API usages (`runReadAction`) — informational only,
not a publish blocker.
