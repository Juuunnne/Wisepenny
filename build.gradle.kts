import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    // Declared once here so the :server module can apply them without the Kotlin
    // plugin classpath clashing with :composeApp's multiplatform plugin.
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    // Static analysis / formatting — applied to every module in the subprojects block below.
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    // Gradual adoption: report issues but don't fail `check` / `build` yet.
    // Tighten (remove ignoreFailures) once the existing code is cleaned up.
    configure<DetektExtension> {
        buildUponDefaultConfig = true
        ignoreFailures = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    }
    configure<KtlintExtension> {
        ignoreFailures.set(true)
    }
}
