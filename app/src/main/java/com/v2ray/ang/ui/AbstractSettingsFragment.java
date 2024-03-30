package com.v2ray.ang.ui;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public abstract class AbstractSettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = AbstractSettingsFragment.class.getSimpleName();

    private static final String DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG";

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof DialogPreference) {
            DialogFragment f = DialogPreference.DialogPreferenceCompatDialogFragment.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
