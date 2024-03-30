package com.neko.themeengine

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.neko.R

class ThemeEngine(context: Context) {
    private val prefs = context.getSharedPreferences("theme_engine_prefs", Context.MODE_PRIVATE)

    private var isFirstStart
        get() = prefs.getBoolean(FIRST_START, true)
        set(value) = prefs.edit { putBoolean(FIRST_START, value) }

    init {
        if (isFirstStart) {
            setDefaultValues(context)
            isFirstStart = false
        }
    }

    /**
     * Returns current [ThemeMode].
     * Setting this property applies the given theme mode to the activity.
     */
    var themeMode: Int
        get() = prefs.getInt(THEME_MODE, ThemeMode.AUTO)
        set(value) {
            require(value in 0..2) {
                "Incompatible value! Set this property with help of ThemeMode object."
            }
            prefs.edit { putInt(THEME_MODE, value) }
            AppCompatDelegate.setDefaultNightMode(
                when (value) {
                    ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
        }

    private val nightMode
        get() = when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

    /**
     * Returns true if Dynamic Colors are enabled, false otherwise.
     * Setting this property to true enables dynamic colors, false disables dynamic colors.
     * Keep in mind that dynamic colors will work only on Android 12 i.e. API 31 and higher devices.
     * And call Activity.recreate() after changing this property so that the changes get applied to the activity.
     */
    var isDynamicTheme
        get() = prefs.getBoolean(DYNAMIC_THEME, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        set(value) = prefs.edit { putBoolean(DYNAMIC_THEME, value) }

    /**
     * Get current app theme.
     * @return a dynamic theme if [isDynamicTheme] is enabled or a static theme otherwise.
     */
    fun getTheme(): Int {
        return if (hasS() && isDynamicTheme) R.style.Theme_ThemeEngine_Dynamic else staticTheme.themeId
    }

    /**
     * Get current static app theme, the theme which is used when dynamic color is disabled
     */
    var staticTheme: Theme
        get() = Theme.values()[prefs.getInt(APP_THEME, 1)]
        set(value) = prefs.edit { putInt(APP_THEME, value.ordinal) }

    /**
     * Resets static theme
     */
    fun resetTheme() {
        prefs.edit { remove(APP_THEME) }
    }

    private fun setDefaultValues(context: Context) {
        themeMode = context.getIntSafe(R.integer.theme_mode, ThemeMode.AUTO)
        prefs.edit { putInt(APP_THEME, context.getIntSafe(R.integer.static_theme, 1)) }
        isDynamicTheme = context.getBooleanSafe(R.bool.dynamic_theme, hasS()) && hasS()
    }

    companion object {
        private var INSTANCE: ThemeEngine? = null

        @JvmStatic
        fun getInstance(context: Context): ThemeEngine {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = ThemeEngine(context)
                INSTANCE = newInstance
                return newInstance
            }
        }

        /**
         * Applies themes and night mode to all activities by registering a [ActivityLifecycleCallbacks] to your application.
         * @param application Target Application
         */
        @JvmStatic
        fun applyToActivities(application: Application) {
            application.registerActivityLifecycleCallbacks(ThemeEngineActivityCallback())
        }

        /**
         * Applies themes and night mode to given activity
         * @param activity Target activity
         */
        @JvmStatic
        fun applyToActivity(activity: Activity) {
            with(getInstance(activity)) {
                activity.theme.applyStyle(getTheme(), true)
                AppCompatDelegate.setDefaultNightMode(nightMode)
            }
        }

        private const val THEME_MODE = "theme_mode"
        private const val DYNAMIC_THEME = "dynamic_theme"
        private const val APP_THEME = "app_theme"
        private const val FIRST_START = "first_start"
    }
}

private class ThemeEngineActivityCallback : ActivityLifecycleCallbacks {
    override fun onActivityPreCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        ThemeEngine.applyToActivity(activity)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}