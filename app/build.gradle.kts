import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use(::load)
    }
}

android {
    namespace = "eu.de_gouveia.callblocker"
    compileSdk = 36

    defaultConfig {
        applicationId = "eu.de_gouveia.callblocker"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.test.InstrumentationTestRunner"
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("personalRelease") {
                storeFile = rootProject.file(requireNotNull(keystoreProperties["storeFile"]))
                storePassword = requireNotNull(keystoreProperties["storePassword"]).toString()
                keyAlias = requireNotNull(keystoreProperties["keyAlias"]).toString()
                keyPassword = requireNotNull(keystoreProperties["keyPassword"]).toString()
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("personalRelease")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
