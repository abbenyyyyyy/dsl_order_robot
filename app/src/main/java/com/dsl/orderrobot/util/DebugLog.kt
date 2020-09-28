package com.dsl.orderrobot.util

import android.util.Log

/**
 * @author dsl-abben
 * on 2020/02/14.
 */
object DebugLog {
    private const val tag = "DslRobot"

    private const val showDebug = true

    fun d(message: String) {
        if (showDebug) {
            Log.d(tag, message)
        }
    }

    fun i(message: String) {
        if (showDebug) {
            Log.i(tag, message)
        }
    }

    fun w(message: String) {
        if (showDebug) {
            Log.w(tag, message)
        }
    }

    fun e(message: String) {
        if (showDebug) {
            Log.e(tag, message)
        }
    }
}