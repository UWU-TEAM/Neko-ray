package com.neko.ip;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neko.ip.RequestNetwork;
import com.neko.v2ray.R;
import com.neko.v2ray.ui.BaseActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class IpLocation extends BaseActivity {
    private HashMap<String, Object> Map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> Maps = new ArrayList<>();
    private RequestNetwork.RequestListener _req_request_listener;
    private TextView as;
    private TextView city;
    private Button copy;
    private TextView country;
    private TextView countryCode;
    private TextView ip;
    private TextView isp;
    private TextView lat;
    private TextView lon;
    private TextView org;
    private TextView region;
    private TextView regionName;
    private RequestNetwork req;
    private TextView timezone;
    private TextView zip;

    private void initialize(Bundle bundle) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.copy = (Button) findViewById(getUwU("copy", "id"));
        this.city = (TextView) findViewById(getUwU("city", "id"));
        this.region = (TextView) findViewById(getUwU("region", "id"));
        this.country = (TextView) findViewById(getUwU("country", "id"));
        this.ip = (TextView) findViewById(getUwU("ip", "id"));
        this.countryCode = (TextView) findViewById(getUwU("countryCode", "id"));
        this.regionName = (TextView) findViewById(getUwU("regionName", "id"));
        this.zip = (TextView) findViewById(getUwU("zip", "id"));
        this.lat = (TextView) findViewById(getUwU("lat", "id"));
        this.lon = (TextView) findViewById(getUwU("lon", "id"));
        this.timezone = (TextView) findViewById(getUwU("timezone", "id"));
        this.isp = (TextView) findViewById(getUwU("isp", "id"));
        this.org = (TextView) findViewById(getUwU("org", "id"));
        this.as = (TextView) findViewById(getUwU("as", "id"));
        this.req = new RequestNetwork(this);
        this.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IpLocation ipLocation = IpLocation.this;
                IpLocation.this.getApplicationContext();
                ((ClipboardManager) ipLocation.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("clipboard", IpLocation.this.ip.getText().toString()));
            }
        });
        this._req_request_listener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String str, String str2, HashMap<String, Object> hashMap) {
                try {
                    IpLocation.this.Maps = (ArrayList) new Gson().fromJson("[".concat(str2.concat("]")), new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                    IpLocation.this.city.setText(((HashMap) IpLocation.this.Maps.get(0)).get("city").toString());
                    IpLocation.this.country.setText(((HashMap) IpLocation.this.Maps.get(0)).get("country").toString());
                    IpLocation.this.region.setText(((HashMap) IpLocation.this.Maps.get(0)).get("region").toString());
                    IpLocation.this.ip.setText(((HashMap) IpLocation.this.Maps.get(0)).get("query").toString());
                    IpLocation.this.countryCode.setText(((HashMap) IpLocation.this.Maps.get(0)).get("countryCode").toString());
                    IpLocation.this.regionName.setText(((HashMap) IpLocation.this.Maps.get(0)).get("regionName").toString());
                    IpLocation.this.zip.setText(((HashMap) IpLocation.this.Maps.get(0)).get("zip").toString());
                    IpLocation.this.lat.setText(((HashMap) IpLocation.this.Maps.get(0)).get("lat").toString());
                    IpLocation.this.lon.setText(((HashMap) IpLocation.this.Maps.get(0)).get("lon").toString());
                    IpLocation.this.timezone.setText(((HashMap) IpLocation.this.Maps.get(0)).get("timezone").toString());
                    IpLocation.this.isp.setText(((HashMap) IpLocation.this.Maps.get(0)).get("isp").toString());
                    IpLocation.this.org.setText(((HashMap) IpLocation.this.Maps.get(0)).get("org").toString());
                    IpLocation.this.as.setText(((HashMap) IpLocation.this.Maps.get(0)).get("as").toString());
                } catch (Exception e) {
                    Toast.makeText(IpLocation.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(String str, String str2) {
            }
        };
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getUwU("uwu_ip_location", "layout"));
        initialize(bundle);
        initializeLogic();
    }

    public int getUwU(String str, String str2) {
        return getBaseContext().getResources().getIdentifier(str, str2, getBaseContext().getPackageName());
    }

    private void initializeLogic() {
        this.req.startRequestNetwork(RequestNetworkController.GET, "http://ip-api.com/json/", "", this._req_request_listener);
    }
}