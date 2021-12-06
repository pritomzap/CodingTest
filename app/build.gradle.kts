import AppConfig.testInstrumentationRunner
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.application")
    kotlin("android")
    id ("dagger.hilt.android.plugin")
    kotlin("kapt")
    id ("kotlin-parcelize")
    id("kotlin-android")
}

android {
    compileSdkVersion (AppConfig.compileSkdVersion)
    buildToolsVersion (AppConfig.buildToolsVersion)

    defaultConfig {
        applicationId  = AppConfig.applicationId
        minSdkVersion (AppConfig.minSdkVersion)
        targetSdkVersion (AppConfig.targetSdkVersion)
        versionCode (AppConfig.versionCode)
        versionName (AppConfig.versionName)
        testInstrumentationRunner (AppConfig.testInstrumentationRunner)

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += AppConfig.getRoomAnnotationProcessorArgMap(projectDir)
            }
        }

        applicationVariants.all {
            val variant = this
            variant.outputs
                .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                .forEach { output ->
                    val outputFileName = "${AppConfig.appVarientName}_(${variant.baseName}_${variant.versionName} ${variant.versionCode}).apk"
                    output.outputFileName = outputFileName
                }
        }
    }

    buildFeatures {
        dataBinding = true
        // viewBinding = true
    }
    packagingOptions {
        exclude("META-INF/ASL2.0")
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }

    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError  = false
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled  = false
            isShrinkResources = false
            isDebuggable  = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release"){
            isMinifyEnabled  = true
            isShrinkResources  = true
            isDebuggable  = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions{
        jvmTarget = "1.8"
    }
    flavorDimensions ("environment")
    productFlavors{
        create("dev"){
            dimension = "environment"
        }
        create("staging"){
            dimension = "environment"
        }
        create("production"){
            dimension = "environment"
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar","*.aar"))))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation ("androidx.core:core-ktx:1.6.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation ("com.google.android.material:material:1.4.0")

    implementation ("com.google.dagger:hilt-android:${Versions.hiltVersion}")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    kapt ("com.google.dagger:hilt-compiler:${Versions.hiltVersion}")

    implementation("androidx.room:room-runtime:${Versions.roomVersion}")
    kapt("androidx.room:room-compiler:${Versions.roomVersion}")
    implementation("androidx.room:room-ktx:${Versions.roomVersion}")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutineVersion}")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutineVersion}")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutineVersion}")

    implementation ("androidx.lifecycle:lifecycle-extensions:${Versions.lifeCycleVersion}")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycleVersion}")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifeCycleVersion}")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifeCycleVersion}")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("androidx.activity:activity-ktx:${Versions.activityVersion}")


    // For instrumentation tests
    androidTestImplementation  ("com.google.dagger:hilt-android-testing:${Versions.hiltVersion}")
    kaptAndroidTest ("com.google.dagger:hilt-compiler:${Versions.hiltVersion}")

    // For local unit tests
    testImplementation ("com.google.dagger:hilt-android-testing:${Versions.hiltVersion}")
    kaptTest ("com.google.dagger:hilt-compiler:${Versions.hiltVersion}")

    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutineVersion}")
    androidTestImplementation ("com.google.truth:truth:1.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.arch.core:core-testing:2.1.0")
    testImplementation ("com.google.truth:truth:1.1")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
}
kapt {
    correctErrorTypes = true
}