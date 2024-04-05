package com.neko.v2ray.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

public class ResourceSettingsFragment extends AbstractSettingsFragment {

    public static final String EXTRA_PREFERENCE_RESOURCE = "preferencesResource";

    protected int preferencesResource;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
        Bundle b = getArguments();
        if (b != null) {
            preferencesResource = b.getInt(EXTRA_PREFERENCE_RESOURCE, preferencesResource);
        }
        if (preferencesResource != 0) {
            addPreferencesFromResource(preferencesResource);
        }
    }
}
