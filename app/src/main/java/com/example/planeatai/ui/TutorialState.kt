package com.example.planeatai.ui

import android.content.Context
import android.content.SharedPreferences

object TutorialState {
    private const val PREF_NAME = "tutorial_prefs"
    private const val KEY_SHOWN = "tutorial_shown"

    fun isTutorialShown(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOWN, false)
    }

    fun setTutorialShown(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOWN, true).apply()
    }

    fun resetTutorial(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOWN, false).apply()
    }
}
