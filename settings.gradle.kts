rootProject.name = "CyberCore"

// Builds Takion from source and substitutes it for me.croabeast.takion:shaded, so CyberCore can be
// developed against unpublished Takion changes. Locally the checkout is a sibling folder; CI checks
// it out inside this repository instead. When neither path exists the build falls back to the
// published artifact.
listOf("../Takion", "Takion")
    .map(::file)
    .firstOrNull { it.resolve("settings.gradle.kts").isFile }
    ?.let(::includeBuild)
