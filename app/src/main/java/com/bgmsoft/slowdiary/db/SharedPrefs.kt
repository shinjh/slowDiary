package com.bgmsoft.slowdiary.db

import android.content.Context
import android.content.SharedPreferences
import com.bgmsoft.slowdiary.model.Token
import com.bgmsoft.slowdiary.utils.AES_Decode
import com.bgmsoft.slowdiary.utils.AES_Encode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefs(context: Context) {

    private val TOKEN_COMPARE_VALUE = "abcdefghijklmnopqrstuvwxyz123456"

    private val EMPTY_COLLECTIONS_JSON = "[]"
    private val EMPTY_CLASS_JSON = "{}"
    private val EMPTY_STRING = ""

    val PREFS_NAME = "prefs_name"
    val PREFS_FIRST = "prefs_first"
    val PREFS_PUSH_ID = "prefs_push_id"
    val PREFS_ACCOUNT_TOKEN = "prefs_account_token"

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    // Object 처리를 위한 인라인 함수
    private inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    // pushId
    var prefsPushId: String?
        get() = prefs.getString(PREFS_PUSH_ID, null)
        set(value) = prefs.edit().putString(PREFS_PUSH_ID, value).apply()

    // 최초 실행 여부
    var prefsFirst: Boolean
        get() = prefs.getBoolean(PREFS_FIRST, true)
        set(value) = prefs.edit().putBoolean(PREFS_FIRST, value).apply()

    // 사용자 인증 토큰
    var prefsAuthToken: Token?
        get() = prefs.getString(PREFS_ACCOUNT_TOKEN, null)?.run {
            Gson().fromJson<Token>(
                AES_Decode(
                    this,
                    TOKEN_COMPARE_VALUE
                )
            )
        }
        set(value) = prefs.edit().putString(
            PREFS_ACCOUNT_TOKEN,
            AES_Encode(Gson().toJson(value as Token), TOKEN_COMPARE_VALUE)
        ).apply()

    fun removeAuthToken() = prefs.edit().remove(PREFS_ACCOUNT_TOKEN).apply()
}