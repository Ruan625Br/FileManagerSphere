plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
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
        buildConfigField("String", "FILE_PROVIDIER_AUTHORITY", "$applicationId.file_provider")
        resValue("string", "app_provider_authority", "$applicationId.app_provider")
        resValue("string", "file_provider_authority", "$applicationId.file_provider")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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


    dependencies {
        // AndroidX Libraries
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
        implementation("androidx.activity:activity-compose:1.7.2")
        implementation(platform("androidx.compose:compose-bom:2023.08.00"))
        implementation("androidx.compose.ui:ui-graphics")
        implementation("androidx.compose.ui:ui-tooling-preview")
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
        implementation("com.github.bumptech.glide:glide:4.15.1")
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

        implementation("androidx.navigation:navigation-ui-ktx:\$navigation_version")
        implementation("androidx.navigation:navigation-fragment-ktx:\$navigation_version")

        //Compose
        val composeBom = platform("androidx.compose:compose-bom:2023.08.00")
        implementation(composeBom)
        androidTestImplementation(composeBom)

        implementation("androidx.compose.runtime:runtime")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.foundation:foundation")
        implementation("androidx.compose.foundation:foundation-layout")
        implementation("androidx.compose.material3:material3:\$material3")
        implementation("androidx.compose.material3:material3-window-size-class:\$material3")
        implementation("androidx.compose.runtime:runtime-livedata")
        implementation("androidx.compose.ui:ui-tooling")
        implementation("com.google.accompanist:accompanist-themeadapter-material:0.28.0")
        implementation("androidx.compose.material:material-icons-extended")

        //ExoPlayer
        implementation("androidx.media3:media3-exoplayer:1.1.0")
        implementation("androidx.media3:media3-ui:1.1.0")

        //zoomable
        implementation("net.engawapg.lib:zoomable:1.5.0-beta1")

        //Coil
        implementation("io.coil-kt:coil-bom")
        val coilBom = platform("io.coil-kt:coil-bom:2.4.0")
        implementation(coilBom)
        implementation("io.coil-kt:coil-video")
        implementation("io.coil-kt:coil-gif")
        implementation("io.coil-kt:coil-svg")
        implementation("io.coil-kt:coil-compose")

        //Room
        val roomVersion = "2.5.2"
        implementation("androidx.room:room-runtime:$roomVersion")
        implementation("androidx.core:core-ktx:1.10.1")
        ksp("androidx.room:room-compiler:$roomVersion")
        implementation("androidx.room:room-ktx:$roomVersion")


        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

        //Constraint layout compose
        implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    }


}





