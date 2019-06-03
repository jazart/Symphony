package com.jazart.symphony.common

import java.util.concurrent.atomic.AtomicBoolean

data class Event<T>(val data: T) {
    private val isConsumed = AtomicBoolean()

    fun peek(): T = data

    fun consume(): T? {
        return if (isConsumed.compareAndSet(false, true)) data else null
    }
}