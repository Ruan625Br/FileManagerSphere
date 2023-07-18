package com.etb.filemanager.settings.preference

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.etb.filemanager.BuildConfig
import com.etb.filemanager.databinding.FragmentAboutBinding
import com.google.android.material.transition.MaterialFadeThrough

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.aboutToolbar.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
        binding.aboutVersion.text = BuildConfig.VERSION_NAME
        binding.aboutCode.setOnClickListener {openLinkInBrowser(LINK_SOURCE) }
        binding.aboutLicenses.setOnClickListener {openLinkInBrowser(LINK_LICENSES) }
        binding.aboutAuthor.setOnClickListener {openLinkInBrowser(LINK_AUTHOR) }
    }

    private fun openLinkInBrowser(uri: String) {
        val context = requireContext()
        val browserIntent =
            Intent(Intent.ACTION_VIEW, uri.toUri()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                context.startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {

            }
        } else {
            val packageName = context.packageManager.resolveActivity(
                    browserIntent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )?.run { activityInfo.packageName }
            if (packageName != null) {
                if (packageName == "android") {
                    openAppChooser(browserIntent)
                }
                try {
                    browserIntent.setPackage(packageName)
                    startActivity(browserIntent)
                } catch (e: ActivityNotFoundException) {
                    browserIntent.setPackage(null)
                    openAppChooser(browserIntent)
                }

            }
        }
    }

    companion object {
        const val LINK_SOURCE = "https://github.com/Ruan625Br/FileManagerSphere"
        const val LINK_LICENSES = "https://github.com/Ruan625Br/FileManagerSphere/wiki/Licences"
        const val LINK_AUTHOR = "https://github.com/Ruan625Br"
    }

    private fun openAppChooser(intent: Intent) {
        val chooserIntent = Intent(Intent.ACTION_CHOOSER).putExtra(Intent.EXTRA_INTENT, intent)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(chooserIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
