plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.7.0-Alpha")
}
