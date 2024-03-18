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
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.neko.R;

public class StorageProgressBar extends ProgressBar {

    private int storageLevel;

    public StorageProgressBar(Context context) {
        super(context);
    }

    public StorageProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StorageProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        setStorageLevel(getOccupiedStoragePercentage());
        animateStorageLevel(0, getOccupiedStoragePercentage());
        storageLevel = getOccupiedStoragePercentage();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentLevel = getOccupiedStoragePercentage();
                setStorageLevel(currentLevel);
                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    private int getOccupiedStoragePercentage() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long totalBytes = statFs.getTotalBytes();
        long freeBytes = statFs.getAvailableBytes();
        long usedBytes = totalBytes - freeBytes;
        return (int) ((usedBytes * 100) / totalBytes);
    }

    private void setStorageLevel(int level) {
        setProgress(level);
        setProgressBarColor(level);
    }

    private void animateStorageLevel(final int startValue, final int endValue) {
        final ValueAnimator animator = ValueAnimator.ofInt(startValue, endValue);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                setStorageLevel(animatedValue);
            }
        });
        animator.start();
    }

    private void setProgressBarColor(int storageLevel) {
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
            int alpha = 255;
            int green = interpolateColor(255, 0, storageLevel, 0, 100);
            int red = interpolateColor(0, 255, storageLevel, 0, 100);
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
