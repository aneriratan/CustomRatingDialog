plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

// Local-only default coordinates. Assigned via a conditional so they never
// clobber the `-Pgroup` / `-Pversion` that JitPack injects at build time
// (JitPack sets group = `com.github.<user>.<repo>` and version = the git tag).
if (group.toString().isBlank() || group.toString() == rootProject.name) {
    group = "com.custom"
}
if (version.toString() == Project.DEFAULT_VERSION) {
    version = "1.0.0"
}

android {
    namespace = "com.custom.ratingdialog"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                // groupId and version intentionally omitted — they inherit from
                // project.group / project.version above, which JitPack overrides
                // at build time with your GitHub coordinates and the git tag.
                artifactId = "ratingdialog"
            }
        }
        // No `repositories {}` block: JitPack builds the repo itself and reads the
        // artifact straight from `publishToMavenLocal`. No Maven server or
        // credentials are needed.
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
