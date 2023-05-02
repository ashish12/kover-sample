@file:Suppress("PropertyName")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
}

val jdk_version = "17"
val disable_coverage: String by project

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
    if (System.getProperty("disable_coverage")?.toBoolean() == true) {
        disable()
    }

    useKoverTool()
}

koverReport {
    defaults {
        xml {
            onCheck = true
        }

        html {
            onCheck = true
        }

        verify {
            onCheck = true
            rule("Minimal instruction coverage rate in percent") { // add verification rule
                bound {
                    metric = kotlinx.kover.gradle.plugin.dsl.MetricType.INSTRUCTION // change coverage metric to evaluate (LINE, INSTRUCTION, BRANCH)
                    aggregation = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE // change counter value (COVERED_COUNT, MISSED_COUNT, COVERED_PERCENTAGE, MISSED_PERCENTAGE)
                }
                minBound(45)
            }
        }
    }
}
