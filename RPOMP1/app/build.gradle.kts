plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.rpomp81"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rpomp81"
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
    implementation("com.google.code.gson:gson:2.10.1") // Для работы с JSON
    implementation("com.android.volley:volley:1.2.1") // Для сетевых запросов
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2") // Добавлена зависимость для RecyclerView
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.opencsv:opencsv:5.5.2")
    implementation ("androidx.core:core-splashscreen:1.0.0")

  //  implementation("androidx.cardview:cardview:1.0.0") // CardView (если потребуется)
  //  implementation("androidx.viewbinding:viewbinding:7.2.2") // ViewBinding
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}