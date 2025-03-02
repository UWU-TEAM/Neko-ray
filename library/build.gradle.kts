plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.neko"
    compileSdk = 35

    defaultConfig {
        minSdk = 27
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        release {
            isMinifyEnabled = false

        }
        debug {
            isMinifyEnabled = false
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.preference.ktx)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.okhttp)
    implementation(libs.jsoup)
    implementation(libs.picasso)
}
