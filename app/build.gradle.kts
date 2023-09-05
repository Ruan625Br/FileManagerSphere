plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

}

android {
    namespace = "com.etb.filemanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.etb.filemanager"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "1.1.1"

        resValue("string", "app_version", "$versionName ($versionCode)")
        buildConfigField("String", "FILE_PROVIDER_AUTHORITY", "\"$applicationId.file_provider\"")
        resValue("string", "app_provider_authority", "$applicationId.app_provider")
        resValue("string", "file_provider_authority", "$applicationId.file_provider")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


    }

    buildFeatures {
        aidl = true
        buildConfig = true
        compose = true
        viewBinding = true
        dataBinding = true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")

        }
    }

}
dependencies {

    val material3 = "1.2.0-alpha05"
    val navigationVersion = "2.6.0"
    val composeBom = platform("androidx.compose:compose-bom:2023.08.00")
    val coilBom = platform("io.coil-kt:coil-bom:2.4.0")
    val roomVersion = "2.5.2"


    // AndroidX Libraries
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.core:core-ktx:+")


    // UI Libraries
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation("me.zhanghai.android.fastscroll:library:1.2.0")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Third-party Libraries
    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.21.1"))
    implementation("io.github.Rosemoe.sora-editor:editor")
    implementation("io.github.Rosemoe.sora-editor:language-textmate")
    implementation("commons-net:commons-net:3.9.0")
    implementation("org.tukaani:xz:1.9")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")
    implementation("dev.rikka.rikkax.preference:simplemenu-preference:1.0.3")
    implementation("dev.rikka.shizuku:api:12.2.0")
    runtimeOnly("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    //https://github.com/Glavo/kala-compress
    implementation("org.glavo.kala:kala-compress:1.21.0.1-beta3")

    // Apache Commons IO
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.commons:commons-compress:1.20")

    // https://mvnrepository.com/artifact/net.sf.sevenzipjbinding/sevenzipjbinding
    implementation("net.sf.sevenzipjbinding:sevenzipjbinding:16.02-2.01")

    //Google
    implementation("com.google.code.gson:gson:2.10.1")


    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")

    //Compose
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.material3:material3:$material3")
    implementation("androidx.compose.material3:material3-window-size-class:$material3")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("com.google.accompanist:accompanist-themeadapter-material:0.28.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    //Google accompanist
    implementation("com.google.accompanist:accompanist-navigation-animation:0.33.1-alpha")

    //Glide Compose
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.5")


    //ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.1.0")
    implementation("androidx.media3:media3-ui:1.1.0")

    //zoomable
    implementation("net.engawapg.lib:zoomable:1.5.0-beta1")

    //Coil
    implementation("io.coil-kt:coil-bom")
    implementation(coilBom)
    implementation("io.coil-kt:coil-video")
    implementation("io.coil-kt:coil-gif")
    implementation("io.coil-kt:coil-svg")
    implementation("io.coil-kt:coil-compose")

    //Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.core:core-ktx:1.10.1")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")


    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")
    ksp("androidx.hilt:hilt-compiler:1.0.0")
    ksp("com.google.dagger:hilt-compiler:2.44")
}










