plugins {
    kotlin("js") version "1.6.10"
}

group = "me.safar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.1")
    implementation(npm("html2canvas", "1.4.1"))
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.RequiresOptIn")
    }
}
