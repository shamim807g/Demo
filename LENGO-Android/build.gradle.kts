buildscript {
    repositories {
        google()
        mavenCentral()
        //maven { url = uri(" https://dl.bintray.com/getsentry/sentry-android") }
    }
}


@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    //alias(libs.plugins.sentry) apply false
}
true // Needed to make the Suppress annotation work for the plugins block