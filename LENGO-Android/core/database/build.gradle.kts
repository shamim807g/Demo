@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("lengo.android.library")
    id("lengo.android.room")
    id("lengo.android.hilt")
    //alias(libs.plugins.cash.sqldelight)
}

android {
    namespace = "com.lengo.database"
}

dependencies {
    implementation(libs.gson)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    implementation(project(":core:model"))
    implementation(project(":core:common"))
}

//sqldelight {
//    databases {
//        create("LENGODatabase") {
//            packageName.set("com.lengo.uni")
//            schemaOutputDirectory.set(file("src/main/sqldelight/schema"))
//            verifyMigrations.set(true)
//        }
//    }
//}
