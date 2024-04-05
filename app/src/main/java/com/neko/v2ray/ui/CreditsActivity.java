package com.neko.v2ray.ui;

import androidx.fragment.app.Fragment;
import com.neko.v2ray.R;
import com.neko.v2ray.ui.UwuCollapsingToolbarActivity;
import com.neko.v2ray.ui.ResourceSettingsFragment;

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
