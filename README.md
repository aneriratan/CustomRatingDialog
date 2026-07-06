# CustomRatingDialog

A lightweight, dependency-free custom rating dialog for Android, published via
[JitPack](https://jitpack.io).

## Use it in your app

**1. Add the JitPack repository** — in your consuming project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**2. Add the dependency** — in your app module's `build.gradle.kts`.
Replace `<USER>` with your GitHub username and `<TAG>` with a released tag
(e.g. `1.0.0`):

```kotlin
implementation("com.github.<USER>.CustomRatingDialog:ratingdialog:<TAG>")
```

> This is a multi-module repo (`app` + `ratingdialog`), so the coordinate is
> `com.github.<USER>.<REPO>:<MODULE>:<TAG>` — group ends in the repo name and the
> artifact is the module name `ratingdialog`.

## Usage

```kotlin
CustomRatingDialog.Builder(context)
    .title("Enjoying the app?")
    .message("Tap a star to rate your experience")
    .onRatingSubmitted { rating -> /* handle the rating */ }
    .show()
```

## Publishing a new version

1. Bump the version and commit.
2. Create and push a git tag: `git tag 1.0.1 && git push origin 1.0.1`.
3. On [jitpack.io](https://jitpack.io), enter your repo and click **Get it** on
   the tag to trigger the first build. JitPack builds the AAR on demand — no
   Maven server or credentials required.
