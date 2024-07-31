package com.neko.ip;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neko.v2ray.R;
import com.neko.v2ray.ui.BaseActivity;
import com.neko.v2ray.util.SoftInputAssist;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class IpLocation extends BaseActivity {
    private HashMap<String, Object> Map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> Maps = new ArrayList<>();
    private RequestNetwork.RequestListener _req_request_listener;
    private TextView city;
    private TextView country;
    private EditText edittext1;
    private LinearLayout linear1;
    private LinearLayout linear10;
    private LinearLayout linear11;
    private LinearLayout linear12;
    private Button linear2;
    private LinearLayout linear4;
    private LinearLayout linear5;
    private LinearLayout linear6;
    private LinearLayout linear9;
    private TextView region;
    private RequestNetwork req;
    private TextView textview2;
    private TextView textview4;
    private TextView textview6;
    private TextView textview7;
    private TextView textview8;
    private TextView textview9;
    private SoftInputAssist softInputAssist;

    private void initialize(Bundle bundle) {
        linear1 = findViewById(getUwU("linear1", "id"));
        linear11 = findViewById(getUwU("linear11", "id"));
        linear10 = findViewById(getUwU("linear10", "id"));
        linear9 = findViewById(getUwU("linear9", "id"));
        linear2 = findViewById(getUwU("linear2", "id"));
        linear12 = findViewById(getUwU("linear12", "id"));
        edittext1 = findViewById(getUwU("edittext1", "id"));
        textview2 = findViewById(getUwU("textview2", "id"));
        linear4 = findViewById(getUwU("linear4", "id"));
        linear6 = findViewById(getUwU("linear6", "id"));
        linear5 = findViewById(getUwU("linear5", "id"));
        textview4 = findViewById(getUwU("textview4", "id"));
        city = findViewById(getUwU("city", "id"));
        textview6 = findViewById(getUwU("textview6", "id"));
        region = findViewById(getUwU("region", "id"));
        textview7 = findViewById(getUwU("textview7", "id"));
        country = findViewById(getUwU("country", "id"));
        textview8 = findViewById(getUwU("textview8", "id"));
        textview9 = findViewById(getUwU("textview9", "id"));
        req = new RequestNetwork(this);
        linear11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IpLocation ipLocation = IpLocation.this;
                ((ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textview9.getText().toString()));
            }
        });
        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IpLocation.this._Aniqlash(IpLocation.this.edittext1.getText().toString());
            }
        });
        _req_request_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String str, String str2, HashMap<String, Object> hashMap) {
                try {
                    Maps = new Gson().fromJson("[" + str2 + "]", new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                    city.setText(Maps.get(0).get("city").toString());
                    country.setText(Maps.get(0).get("country").toString());
                    region.setText(Maps.get(0).get("region").toString());
                    textview9.setText(Maps.get(0).get("ip").toString());
                } catch (Exception e) {
                    SketchwareUtil.showMessage(IpLocation.this.getApplicationContext(), "Error!");
                }
            }

            @Override
            public void onErrorResponse(String str, String str2) {
            }
        };
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getUwU("uwu_ip_location", "layout"));
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize(bundle);
        initializeLogic();
        softInputAssist = new SoftInputAssist(this);
    }

    public int getUwU(String str, String str2) {
        return getBaseContext().getResources().getIdentifier(str, str2, getBaseContext().getPackageName());
    }

    private void initializeLogic() {
        req.startRequestNetwork("GET", "https://ipinfo.io/geo", "", _req_request_listener);
    }

    public void _Aniqlash(String str) {
        req.setHeaders(Map);
        req.startRequestNetwork("GET", "https://ipinfo.io/" + str + "/geo", "", _req_request_listener);
    }

    @Override
    protected void onResume() {
        softInputAssist.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        softInputAssist.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        softInputAssist.onDestroy();
        super.onDestroy();
    }
}
