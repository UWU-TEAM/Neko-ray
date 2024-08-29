import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.mikepenz.aboutlibraries.plugin")
}

configurations {
    create("defaultRuntimeOnly")
}

android {
    namespace = "com.neko.v2ray"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.neko.v2ray"
        minSdk = 27
        targetSdk = 34
        versionCode = 230
        versionName = "1.1.10"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
        splits {
            abi {
                isEnable = true
                include(
                    "arm64-v8a",
                    "armeabi-v7a",
                    "x86_64",
                    "x86"
                )
                isUniversalApk = true
            }
        }

        val formattedDate = SimpleDateFormat("dd, MMMM yyyy").format(Date())
        val variant = this
        resValue("string", "neko_build_date", "$formattedDate")
        resValue("string", "neko_app_version", "${variant.versionName} (${variant.versionCode})")
        resValue("string", "neko_min_sdk_version", "${variant.minSdk} (Android 8, Oreo)")
        resValue("string", "neko_target_sdk_version", "${variant.targetSdk} (Android 14, upside down cake)")
        resValue("string", "neko_packagename", "${variant.applicationId}")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../platform.keystore")
            storePassword = "password"
            keyAlias = "platform"
            keyPassword = "password"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
        create("release") {
            storeFile = file("../platform.keystore")
            storePassword = "password"
            keyAlias = "platform"
            keyPassword = "password"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        getByName("release") {
            resValue("string", "neko_build_type", "Release Build")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            resValue("string", "neko_build_type", "Debug Build")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
            kotlin.srcDirs("src/main/kotlin")
            java.srcDirs("src/main/java")
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    applicationVariants.all {
        val variant = this
        val versionCodes = mapOf(
            "armeabi-v7a" to 4,
            "arm64-v8a" to 4,
            "x86" to 4,
            "x86_64" to 4,
            "universal" to 4
        )

        outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val abi = output.getFilter("ABI") ?: "universal"
                output.outputFileName = "Neko-ray_${variant.versionName}_$abi.apk"
                if (abi in versionCodes) {
                    output.versionCodeOverride = 1000000 + variant.versionCode
                }
            }
    }

    lint {
        disable += "MissingTranslation"
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    dataBinding {
        addKtx = true
    }

    packaging {
        resources.excludes.add("META-INF/*")
        jniLibs.useLegacyPackaging = true
    }
}

if (file("user.gradle").exists()) {
    apply(from = "user.gradle")
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.flexbox)

    // Include Project
    implementation(project(":library"))

    // Androidx
    implementation(libs.constraintlayout)
    implementation(libs.appcompat)
    implementation(libs.legacy.support.v4)
    implementation(libs.material)
    implementation(libs.cardview)
    implementation(libs.preference.ktx)
    implementation(libs.recyclerview)
    implementation(libs.fragment.ktx)
    implementation(libs.multidex)
    implementation(libs.viewpager2)

    // Androidx ktx
    implementation(libs.activity.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Kotlin
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.mmkv.static)
    implementation(libs.gson)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.rxpermissions)
    implementation(libs.toastcompat)
    implementation(libs.editorkit)
    implementation(libs.language.base)
    implementation(libs.language.json)
    implementation(libs.quickie.bundled)
    implementation(libs.core)

    // Updating these 2 dependencies may cause some errors. Be careful.
    implementation(libs.work.runtime.ktx)
    implementation(libs.work.runtime)
    implementation(libs.work.multiprocess)

    // Misc
    implementation(libs.okhttp)
    implementation(libs.jsoup)
    implementation(libs.webkit)
    implementation(libs.barcode.scanning)
    implementation(libs.aboutlibraries.core)
    implementation(libs.fastadapter)
    implementation(libs.navigation.fragment)
    implementation(libs.picasso)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))
}
