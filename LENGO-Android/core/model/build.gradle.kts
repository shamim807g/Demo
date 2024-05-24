@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("lengo.android.feature")
    id("lengo.android.library.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.lengo.model"
}

dependencies {
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.gson)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)
}