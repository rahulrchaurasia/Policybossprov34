plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("com.google.devtools.ksp")
    id ("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.policyboss.policybosspro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.policyboss.policybosspro"
        minSdk = 24
        targetSdk = 34
        versionCode = 47
        versionName = "1.4.0.6"

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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }

    //for CFirebase Push Notification
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Add this to use the AndroidX SplashScreen API
    implementation ("androidx.core:core-splashscreen:1.0.1")

    // ViewModel ktx [For Factory method its handle its self]
    implementation ("androidx.activity:activity-ktx:1.9.1")
    implementation ("androidx.fragment:fragment-ktx:1.8.2")


    // Room
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    implementation("androidx.paging:paging-runtime:3.3.2")
    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

//05 temp
//    implementation("com.google.android.play:review-ktx:2.0.1")
//    implementation("com.google.android.play:core-ktx:1.8.1")

    ksp ("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Coroutine Lifecycle Scopes
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Navigation Components
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")


    // Hilt Components
    implementation("com.google.dagger:hilt-android:2.52")
    ksp ("com.google.dagger:hilt-compiler:2.52")

    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Hilt with WorkManager
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // For Image Proceesing
    implementation ("io.coil-kt:coil:2.7.0")
//    implementation("io.coil-kt:coil-transformations:2.7.0")

    implementation ("com.google.android.gms:play-services-vision:20.1.3")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.google.android.gms:play-services-analytics:18.1.0")
    implementation ("com.google.android.gms:play-services-auth:21.2.0")   //req for SMS Retreiver


    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation ("com.google.firebase:firebase-dynamic-links:22.1.0")
    implementation ("com.google.firebase:firebase-messaging:24.0.0")

  //Firebase Auth {latest req for token}
    implementation("com.google.auth:google-auth-library-oauth2-http:1.3.0")

    implementation ("com.github.bumptech.glide:glide:4.15.1")

   implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")  // for swipe to refresh
//    implementation ("com.google.firebase:firebase-iid:21.1.0") {
//        transitive = true
//    }

    //razorpay
    implementation("com.razorpay:checkout:1.6.41")

    //webengage
    implementation ("com.webengage:android-sdk:4.16.1")

    implementation(files("libs/MiPush_SDK_Client_5_1_5-G_3rd.aar"))






    // Image Cropper
//    implementation("com.github.yalantis:ucrop:2.2.8-native")

    implementation("com.vanniktech:android-image-cropper:4.6.0")

//
    implementation ("io.github.chaosleung:pinview:1.4.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

