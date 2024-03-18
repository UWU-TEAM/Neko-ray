package com.v2ray.ang.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.v2ray.ang.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public abstract class UwuCollapsingToolbarActivity extends BaseActivity {
    protected int preferencesResource = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uwu_collapsing_toolbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_wrapper, getFragment())
                .commit();
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack("root")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.content_wrapper, fragment)
                .commit();
    }

    protected Fragment getFragment() {
        if (preferencesResource == 0) {
            throw new IllegalStateException("Neither preferencesResource given, nor overriden getFragment()");
        }
        ResourceSettingsFragment fragment = new ResourceSettingsFragment();
        Bundle b = new Bundle();
        b.putInt(ResourceSettingsFragment.EXTRA_PREFERENCE_RESOURCE, preferencesResource);
        fragment.setArguments(b);
        return fragment;
    }
}