plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.rpomp208"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rpomp208"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation ("com.google.android.gms:play-services-maps:17.0.0")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("org.osmdroid:osmdroid-android:6.1.16")
    implementation ("org.osmdroid:osmdroid-mapsforge:6.1.16")
    implementation ("com.google.android.gms:play-services-location:21.0.1") // Для работы с FusedLocationProviderClient

    implementation ("androidx.appcompat:appcompat:1.6.1") // Для поддержки AppCompatActivity
    implementation ("androidx.core:core-ktx:1.10.1") // Для работы с Kotlin (если используется)
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4") // Для ConstraintLayout
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}