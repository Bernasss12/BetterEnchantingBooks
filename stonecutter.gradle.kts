plugins {
    id("dev.kikugie.stonecutter")
    id("org.jetbrains.kotlin.jvm") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
    id("fabric-loom") version "1.7-SNAPSHOT" apply false
}
stonecutter active "1.21" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}