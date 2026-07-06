# Publish an Android Library to a Custom Maven Repo — Full Reference

Reusable prompt/checklist for turning any Android **library module** into a
published Maven dependency and consuming it in other apps.

> Fill these in before you start:
> - **namespace / package**: `com.custom.ratingdialog`
> - **groupId**: `com.custom`
> - **artifactId**: `ratingdialog`
> - **version**: `1.0.0`
> - **repo URL**: `https://maven.example.com/releases`

---

## Step 1 — Confirm module structure

It must be a **library** module (`com.android.library`), not an app module.

```
YourProject/
├── app/
├── ratingdialog/          ← library module
│   ├── build.gradle.kts
│   └── src/main/...
└── settings.gradle.kts
```

`settings.gradle.kts`:

```kotlin
include(":app")
include(":ratingdialog")
```

---

## Step 2 — Library `build.gradle.kts`

```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.custom.ratingdialog"
    // ... your existing config (compileSdk, defaultConfig, etc.)

    publishing {
        singleVariant("release") {
            withSourcesJar()   // optional — remove if you don't want sources published
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId    = "com.custom"
                artifactId = "ratingdialog"
                version    = "1.0.0"
            }
        }
        repositories {
            maven {
                name = "custom"
                url  = uri("https://maven.example.com/releases")
                credentials {
                    username = providers.gradleProperty("mavenUser").get()
                    password = providers.gradleProperty("mavenPass").get()
                }
                // Only if the repo is plain HTTP (not HTTPS):
                // isAllowInsecureProtocol = true
            }
        }
    }
}
```

> The publish task name is derived from the publication + repository names:
> `publish<Publication>PublicationTo<Repository>Repository`
> → here: `publishReleasePublicationToCustomRepository`.

---

## Step 3 — Credentials (global, never in the repo)

`~/.gradle/gradle.properties` (outside the project, not committed to git):

```properties
mavenUser=your_user
mavenPass=your_pass
```

---

## Step 4 — Build test (before publishing)

```bash
./gradlew :ratingdialog:assembleRelease
```

Only continue when this is green. Fix any build error here first.

---

## Step 5 — Publish

```bash
./gradlew :ratingdialog:publishReleasePublicationToCustomRepository
```

This generates `.aar` + `.pom` (+ sources jar) and PUTs them to the Maven repo.

---

## Step 6 — Verify on the repo

Open in a browser:

```
https://maven.example.com/releases/com/custom/ratingdialog/1.0.0/
```

You should see `ratingdialog-1.0.0.aar` and `ratingdialog-1.0.0.pom`.

---

## Step 7 — Consume in another app

```kotlin
// settings.gradle.kts
repositories {
    maven { url = uri("https://maven.example.com/releases") }
}

// app/build.gradle.kts
implementation("com.custom:ratingdialog:1.0.0")
```

Sync → done.

---

## Gotchas

- **Immutable versions** — once `1.0.0` is published it can't be overwritten.
  Bump the version on every change: `1.0.1`, `1.0.2`, …
- **401 / 403** — wrong credentials, or wrong WebDAV PUT path. Check the property
  values and the repo URL path segment (`/releases` vs `/snapshots` vs root).
- **WebDAV PUT fails** — some servers need `dav_svn` / correct `Location` block,
  and the auth (`htpasswd`) must sit on the right path. Verify PUT works with a
  manual `curl -u user:pass -T file.txt <url>` before blaming Gradle.
- **Consumer can't resolve** — make sure the consumer's `repositories` block
  points at the same base URL (not the full artifact path), and the groupId/
  artifactId/version exactly match what was published.
- **Sources not needed** — drop `withSourcesJar()` to publish only the `.aar`.

---

## Quick reuse prompt

> I have an Android library module `<package>` and want to publish it to my
> Maven repo at `<repo URL>` as `<groupId>:<artifactId>:<version>`, then consume
> it in another app. Give me the `maven-publish` `build.gradle.kts` block, the
> global credentials setup, the publish command, and the consumer-side snippet.
