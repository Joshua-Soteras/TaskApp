plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.quests.shared.test"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation(project(mapOf("path" to ":app")))
    implementation("junit:junit:4.13.2")
    implementation("androidx.room:room-ktx:2.6.0")
    implementation("androidx.test:runner:1.5.2")
    implementation("androidx.test.ext:junit:1.1.5")
    implementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    implementation("com.google.dagger:hilt-android:2.48.1")
    implementation("com.google.dagger:hilt-android-testing:2.48.1")
    implementation("com.github.skydoves:sandwich:2.0.4")
    ksp("com.google.dagger:hilt-android-compiler:2.48.1")
}