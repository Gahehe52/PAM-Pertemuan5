import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        // Ini adalah blok yang diperbaiki agar tidak error
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android)
        }
        commonMain.dependencies {
            // Library Bawaan Standar
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // --- TAMBAHAN WAJIB TUGAS 5 & PROFIL ---
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.0")
            implementation("com.russhwolf:multiplatform-settings:1.1.1")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.datetime)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        val iosMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.sqldelight.native)
            }
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.sqldelight.jvm)
        }
    }
}

android {
    namespace = "com.example.notesghama"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.notesghama"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose {
    resources {
        packageOfResClass = "com.example.notesghama"
    }
    desktop {
        application {
            mainClass = "com.example.notesghama.MainKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "com.example.notesghama"
                packageVersion = "1.0.0"
            }
        }
    }
}

sqldelight {
    databases {
        create("NotesDatabase") {
            packageName.set("com.example.notesghama.db")
        }
    }
}