@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("lengo.android.library")
    id("lengo.android.hilt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.lengo.network"
}

dependencies {
    implementation(libs.gson)
    implementation(libs.moshi)
    implementation(libs.moshi.adapter)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    ksp(libs.moshi.kotlin.codegen)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.collections.immutable)
    debugImplementation(libs.chuckerteam)
    releaseImplementation(libs.chuckerteam.no.op)
    implementation(project(":core:model"))
    implementation(project(":core:common"))
}