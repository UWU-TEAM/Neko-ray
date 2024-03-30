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

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.PorterDuff;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.neko.R;

public class BatteryProgressBar extends ProgressBar {

    private boolean isCharging = false;
    private int batteryLevel;

    public BatteryProgressBar(Context context) {
        super(context);
    }

    public BatteryProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BatteryProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        batteryLevel = getBatteryLevel();
        setBatteryLevel(getBatteryLevel());
        animateBatteryLevel(0, getBatteryLevel());

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int currentLevel = getBatteryLevel();
                if (isCharging() || batteryLevel != currentLevel) {
                    setBatteryLevel(currentLevel);
                    batteryLevel = currentLevel;
                    animateBatteryLevel(0, currentLevel);
                }
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    private int getBatteryLevel() {
        BatteryManager batteryManager = (BatteryManager) getContext().getSystemService(Context.BATTERY_SERVICE);
        if (batteryManager != null) {
            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            return 0;
        }
    }

    private boolean isCharging() {
        BatteryManager batteryManager = (BatteryManager) getContext().getSystemService(Context.BATTERY_SERVICE);
        if (batteryManager != null) {
            int status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
        }
        return isCharging;
    }

    private void setBatteryLevel(int level) {
        setProgress(level);
        setProgressBarColor(level);
    }

    private void animateBatteryLevel(final int startValue, final int endValue) {
        final ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                setBatteryLevel(animatedValue);
            }
        });
        animator.start();
    }

    private void setProgressBarColor(int batteryLevel) {
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
            int alpha = 255;
            int red = interpolateColor(255, 0, batteryLevel, 0, 100);
            int green = interpolateColor(0, 255, batteryLevel, 0, 100);
            int blue = 0;
            int color = (alpha << 24) | (red << 16) | (green << 8) | blue;
            int bgColor = ContextCompat.getColor(getContext(), R.color.uwu_bg_color);
            Drawable bg = layerDrawable.getDrawable(0);
            Drawable progress = layerDrawable.getDrawable(1);
            if (bg != null) {
                bg.setColorFilter(bgColor, PorterDuff.Mode.SRC_IN);
            }
            if (progress != null) {
                progress.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private int interpolateColor(int start, int end, float fraction, float startFraction, float endFraction) {
        return (int) (start + (end - start) * ((fraction - startFraction) / (endFraction - startFraction)));
    }
}
