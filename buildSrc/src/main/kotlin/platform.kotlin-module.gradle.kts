@file:Suppress("PropertyName")

import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.ExcludeTransitiveDependenciesFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.CsvReportRenderer
import com.github.jk1.license.render.InventoryHtmlReportRenderer
import com.github.jk1.license.render.ReportRenderer
import com.github.jk1.license.render.TextReportRenderer

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
    id("com.appmattus.markdown")
    id("com.github.jk1.dependency-license-report")
}

val jdk_version: String by project
val instruction_coverage: String by project
val disable_coverage: String by project
val logback_version: String = "1.4.5"
val version: String by project

repositories {
    mavenCentral()
    gradlePluginPortal()
}

group = "com.deserve.lib"

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(jdk_version))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = jdk_version
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
}

tasks.test {
    testLogging {
        exceptionFormat = FULL
    }
    useJUnitPlatform()
}

kover {
    disabledForProject = System.getProperty("disable_coverage")?.toBoolean() ?: false

    useKoverTool()
}

koverReport {
    xml {
        onCheck = true // true to run koverMergedXmlReport task during the execution of the check task (if it exists) of the current project
    }

    html {
        onCheck = true // true to run koverMergedHtmlReport task during the execution of the check task (if it exists) of the current project
    }

    verify {
        onCheck = true // true to run koverMergedVerify task during the execution of the check task (if it exists) of the current project
        rule("Minimal instruction coverage rate in percent") { // add verification rule
            bound {
                minValue = Integer.valueOf(instruction_coverage)
                metric = kotlinx.kover.gradle.plugin.dsl.MetricType.INSTRUCTION // change coverage metric to evaluate (LINE, INSTRUCTION, BRANCH)
                aggregation = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE // change counter value (COVERED_COUNT, MISSED_COUNT, COVERED_PERCENTAGE, MISSED_PERCENTAGE)
            }
            minBound(45)
        }
    }
}

licenseReport {
    renderers = arrayOf<ReportRenderer>(
        InventoryHtmlReportRenderer("report.html", "Backend"), CsvReportRenderer(),
        TextReportRenderer()
    )
    filters = arrayOf<DependencyFilter>(
        LicenseBundleNormalizer(), ExcludeTransitiveDependenciesFilter()
    )
}
