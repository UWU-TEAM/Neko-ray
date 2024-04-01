import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

configurations {
    create("defaultRuntimeOnly")
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    // Include Project
    implementation(project(":library"))

    // Androidx
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta02")

    // Androidx ktx
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    implementation("com.tencent:mmkv-static:1.3.4")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.reactivex:rxjava:1.3.8")
    implementation("io.reactivex:rxandroid:1.2.1")
    implementation("com.tbruyelle.rxpermissions:rxpermissions:0.9.4@aar")
    implementation("me.drakeet.support:toastcompat:1.1.0")
    implementation("com.blacksquircle.ui:editorkit:2.9.0")
    implementation("com.blacksquircle.ui:language-base:2.9.0")
    implementation("com.blacksquircle.ui:language-json:2.9.0")
    implementation("io.github.g00fy2.quickie:quickie-bundled:1.9.0")
    implementation("com.google.zxing:core:3.5.3")

    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.work:work-multiprocess:2.8.1")

    // Misc
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.kyleduo.switchbutton:library:2.1.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("androidx.webkit:webkit:1.10.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar","*.jar"))))
}

android {
    namespace = "com.v2ray.ang"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.v2ray.ang"
        minSdk = 27
        targetSdk = 34
        versionCode = 210
        versionName = "1.0.1"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

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
            kotlin.srcDir("src/main/kotlin")
            java.srcDir("src/main/java")
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
        }
    }

    applicationVariants.all {
        val variant = this
        val versionCodes =
            mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86" to 3, "x86_64" to 4)

        variant.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val abi = if (output.getFilter("ABI") != null)
                    output.getFilter("ABI")
                else
                    "all"

                output.outputFileName = "Neko_v2rayNG_${variant.versionName}_${abi}.apk"
                if(versionCodes.containsKey(abi))
                {
                    output.versionCodeOverride = (1000000 * versionCodes[abi]!!).plus(variant.versionCode)
                }
                else
                {
                    return@forEach
                }
            }
    }

    lintOptions {
        disable("MissingTranslation")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packagingOptions {
        exclude("META-INF/ASL2.0")
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

if (file("user.gradle").exists()) {
    apply(from = "user.gradle")
}
