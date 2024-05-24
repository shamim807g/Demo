import com.lengo.uni.APPLICATION_ID
import com.lengo.uni.VERSION_CODE
import com.lengo.uni.VERSION_NAME

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("lengo.android.application")
    id("lengo.android.application.compose")
    id("lengo.android.application.flavors")
    id("lengo.android.hilt")
}

android {
    namespace = "com.lengo.uni"

    defaultConfig {
        applicationId = APPLICATION_ID
        versionCode = VERSION_CODE
        versionName = VERSION_NAME
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        resourceConfigurations += setOf(
            "en",
            "ar",
            "da",
            "de",
            "el",
            "es",
            "fi",
            "fr",
            "it",
            "ja",
            "nl",
            "no",
            "pl",
            "pt",
            "ru",
            "sv",
            "th",
            "tr",
            "uk",
            "zh"
        )
    }
    signingConfigs {
        create("release") {
            keyAlias = "danish_key"
            keyPassword = "yorbax121"
            storeFile = file("prod_keystore.jks")
            storePassword = "yorbax121"
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    bundle {
        language {
            // Specifies that the app bundle should not support
            // configuration APKs for language resources. These
            // resources are instead packaged with each base and
            // dynamic feature APK.
            enableSplit = false
        }
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:common"))
    implementation(project(":core:preferences"))
    implementation(project(":core:database"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:modalsheet"))
    implementation(libs.gson)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.play.core)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.google.play.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.hilt.ext.work)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.compose.charts)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlin.stdlib)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.molecule.runtime)


    testImplementation(libs.junit4)
    androidTestImplementation(libs.work.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.androidx.test.rules)

}