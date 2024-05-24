/*
 * Copyright 2023 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.lengo.uni

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")


internal fun Project.version(key: String): String = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion(key)
    .get()
    .requiredVersion

fun Project.versionInt(key: String) = version(key).toInt()

val Project.ANDROID_COMPILE_SDK_VERSION get() = versionInt("compileSdk")
val Project.ANDROID_MIN_SDK_VERSION get() = versionInt("minSdk")
val Project.ANDROID_TARGET_SDK_VERSION get() = versionInt("targetSdk")
val Project.VERSION_CODE get() = versionInt("versionCode")
val Project.VERSION_NAME get() = version("versionName")
val Project.APPLICATION_ID get() = version("applicationId")
