package com.lengo.uni.ui

import androidx.lifecycle.ViewModel
import com.lengo.common.EventQueue
import com.lengo.common.mutableEventQueue

abstract class BaseViewModel<S, E> : ViewModel() {
    abstract val initialState: S

    private val _eventQueue = mutableEventQueue<E>()
    val eventQueue: EventQueue<E> = _eventQueue


}