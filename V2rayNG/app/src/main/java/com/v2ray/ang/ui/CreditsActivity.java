package com.v2ray.ang.ui;

import androidx.fragment.app.Fragment;
import com.v2ray.ang.R;
import com.v2ray.ang.ui.UwuCollapsingToolbarActivity;
import com.v2ray.ang.ui.ResourceSettingsFragment;

public class CreditsActivity extends UwuCollapsingToolbarActivity {

    @Override
    protected Fragment getFragment() {
        return new AsFragment();
    }

    public static class AsFragment extends ResourceSettingsFragment {
        public AsFragment() {
            preferencesResource =  R.xml.uwu_preferences_credits;
        }
    }
}
