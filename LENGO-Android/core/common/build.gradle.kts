@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("lengo.android.library")
    id("lengo.android.library.compose")
    id("lengo.android.hilt")
}

android {
    namespace = "com.lengo.common"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:modalsheet"))
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    api(libs.billing.ktx)
    implementation(libs.molecule.runtime)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.work.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.androidx.test.rules)
}