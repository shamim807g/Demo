@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("lengo.android.library")
    id("lengo.android.hilt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.lengo.preferences"
}

dependencies {
    implementation(libs.gson)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.androidx.dataStore.preferences)
    implementation(project(":core:model"))
    implementation(project(":core:common"))
}
