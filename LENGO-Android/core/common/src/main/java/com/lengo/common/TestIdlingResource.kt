package com.lengo.common


import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

object TestIdlingResource {
    var isInTest = AtomicBoolean(false)
    private val counter = AtomicInteger(0)

    @JvmField
    val idlingResource = object : IdlingResource {


        override fun getName(): String {
            TODO("Not yet implemented")
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            TODO("Not yet implemented")
        }

        override fun isIdleNow() = counter.get() == 0
    }

    fun increment() {
        counter.incrementAndGet()
    }

    fun isAppInTest(): Boolean {
        return isInTest.get()
    }

    fun decrement() {
        if (!idlingResource.isIdleNow) {
            counter.decrementAndGet()
        }

        if (counter.get() < 0 && BuildConfig.DEBUG) {
            throw IllegalStateException("TestIdlingResource counter is corrupted! value = ${counter.get()}")
        }
    }

    fun reset() {
        counter.set(0)
    }

    fun get(): Int {
        return counter.get()
    }
}