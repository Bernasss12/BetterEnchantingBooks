plugins {
    id("fabric-loom")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

version = property("mod_version") as String
group = property("maven_group") as String

val javaVersion = (property("java_version") as String).toInt()

base {
    archivesName = "${property("archives_base_name")}-${property("archives_minecraft_version")}"
}

repositories {
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.nucleoid.xyz/")
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = "${property("minecraft_version")}")
    mappings(group = "net.fabricmc", name = "yarn", version = "${property("yarn_mappings")}", classifier = "v2")

    modApi(group = "me.shedaniel.cloth", name = "cloth-config-fabric", version = "${property("cloth_config_version")}") {
        exclude("net.fabricmc.fabric-api")
    }

    modImplementation(group = "com.terraformersmc", name = "modmenu", version = "${property("modmenu_version")}") {
        exclude("net.fabricmc.fabric-api")
    }

    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = "${property("loader_version")}")
    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "${property("fabric_version")}")
    modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = "${property("fabric_kotlin_version")}")
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}

tasks.getByName<ProcessResources>("processResources") {
    val toExpand: MutableMap<String, String> = mutableMapOf(
        "version" to project.version.toString()
    )

    properties.entries.associateTo(toExpand) {
        it.key to it.value.toString()
    }

    setOf("fabric.mod.json", "bebooks.mixins.json").forEach {
        filesMatching(it) {
            expand(properties)
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
    options.release = javaVersion
}

java {
    targetCompatibility = JavaVersion.toVersion(javaVersion)
    withSourcesJar()
}

tasks.withType<Jar> {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}"}
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
