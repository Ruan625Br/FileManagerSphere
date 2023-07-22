package com.etb.filemanager.files.util

// SPDX-License-Identifier: GPL-3.0-or-later

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.app.UiModeManager
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.os.PowerManager
import android.view.ContextThemeWrapper
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.collection.ArrayMap
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.etb.filemanager.settings.preference.Preferences
import com.google.android.material.color.DynamicColors
import java.lang.ref.WeakReference
import java.util.Locale


object AppearanceUtils {
    val TAG = AppearanceUtils::class.java.simpleName
    private val sActivityReferences = ArrayMap<Int, WeakReference<Activity>>()
    fun applyOnlyLocale(context: Context) {
        // Update locale and layout direction for the application
        val options = AppearanceOptions()
        options.locale = LangUtils().getFromPreference(context)
       // options.layoutDirection = Prefs.Appearance.getLayoutDirection()
        updateConfiguration(context, options)
        if (context != context.applicationContext) {
            updateConfiguration(context.applicationContext, options)
        }
    }

    /**
     * Return a [ContextThemeWrapper] with the default locale, layout direction, theme and night mode.
     */
    fun getThemedContext(context: Context): Context {
        val options = AppearanceOptions()
        options.locale = LangUtils().getFromPreference(context)
        //TODO(function not yet integrated) options.layoutDirection = Prefs.Appearance.getLayoutDirection()
        options.theme = Preferences.Appearance.getAppTheme()
        val newCtx = ContextThemeWrapper(
            context,
            options.theme!!
        )
        newCtx.applyOverrideConfiguration(createOverrideConfiguration(context, options))
        return newCtx
    }

    /**
     * Return a [ContextWrapper] with system configuration. This is helpful when it is necessary to access system
     * configurations instead of the one used in the app.
     */
    fun getSystemContext(context: Context): Context {
        val res = Resources.getSystem()
        val configuration = res.configuration
        return ContextWrapper(context.createConfigurationContext(configuration))
    }

    /**
     * Initialize appearance in the app. Must be called from [Application.onCreate].
     */
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(ActivityAppearanceCallback())
        application.registerComponentCallbacks(ComponentAppearanceCallback(application))
        applyOnlyLocale(application)
    }

    /**
     * This is similar to what the delegate methods such as [AppCompatDelegate.setDefaultNightMode] does.
     * This is required because simply calling [ActivityCompat.recreate] cannot apply the changes to
     * all the active activities.
     */
    fun applyConfigurationChangesToActivities() {
        for (activityRef: WeakReference<Activity> in sActivityReferences.values) {
            val activity = activityRef.get()
            if (activity != null) {
                ActivityCompat.recreate(activity)
            }
        }
    }

    private fun updateConfiguration(context: Context, options: AppearanceOptions) {
        val res = context.resources
        // Set theme
        if (options.theme != null) {
            context.setTheme(options.theme!!)
        }
        // Update configuration
        val overrideConf = createOverrideConfiguration(context, options)
        res.updateConfiguration(overrideConf, res.displayMetrics)
    }

    private fun createOverrideConfiguration(
        context: Context,
        options: AppearanceOptions
    ): Configuration {
        return createOverrideConfiguration(context, options, null, false)
    }

    @SuppressLint("AppBundleLocaleChanges") // We don't use Play Store
    private fun createOverrideConfiguration(
        context: Context,
        options: AppearanceOptions,
        configOverlay: Configuration?,
        ignoreFollowSystem: Boolean
    ): Configuration {
        // Order matters!
        val res = context.resources
        val oldConf = res.configuration
        val overrideConf = Configuration(oldConf)

        // Set locale
        if (options.locale != null) {
            Locale.setDefault(options.locale)
            val currentLocale =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) oldConf.locales[0] else oldConf.locale
            if (currentLocale !== options.locale) {
                // Locale has changed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setLocaleApi24(overrideConf, options.locale!!)
                } else {
                    overrideConf.setLocale(options.locale)
                    overrideConf.setLayoutDirection(options.locale)
                }
            }
        }
        // Set layout direction
        if (options.layoutDirection != null) {
            val currentLayoutDirection = overrideConf.layoutDirection
            if (currentLayoutDirection != options.layoutDirection) {
                when (options.layoutDirection) {
                    View.LAYOUT_DIRECTION_RTL -> overrideConf.setLayoutDirection(
                        Locale.forLanguageTag(
                            "ar"
                        )
                    )

                    View.LAYOUT_DIRECTION_LTR -> overrideConf.setLayoutDirection(Locale.ENGLISH)
                }
            }
        }

        // Set night mode
/*
        if (options.nightMode != null) {
            // Follow AppCompatDelegateImpl
            val nightMode =
                if (options.nightMode != AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) options.nightMode!! else AppCompatDelegate.getDefaultNightMode()
            val modeToApply = mapNightModeOnce(context, nightMode)
            val newNightMode: Int
            when (modeToApply) {
                AppCompatDelegate.MODE_NIGHT_YES -> newNightMode = Configuration.UI_MODE_NIGHT_YES
                AppCompatDelegate.MODE_NIGHT_NO -> newNightMode = Configuration.UI_MODE_NIGHT_NO
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> if (ignoreFollowSystem) {
                    // We're generating an overlay to be used on top of the system configuration,
                    // so use whatever's already there.
                    newNightMode = Configuration.UI_MODE_NIGHT_UNDEFINED
                } else {
                    // If we're following the system, we just use the system default from the
                    // application context
                    val sysConf = Resources.getSystem().configuration
                    newNightMode = sysConf.uiMode and Configuration.UI_MODE_NIGHT_MASK
                }

                else -> if (ignoreFollowSystem) {
                    newNightMode = Configuration.UI_MODE_NIGHT_UNDEFINED
                } else {
                    val sysConf = Resources.getSystem().configuration
                    newNightMode = sysConf.uiMode and Configuration.UI_MODE_NIGHT_MASK
                }
            }
            overrideConf.uiMode =
                newNightMode or (overrideConf.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
        }
*/

        // Apply overlay
        if (configOverlay != null) {
            overrideConf.setTo(configOverlay)
        }
        return overrideConf
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLocaleApi24(config: Configuration, locale: Locale) {
        val defaultLocales = LocaleList.getDefault()
        val locales = LinkedHashSet<Locale>(defaultLocales.size() + 1)
        // Bring the target locale to the front of the list
        // There's a hidden API, but it's not currently used here.
        locales.add(locale)
        for (i in 0 until defaultLocales.size()) {
            locales.add(defaultLocales[i])
        }
        config.setLocales(LocaleList(*locales.toTypedArray()))
    }


    private class AppearanceOptions() {
        var locale: Locale? = null
        var layoutDirection: Int? = null
        var theme: Int? = null
        var nightMode: Int? = null
    }

    private class ActivityAppearanceCallback() : ActivityLifecycleCallbacks {
        override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {

                activity.setTheme(Preferences.Appearance.getAppTheme())

            // Theme must be set first because the method below will add dynamic attributes to the theme
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                onActivityPreCreated(activity, savedInstanceState)
            }
            val window = activity.window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                onActivityPostCreated(activity, savedInstanceState)
            }
        }

        override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
            applyOnlyLocale(activity)
            sActivityReferences[activity.hashCode()] = WeakReference(activity)
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityPreDestroyed(activity: Activity) {
            sActivityReferences.remove(activity.hashCode())
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                onActivityPreDestroyed(activity)
            }
        }
    }

    private class ComponentAppearanceCallback(private val mApplication: Application) :
        ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
            applyOnlyLocale(mApplication)
        }

        override fun onLowMemory() {}
        override fun onTrimMemory(level: Int) {}
    }
}