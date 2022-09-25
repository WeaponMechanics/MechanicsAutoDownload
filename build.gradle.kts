import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "me.cjcrafter"
version = "1.2.3"

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

configurations {
    compileClasspath.get().extendsFrom(create("shadeOnly"))
}

// See https://github.com/Minecrell/plugin-yml
bukkit {
    main = "me.cjcrafter.auto.ExamplePlugin"
    apiVersion = "1.13"

    authors = listOf("CJCrafter")
    softDepend = listOf("MechanicsCore", "WeaponMechanics", "ArmorMechanics")
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.9.0")
}

tasks.named<ShadowJar>("shadowJar") {
    classifier = null
    archiveFileName.set("MechanicsAutoDownload-${project.version}.jar")
    configurations = listOf(project.configurations["shadeOnly"], project.configurations["runtimeClasspath"])

    dependencies {
        relocate("com.google.gson", "me.cjcrafter.auto.gson") {
            include(dependency("com.google.code.gson::"))
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(8)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/WeaponMechanics/MechanicsAutoDownload")
            credentials {
                username = findProperty("user").toString()
                password = findProperty("pass").toString()
            }
        }
    }
    publications {
        create<MavenPublication>("publication") {
            artifact(tasks.named("shadowJar")) {
                classifier = null
            }

            pom {
                groupId = "me.cjcrafter"
                artifactId = "mechanicsautodownload" // MUST be lowercase
                packaging = "jar"
            }
        }
    }
}