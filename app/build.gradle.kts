plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.virtuallabs"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.virtuallabs"
        minSdk = 24
        targetSdk = 34

        versionCode = 2
        versionName = "0.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Для прототипа: подпишем release тем же debug-ключом, чтобы APK можно было установить.
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // Для Kotlin 1.9.24 обычно подходит 1.5.14 (если нужно — обновите)
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.annotation:annotation:1.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.activity:activity-compose:1.9.1")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.navigation:navigation-compose:2.7.7")

    // DataStore (хранение состояния premium)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ВАЖНО: XML-тема Theme.Material3.* (используется в res/values/themes.xml)
    // находится в библиотеке Material Components. Без этой зависимости сборка release падает
    // на этапе processReleaseResources с ошибкой AAPT: Theme.Material3... not found.
    implementation("com.google.android.material:material:1.12.0")

    // Google Play Billing (в прототипе используется как заглушка; реальную покупку подключаете позже)
    implementation("com.android.billingclient:billing-ktx:7.0.0")
}
