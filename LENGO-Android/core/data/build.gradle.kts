@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("lengo.android.library")
    id("lengo.android.hilt")
    id("lengo.android.library.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.lengo.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:preferences"))
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.adapter)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.ext.work)
    ksp(libs.moshi.kotlin.codegen)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.billing.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.google.play.core)
    implementation(libs.google.play.core.ktx)
    implementation(libs.material)
    implementation(libs.compose.charts)
    implementation(libs.androidx.navigation.compose)


    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}