plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotzilla)
}

android {
    namespace = "com.davcres.rickandmorty"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.davcres.rickandmorty"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "env"
    productFlavors {
        create("pre") {
            dimension = "env"
            applicationIdSuffix = ".pre"
            versionNameSuffix = "-pre"
            resValue("string", "app_name", "Rick & Morty (Pre)")

            buildConfigField("String", "API_BASE_URL", "\"https://api-pre.example.com\"")
            buildConfigField("Boolean", "IS_PRO", "false")
            buildConfigField("long", "CACHE_L1_TTL_MINUTES", "2L")
            buildConfigField("long", "CACHE_L2_TTL_HOURS", "1L")
        }
        create("pro") {
            dimension = "env"
            resValue("string", "app_name", "Rick & Morty")

            buildConfigField("String", "API_BASE_URL", "\"https://api.example.com\"")
            buildConfigField("Boolean", "IS_PRO", "true")
            buildConfigField("long", "CACHE_L1_TTL_MINUTES", "5L")
            buildConfigField("long", "CACHE_L2_TTL_HOURS", "24L")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.di)
    implementation(projects.core)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.feature.characters)

    // Koin
    implementation(libs.koin.android)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)

    // Jetpack Compose
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.ui.tooling)

    // Testing
    testImplementation(libs.junit) // Unit testing
    androidTestImplementation(libs.junit.test) // Instrumented testing (on device)
    androidTestImplementation(libs.ui.test.junit4) // Compose testing
    debugImplementation(libs.ui.test.manifest) // Compose testing (create a mock manifest to run tests in debug)

    // Analytics
    implementation(libs.kotzilla.sdk.compose) // Kotzilla SDK
}

kotzilla {
    composeInstrumentation = true
}
