package com.etb.filemanager.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.etb.filemanager.R
import com.etb.filemanager.databinding.ActivitySettingsBinding
import com.etb.filemanager.fragment.SettingsFragment
import com.etb.filemanager.settings.preference.PreferenceFragment
import java.util.Objects


class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private lateinit var binding: ActivitySettingsBinding

    private val SCHEME = "file-manager-sphere"
    private val HOST = "settings"

    private var mLevel = 0
    private var mKeys: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.mToolbar)
        binding.mToolbar.setNavigationOnClickListener { onBackPressed() }

        val uri = getIntent().data
        if (uri != null && SCHEME.equals(uri.scheme) && HOST.equals(uri.host) && uri.path != null){
            mKeys = Objects.requireNonNull(uri.pathSegments)
        }

        supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
            if (fragment !is SettingsFragment) {
                ++mLevel
            }
        }
        supportFragmentManager.addOnBackStackChangedListener {
            mLevel = supportFragmentManager.backStackEntryCount
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_layout, SettingsFragment().getInstance(getKey(mLevel)))
            .commit()

    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent.data
        if (uri != null && SCHEME == uri.scheme && HOST == uri.host && uri.path != null) {
            mKeys = Objects.requireNonNull(uri.pathSegments)
            val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.main_layout)
            if (fragment is SettingsFragment) {
                getKey(0.also { mLevel = it })?.let { (fragment as SettingsFragment?)?.setPrefKey(it) }
            }
        }
    }

    fun getIntent(context: Context, vararg paths: String?): Intent {
        val intent = Intent(context, SettingsActivity::class.java)
        if (paths != null) {
            intent.data = SettingsActivity().getSettingUri(*paths as Array<out String>)
        }
        return intent
    }

    private fun getSettingUri(vararg pathSegments: String): Uri {
        val builder = Uri.Builder()
            .scheme(SCHEME)
            .authority(HOST)
        for (pathSegment in pathSegments) {
            builder.appendPath(pathSegment)
        }
        return builder.build()
    }

    private fun getKey(level: Int): String? {
        if (mKeys.size > level) {
            return mKeys[level]
        }
        return null
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        if (pref.fragment != null) {
            val fragmentManager = supportFragmentManager
            val args = pref.extras
            val fragment = fragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment!!)
            val subKey = getKey(mLevel + 1)
            if (subKey != null && fragment is PreferenceFragment && Objects.equals(
                    pref.key,
                    getKey(mLevel)
                )
            ) {
                args.putString("key", subKey)
            }
            fragment.arguments = args
            fragment.setTargetFragment(caller, 0)
            fragmentManager.beginTransaction()
                .replace(R.id.main_layout, fragment)
                .addToBackStack(null)
                .commit()
            return true
        }
        return false
    }

}