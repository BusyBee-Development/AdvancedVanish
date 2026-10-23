plugins {
    java
}

group = "net.busybee.advancedvanish"
version = "26.10.0"
description = "A vanish plugin for Minecraft Paper and Folia servers."

// Compile against a newer API to check nothing used was removed, e.g.
//   ./gradlew compileJava -PpaperApi=26.2.build.134-stable -PjavaRelease=25
// Release builds always use the defaults below.
val paperApi = providers.gradleProperty("paperApi").getOrElse("1.21.1-R0.1-SNAPSHOT")
val javaRelease = providers.gradleProperty("javaRelease").getOrElse("21").toInt()

// Bump when a config.yml key is added, removed or renamed. Older configs are backed up and merged.
val configVersion = 10

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://repo.mikeprimm.com/")
    maven("https://mvn.lib.co.nz/public/")
    maven("https://repo.rosewooddev.io/repository/public/")
}

dependencies {
    // Server API: the lowest version the plugin supports (api-version in paper-plugin.yml)
    compileOnly("io.papermc.paper:paper-api:$paperApi")

    // Runtime libraries, downloaded by AdvancedVanishLoader (keep the versions in step with it)
    compileOnly("redis.clients:jedis:7.5.0")
    compileOnly("com.zaxxer:HikariCP:7.0.2")
    compileOnly("org.apache.logging.log4j:log4j-core:2.22.1")

    // Soft-dependency APIs
    compileOnly("net.luckperms:api:5.4")
    compileOnly("net.essentialsx:EssentialsX:2.21.2") { isTransitive = false }
    compileOnly("me.clip:placeholderapi:2.11.6") { isTransitive = false }
    compileOnly("com.discordsrv:discordsrv:1.30.5") { isTransitive = false }
    compileOnly("us.dynmap:dynmap-api:3.6") { isTransitive = false }
    compileOnly("us.dynmap:DynmapCoreAPI:3.6") { isTransitive = false }
    compileOnly("xyz.jpenilla:squaremap-api:1.2.7") { isTransitive = false }
    compileOnly("me.libraryaddict.disguises:libsdisguises:11.0.18") { isTransitive = false }
    compileOnly("dev.esophose:playerparticles:8.6") { isTransitive = false }

    // Test
    testImplementation("io.papermc.paper:paper-api:$paperApi")
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // Java 21 bytecode loads on 1.21.x (Java 21) and 26.x (Java 25) servers alike
    options.release.set(javaRelease)
    options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:removal"))
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version.toString(),
        "description" to project.description.toString(),
        "configVersion" to configVersion.toString(),
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
    // Plain token replacement: config.yml is full of MiniMessage tags that a template engine would misread
    filesMatching("config.yml") {
        filter { line ->
            line.replace("%CONFIG_VERSION%", props.getValue("configVersion"))
                .replace("%PROJECT_VERSION%", props.getValue("version"))
        }
    }
}

tasks.jar {
    archiveFileName.set("AdvancedVanish-${project.version}.jar")
}

tasks.test {
    useJUnitPlatform()
}
