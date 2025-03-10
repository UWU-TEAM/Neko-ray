import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.aboutlibraries)
}

configurations {
    create("defaultRuntimeOnly")
}

android {
    namespace = "com.neko.v2ray"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.neko.v2ray"
        minSdk = 27
        targetSdk = 35
        versionCode = 636
        versionName = "1.9.39"
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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val formattedDate = SimpleDateFormat("dd, MMMM yyyy").format(Date())
        val variant = this
        resValue("string", "neko_build_date", "$formattedDate")
        resValue("string", "neko_app_version", "${variant.versionName} (${variant.versionCode})")
        resValue("string", "neko_min_sdk_version", "${variant.minSdk} (Android 8, Oreo)")
        resValue("string", "neko_target_sdk_version", "${variant.targetSdk} (Android 15, Vanilla Ice Cream)")
        resValue("string", "neko_packagename", "${variant.applicationId}")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
            matchingFallbacks += listOf("debug")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
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
    // Core Libraries
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))

    // Include Project
    implementation(project(":library"))

    // AndroidX Core Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.preference.ktx)
    implementation(libs.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)

    // UI Libraries
    implementation(libs.material)
    implementation(libs.toastcompat)
    implementation(libs.editorkit)
    implementation(libs.flexbox)

    // Data and Storage Libraries
    implementation(libs.mmkv.static)
    implementation(libs.gson)

    // Reactive and Utility Libraries
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Language and Processing Libraries
    implementation(libs.language.base)
    implementation(libs.language.json)

    // Intent and Utility Libraries
    implementation(libs.quickie.foss)
    implementation(libs.core)

    // AndroidX Lifecycle and Architecture Components
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Background Task Libraries
    implementation(libs.work.runtime.ktx)
    implementation(libs.work.multiprocess)

    // Multidex Support
    implementation(libs.multidex)

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.org.mockito.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Misc
    implementation(libs.okhttp)
    implementation(libs.jsoup)
    implementation(libs.webkit)
    implementation(libs.barcode.scanning)
    implementation(libs.aboutlibraries.core)
    implementation(libs.fastadapter)
    implementation(libs.navigation.fragment)
    implementation(libs.picasso)
}
