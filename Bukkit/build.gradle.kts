import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import info.preva1l.trashcan.description.paper.Permission
import info.preva1l.trashcan.description.paper.PluginLoadOrder
import info.preva1l.trashcan.description.paper.dependency
import info.preva1l.trashcan.setRemapped
import info.preva1l.trashcan.trashcan
import info.preva1l.trashcan.description.paper.PaperDependencyDefinition.RelativeLoadOrder as RLO

plugins {
    asz.common
}

trashcan {
    paper = true
}

repositories {
    maven(url = "https://repo.auxilor.io/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.codemc.io/repository/maven-snapshots/")
    maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven(url = "https://mvn-repo.arim.space/lesser-gpl3/")
    maven(url = "https://repo.rosewooddev.io/repository/public/")
    maven(url = "https://nexus.neetgames.com/repository/maven-releases/")
}

dependencies {
    implementation(project(":API"))
    trashcan()

    library(libs.redisson)

    dependency(libs.vault(), "Vault") { load = RLO.BEFORE ; required = false }
    dependency(libs.placeholderapi(), "PlaceholderAPI") { load = RLO.BEFORE ; required = false }
}

tasks.withType<ShadowJar> {
    relocate("info.preva1l.hooker", rootProject.group.toString() + ".hooks.lib")
    relocate("info.preva1l.trashcan", rootProject.group.toString() + ".trashcan")
}

paper {
    description = "A feature rich zoning/sharding system for paper."
    website = "https://docs.preva1l.info/"
    author = "Preva1l"
    main = rootProject.group.toString() + ".AdvancedServerZones"
    loader = rootProject.group.toString() +  ".trashcan.extension.libloader.BaseLibraryLoader"
    foliaSupported = true
    apiVersion = "1.21"

    load = PluginLoadOrder.STARTUP
}

operator fun Provider<MinimalExternalModuleDependency>.invoke(): String =
    get().let { "${it.module.group}:${it.module.name}:${it.version}" }