package com.neko.v2ray.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.neko.v2ray.R

class CreditsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.uwu_preferences_credits, rootKey)
    }
}

