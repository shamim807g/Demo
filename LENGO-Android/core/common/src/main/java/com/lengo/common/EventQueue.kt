package com.lengo.common
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope

@Stable
internal class Event<T>(val value: T)

class MutableEventQueue<T>
internal constructor(): EventQueue<T>() {
    private val events = mutableListOf<Event<T>>()
    private val nextEventAsState = mutableStateOf<Event<T>?>(null)

    fun push(e: T) {
        events.add(Event(e))
        onEventsChanged()
    }

    override fun next(): State<Event<T>?> {
        return nextEventAsState
    }

    override fun onHandled(event: Event<T>) {
        events.remove(event)
        onEventsChanged()
    }

    private fun onEventsChanged() {
        nextEventAsState.value = events.firstOrNull()
    }
}

fun <T> mutableEventQueue() = MutableEventQueue<T>()

abstract class EventQueue<T> {
    internal abstract fun next(): State<Event<T>?>
    internal abstract fun onHandled(event: Event<T>)
}

@Composable
fun <T> EventHandler(eventQueue: EventQueue<T>, handler: suspend CoroutineScope.(T) -> Unit) {
    val e = eventQueue.next().value
    LaunchedEffect(e) {
        if (e != null) {
            handler(e.value)
            eventQueue.onHandled(e)
        }
    }
}