plugins {
    kotlin("js") version "1.7.21"
    kotlin("plugin.serialization").version("1.7.22")
}

group = "me.safar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.4")
    implementation(npm("html2canvas", "1.4.1"))
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation(npm("@js-joda/timezone", "2.3.0"))

    val ktorVersion = "2.2.1"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-js:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    // In 1.7.20:
                    enabled = true
                    // In 1.8.0:
//                    enabled.set(true)
                }
            }
        }
        tasks.named("browserDevelopmentRun") {
            finalizedBy(":developmentExecutableCompileSync")
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.RequiresOptIn")
    }
}
