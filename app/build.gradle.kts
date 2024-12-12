plugins {

    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)


}

android {
    namespace = "com.example.hellorescue"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hellorescue"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

    }


    packaging {
        // Exclude the META-INF/DEPENDENCIES files to avoid conflicts
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.foundation.android)
    implementation(libs.cronet.embedded)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.core.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)




    // Glide library
    implementation("com.github.bumptech.glide:glide:4.14.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.1")

    //number picker
    implementation ("com.hbb20:ccp:2.5.1")
    implementation ("io.michaelrocks:libphonenumber-android:8.13.35")



    //qr code scanner
    implementation ("com.github.yuriy-budiyev:code-scanner:2.3.0")

    implementation("com.google.mlkit:text-recognition:16.0.0") // Use this dependency for text recognition
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")



    //RapidAPI's geocoding service
    implementation("com.squareup.okhttp3:okhttp:4.9.3")


    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-messaging")
    implementation ("com.google.firebase:firebase-messaging:24.0.1")
    implementation("com.google.firebase:firebase-auth:23.0.0")

    implementation("com.google.firebase:firebase-analytics")

    //gif
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.29")

    //gif loop control
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.23")

    implementation ("com.android.volley:volley:1.2.1")


    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
    implementation ("np.com.susanthapa:curved_bottom_navigation:0.6.5")


    implementation ("androidx.viewpager2:viewpager2:1.0.0")


    //image view round corner
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("com.google.android.material:material:1.13.0-alpha08")

    implementation ("org.mindrot:jbcrypt:0.4")





}










