import dev.kikugie.stonecutter.StonecutterSettings

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/releases")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.4.2"
}

extensions.configure<StonecutterSettings> {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        vers("1.20.1", "1.20.1")
        vers("1.20.4", "1.20.4")
        vers("1.21", "1.21")

        vcsVersion = "1.21"
    }

    create(rootProject)
}
