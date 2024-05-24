package com.lengo.common


import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val lengoDispatcher: LengoDispatchers)

enum class LengoDispatchers {
    Default,
    IO,
    Main
}