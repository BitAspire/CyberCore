import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.4.1"
}

group = "com.bitaspire"
version = "2.0.0"

repositories {
    mavenCentral()
    mavenLocal()

    flatDir { dirs("libraries") }

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://croabeast.github.io/repo/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2-1")
    annotationProcessor("org.jetbrains:annotations:26.0.2-1")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    implementation("me.croabeast.takion:shaded:1.6.0:all")
    implementation("commons-lang:commons-lang:2.6")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<Javadoc>().configureEach {
    isFailOnError = false

    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:none", "-quiet")
        encoding = "UTF-8"
        charSet = "UTF-8"
        docEncoding = "UTF-8"

        if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_1_9))
            addBooleanOption("html5", true)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.compilerArgs.add("-Xlint:-options")
}

tasks.withType<ShadowJar>().configureEach {
    archiveClassifier.set("")

    relocate("com.github.stefvanschie.inventoryframework", "com.bitaspire.libs.inventory")
    relocate("me.croabeast", "com.bitaspire.libs")
    relocate("com.mojang", "com.bitaspire.libs.mojang")

    exclude(
        "META-INF/**",
        "kotlin/**",
        "org/intellij/**",
        "com/google/**",
        "google/**",
        "org/checkerframework/**",
        "org/jetbrains/**"
    )
}

tasks.build {
    dependsOn("shadowJar")
}

val buildTakionShadowJar by tasks.registering(Exec::class) {
    group = "build"
    description = "Builds the local Takion shaded jar with the all classifier used by CyberCore."

    if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
        commandLine("cmd", "/c", "gradlew.bat", ":shaded:allShadowJar")
    } else {
        commandLine("./gradlew", ":shaded:allShadowJar")
    }
}

val workspaceDir: File = rootDir.parentFile
val cyberCoreConsumers = listOf(
    workspaceDir.resolve("CyberLevels/libraries"),
    workspaceDir.resolve("CyberLogin/libraries"),
    workspaceDir.resolve("CyberStatistics/libraries"),
    workspaceDir.resolve("CyberTokens/libraries"),
    workspaceDir.resolve("XenoEventEngine/libraries"),
    workspaceDir.resolve("XenoLevels/libraries"),
    workspaceDir.resolve("XenoRegionReset/libraries"),
    workspaceDir.resolve("XenoStatistics/libraries"),
    workspaceDir.resolve("XenoTokens/libraries")
)

val syncCyberCoreToLocalConsumers by tasks.registering {
    group = "distribution"
    description = "Copies the CyberCore jar to local consumer libraries folders."

    val shadowJarTask = tasks.named<ShadowJar>("shadowJar")
    val cyberCoreJar = shadowJarTask.flatMap { it.archiveFile }

    dependsOn("jar", "shadowJar")
    inputs.file(cyberCoreJar)
    outputs.files(cyberCoreConsumers.map { it.resolve("CyberCore-2.0.0.jar") })

    doLast {
        val sourceJar = cyberCoreJar.get().asFile

        cyberCoreConsumers.forEach { targetDir ->
            targetDir.mkdirs()

            copy {
                from(sourceJar)
                into(targetDir)
                rename { "CyberCore-2.0.0.jar" }
            }
        }
    }
}

tasks.named("build") {
    dependsOn(syncCyberCoreToLocalConsumers)
}
