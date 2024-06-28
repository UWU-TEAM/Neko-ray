plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.neko"
    compileSdk = 34

    defaultConfig {
        minSdk = 27
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        release {
            isMinifyEnabled = false

        }
        debug {
            isMinifyEnabled = false
        }
    }

    sourceSets {
        getByName("main") {
            kotlin.srcDirs("src/main/kotlin")
            java.srcDirs("src/main/java")
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    lint {
        disable += "MissingTranslation" + "GetLocales"
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")
}
