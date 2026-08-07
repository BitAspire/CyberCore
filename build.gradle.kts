import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.4.1"
}

group = "com.bitaspire"
version = "2.1.0"

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

    implementation("me.croabeast.takion:shaded:2.0.0:all")
    implementation("commons-lang:commons-lang:2.6")
    implementation("org.bstats:bstats-bukkit:3.0.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("failed")
    }
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

// Consumers compile against the relocated packages (com.bitaspire.libs.*), which only exist in the
// shaded jar. Composite builds hand over a project's plain classes by default, so the outgoing
// artifact is swapped here; otherwise every plugin that includes this build stops resolving its
// imports the moment it switches from the copied jar to the substitution.
configurations {
    // The secondary "classes" variant has to go too: compile classpaths prefer it over the jar for
    // compile avoidance, and it points at the unrelocated build/classes directory.
    //
    // setExtendsFrom(emptySet()) stops advertising the dependencies: the shaded jar already carries
    // them relocated, so a consumer that also resolved them would shade a second, unrelocated copy
    // of Takion, Prismatic, VNC and friends into its own jar.
    apiElements {
        setExtendsFrom(emptySet())
        outgoing.artifacts.clear()
        outgoing.variants.removeIf { true }
        outgoing.artifact(tasks.named<ShadowJar>("shadowJar"))
    }
    runtimeElements {
        setExtendsFrom(emptySet())
        outgoing.artifacts.clear()
        outgoing.variants.removeIf { true }
        outgoing.artifact(tasks.named<ShadowJar>("shadowJar"))
    }
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

