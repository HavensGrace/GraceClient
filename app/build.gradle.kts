// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
// If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.lombok)
    kotlin("plugin.serialization") version libs.versions.kotlin
}

android {
    namespace = "io.havens.grace"
    compileSdk = 35



    defaultConfig {
        applicationId = "io.havens.grace"
        minSdk = 28
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 3
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += setOf("arm64-v8a", "armeabi-v7a")
        }
    }
    signingConfigs {
        create("shared") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true

            storeFile = rootDir.resolve("buildKey.jks")
            keyAlias = "UntrustedKey"
            storePassword = "123456"
            keyPassword = "123456"
        }
    }
    packaging {
        jniLibs.useLegacyPackaging = true
        resources.excludes.addAll(
            setOf(
                "DebugProbesKt.bin"
            )
        )
        resources.pickFirsts.addAll(
            setOf(
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties",
                "META-INF/DEPENDENCIES"
            )
        )
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("shared")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
            signingConfig = signingConfigs.getByName("shared")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeCompiler {
        includeTraceMarkers = false
        includeSourceInformation = false
        generateFunctionKeyMetaClasses = false
        featureFlags = setOf(
            ComposeFeatureFlag.OptimizeNonSkippingGroups,
            ComposeFeatureFlag.PausableComposition
        )
    }
}

configurations.all {

}

fun DependencyHandler.implementationRelay() {
    debugImplementation(platform(libs.log4j.bom))
    debugImplementation(libs.log4j.api)
    debugImplementation(libs.log4j.core)
    implementation(libs.bundles.netty)
    implementation(libs.expiringmap)
    implementation(libs.network.common)
    implementation(platform(libs.fastutil.bom))
    implementation(libs.fastutil.long.common)
    implementation(libs.fastutil.long.obj.maps)
    implementation(libs.fastutil.int.obj.maps)
    implementation(libs.fastutil.obj.int.maps)
    implementation(libs.jose4j)
    implementation(libs.math)
    implementation(libs.nbt)
    implementation(libs.snappy)
    implementation(libs.guava)
    implementation(libs.gson)
    implementation(libs.http.client)
    implementation(libs.bcprov)
    implementation(libs.okhttp)
}

dependencies {

    implementation(libs.compose.colorful.sliders)

    // File dependencies
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))


    // AndroidX dependencies
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.legacy:legacy-support-v13:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

    // Material & Google Services
    implementation("com.google.android.material:material:1.3.0")
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("com.google.android.gms:play-services-gcm:17.0.0")
    implementation("com.google.android.gms:play-services-iid:17.0.0")

    //STATS Dependencies
    implementation("com.github.jaikeerthick:Composable-Graphs:v1.2.3")
    implementation("com.amplitude:analytics-android:1.+")

    // Kotlin dependencies
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("org.jetbrains:annotations:21.0.1")
    implementation("app.rive:rive-android:8.1.0")
    implementationRelay()
    implementation(libs.kotlinx.serialization.json.jvm)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}