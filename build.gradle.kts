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

    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.290-kotlin-1.6.10")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.290-kotlin-1.6.10")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-css:17.0.2-pre.290-kotlin-1.6.10")
//    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:6.2.1-pre.290-kotlin-1.6.10")
//    implementation("org.jetbrains.kotlin-wrappers:kotlin-redux:4.1.2-pre.290-kotlin-1.6.10")
//    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:7.2.6-pre.290-kotlin-1.6.10")
//    implementation("com.benasher44:uuid:0.4.0")

//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
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
