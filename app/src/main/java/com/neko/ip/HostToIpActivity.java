package com.neko.ip;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import com.neko.v2ray.ui.*;
import com.neko.v2ray.util.SoftInputAssist;
import com.neko.v2ray.R;

import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class HostToIpActivity extends BaseActivity {
	
	private HashMap<String, Object> anichan2 = new HashMap<>();
	private String quote = "";
	private HashMap<String, Object> hostIP = new HashMap<>();
	
	private LinearLayout linear1;
	private Button linear7;
	private LinearLayout linear9;
	private LinearLayout linear10;
	private EditText edittext1;
	private TextView textview2;
	private TextView textview1;
	private TextView textview9;
	private TextView textview10;
	private TextView textview11;
	private TextView textview12;
	private TextView textview13;
	
	private RequestNetwork anichan;
	private RequestNetwork.RequestListener _anichan_request_listener;
	private SoftInputAssist softInputAssist;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.host_to_ip);
		initialize(_savedInstanceState);
		softInputAssist = new SoftInputAssist(this);
	}
	
	private void initialize(Bundle _savedInstanceState) {
	    Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		linear1 = findViewById(R.id.linear1);
		linear7 = findViewById(R.id.linear7);
		linear9 = findViewById(R.id.linear9);
		linear10 = findViewById(R.id.linear10);
		edittext1 = findViewById(R.id.edittext1);
		textview2 = findViewById(R.id.textview2);
		textview1 = findViewById(R.id.textview1);
		textview9 = findViewById(R.id.textview9);
		textview10 = findViewById(R.id.textview10);
		textview11 = findViewById(R.id.textview11);
		textview12 = findViewById(R.id.textview12);
		textview13 = findViewById(R.id.textview13);
		anichan = new RequestNetwork(this);
		
		linear7.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (edittext1.getText().toString().equals("")) {
					SketchwareUtil.showMessage(getApplicationContext(), "Empty");
				}
				else {
					anichan.startRequestNetwork(RequestNetworkController.GET, "http://ip-api.com/json/".concat(edittext1.getText().toString()), "A", _anichan_request_listener);
					SketchwareUtil.showMessage(getApplicationContext(), "Pls wait..");
				}
			}
		});
		
		_anichan_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				hostIP = new Gson().fromJson(_response, new TypeToken<HashMap<String, Object>>(){}.getType());
				if (hostIP.get("status").toString().equals("success")) {
					textview1.setText("IP:      ".concat(hostIP.get("query").toString()));
					textview9.setText("Country:      ".concat(hostIP.get("country").toString()));
					textview10.setText("ISP Provider:     ".concat(hostIP.get("org").toString()));
					textview11.setText("ISP Provider 2:     ".concat(hostIP.get("isp").toString()));
					textview12.setText("ISP Provider 3:     ".concat(hostIP.get("as").toString()));
					textview13.setText("City:     ".concat(hostIP.get("city").toString()));
				}
				else {
					SketchwareUtil.showMessage(getApplicationContext(), "Failed");
				}
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				SketchwareUtil.showMessage(getApplicationContext(), "Error");
			}
		};
	}
	
	public void _elevation(final View _tampilkan, final double _num) {
		_tampilkan.setElevation((int)_num);
	}
	
	
	public void _advancedCorners(final View _view, final String _color, final double _n1, final double _n2, final double _n3, final double _n4) {
		android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
		gd.setColor(Color.parseColor(_color));
		
		gd.setCornerRadii(new float[]{(int)_n1,(int)_n1,(int)_n2,(int)_n2,(int)_n4,(int)_n4,(int)_n3,(int)_n3});
		
		_view.setBackground(gd);
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
    public ArrayList<Double> getCheckedItemPositionsToArray(ListView listView) {
        ArrayList<Double> arrayList = new ArrayList<>();
        SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
        for (int i = 0; i < checkedItemPositions.size(); i++) {
            if (checkedItemPositions.valueAt(i)) {
                arrayList.add(Double.valueOf(checkedItemPositions.keyAt(i)));
            }
        }
        return arrayList;
    }
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
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
