package com.bgmsoft.slowdiary.utils

import android.util.Log
import com.bgmsoft.slowdiary.BuildConfig

/**
 * Created by JaehoShin on 2019. 5. 23
 */

object L {

    internal val TAG = "BgmSoft"

    /**
     * Log Level Error For Debuging
     */
    fun t() {
        if (BuildConfig.DEBUG) Log.e(TAG, buildLogMsg(""))
    }

    /**
     * Log Level Error
     */
    fun e(message: String) {
        if (BuildConfig.DEBUG) Log.e(TAG, buildLogMsg(message))
    }

    /**
     * Log Level Warning
     */
    fun w(message: String) {
        if (BuildConfig.DEBUG) Log.w(TAG, buildLogMsg(message))
    }

    /**
     * Log Level Information
     */
    fun i(message: String) {
        if (BuildConfig.DEBUG) Log.i(TAG, buildLogMsg(message))
    }

    /**
     * Log Level Debug
     */
    fun d(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, buildLogMsg(message))
    }

    /**
     * Log Level Verbose
     */
    fun v(message: String) {
        if (BuildConfig.DEBUG) Log.v(TAG, buildLogMsg(message))
    }

    fun buildLogMsg(message: String): String {
        val ste = Thread.currentThread().stackTrace[4]
        val sb = StringBuilder()
        sb.append("[")
        sb.append(ste.fileName.replace(".kt", ""))
        sb.append("::")
        sb.append(ste.methodName)
        sb.append("]")
        sb.append(message)
        return sb.toString()
    }
}
