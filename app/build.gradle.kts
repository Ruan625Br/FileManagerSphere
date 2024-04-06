@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("androidx.room")
    alias(libs.plugins.baselineprofile)

}

android {
    namespace = "com.etb.filemanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.etb.filemanager"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.1.2"

        resValue("string", "app_version", "$versionName ($versionCode)")
        buildConfigField("String", "FILE_PROVIDER_AUTHORITY", "\"$applicationId.jn.fileprovider\"")
        resValue("string", "app_provider_authority", "$applicationId.jn.app_provider")
        resValue("string", "file_provider_authority", "$applicationId.jn.file_provider")

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
        //noinspection DataBindingWithoutKapt
        dataBinding = true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

}
dependencies {

    val material3 = "1.2.0"
    val navigationVersion = "2.7.7"
    val roomVersion = "2.6.1"


    // AndroidX Libraries
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.core:core-ktx:1.12.0")


    // UI Libraries
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation("me.zhanghai.android.fastscroll:library:1.3.0")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Third-party Libraries
    implementation(platform("io.github.Rosemoe.sora-editor:bom:0.22.2"))
    implementation("io.github.Rosemoe.sora-editor:editor")
    implementation("io.github.Rosemoe.sora-editor:language-textmate")
    implementation("commons-net:commons-net:3.10.0")
    implementation("org.tukaani:xz:1.9")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")
    implementation("dev.rikka.rikkax.preference:simplemenu-preference:1.0.3")
    implementation("dev.rikka.shizuku:api:13.1.5")
    runtimeOnly("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    //https://github.com/Glavo/kala-compress
    implementation("org.glavo.kala:kala-compress:1.21.0.1-beta3")

    // Apache Commons IO
    implementation("commons-io:commons-io:2.15.1")
    implementation("org.apache.commons:commons-compress:1.25.0")

    implementation("net.sf.sevenzipjbinding:sevenzipjbinding:16.02-2.01")

    //Google
    implementation("com.google.code.gson:gson:2.10.1")


    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")

    //Compose
    implementation("androidx.compose:compose-bom:2024.02.00")
    androidTestImplementation("androidx.compose:compose-bom:2024.02.00")

    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.material3:material3:$material3")
    implementation("androidx.compose.material3:material3-window-size-class:$material3")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("com.google.accompanist:accompanist-themeadapter-material:0.34.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    //Google accompanist
    implementation("com.google.accompanist:accompanist-navigation-animation:0.34.0")

    //Glide Compose
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")


    //ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")

    //zoomable
    implementation("net.engawapg.lib:zoomable:1.6.0")

    //Coil
    implementation(platform("io.coil-kt:coil-bom:2.5.0"))
    implementation("io.coil-kt:coil-video")
    implementation("io.coil-kt:coil-gif")
    implementation("io.coil-kt:coil-svg")
    implementation("io.coil-kt:coil-compose")

    //Room
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")


    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")
    ksp("com.google.dagger:hilt-compiler:2.50")

    //Baseline Profile
    baselineProfile(project(":app:benchmark"))

    //GenerativeAI
    implementation("com.google.ai.client.generativeai:generativeai:0.1.2")

    //Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    //Ruan625Br
    implementation("com.github.Ruan625Br:FilePickerSphere:1.0.0")
    implementation("com.github.Ruan625Br:AIResponseMatcher:4904b50758")

    //Work
    implementation("androidx.work:work-runtime-ktx:2.9.0")
}










