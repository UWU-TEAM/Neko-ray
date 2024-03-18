/*
 * Copyright (C) 2023 The risingOS Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.neko.ui;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

import com.neko.R;

public class ChargingPercentTextView extends AppCompatTextView {

    private String chargingText;
    private String dischargingText;
    private Context mContext;

    public ChargingPercentTextView(Context context) {
        super(context);
        mContext = context;
    }

    public ChargingPercentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ChargingPercentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init(mContext);
    }

    private void init(Context context) {
        final String percentText = context.getResources().getString(R.string.homepage_widget_battery_percent);
        setText(String.valueOf(getBatteryLevel(context) + " " + percentText));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currLvl = getBatteryLevel(context);
                setText(String.valueOf(currLvl) + " " + percentText);
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    private int getBatteryLevel(Context context) {
        final BatteryManager batteryManager = (BatteryManager) context.getSystemService(BatteryManager.class);
        if (batteryManager != null) {
            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            return 0;
        }
    }
}
