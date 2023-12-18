package com.polware.instagramclone.data.util

/** This class create mutable status in IgViewModel */
open class IgEvent<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentOrNull(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

}