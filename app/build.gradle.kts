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

    defaultConfig {
        applicationId = "com.neko.v2ray"
        minSdk = 27
        targetSdk = 34
        versionCode = 220
        versionName = "1.1.0"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
        splits.abi {
            reset()
            include(
                "arm64-v8a",
                "armeabi-v7a",
                "x86_64",
                "x86"
            )
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

    splits {
        abi {
            isEnable = true
            isUniversalApk = false
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val abi = if (output.getFilter("ABI") != null)
                    output.getFilter("ABI")
                else
                    "all"

                output.outputFileName = "Neko_v2rayNG_${variant.versionName}_${abi}.apk"
                output.versionCodeOverride = (1000000).plus(variant.versionCode)
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
    testImplementation("junit:junit:4.13.2")

    // Include Project
    implementation(project(":library"))

    // Androidx
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.7.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Androidx ktx
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    implementation("com.tencent:mmkv-static:1.3.4")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.reactivex:rxjava:1.3.8")
    implementation("io.reactivex:rxandroid:1.2.1")
    implementation("com.tbruyelle.rxpermissions:rxpermissions:0.9.4@aar")
    implementation("me.drakeet.support:toastcompat:1.1.0")
    implementation("com.blacksquircle.ui:editorkit:2.9.0")
    implementation("com.blacksquircle.ui:language-base:2.9.0")
    implementation("com.blacksquircle.ui:language-json:2.9.0")
    implementation("io.github.g00fy2.quickie:quickie-bundled:1.10.0")
    implementation("com.google.zxing:core:3.5.3")

    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.work:work-multiprocess:2.8.1")

    // Misc
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("androidx.webkit:webkit:1.11.0")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.mikepenz:aboutlibraries-core:11.2.0")
    implementation("com.mikepenz:fastadapter:5.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar","*.jar"))))
}
