package com.neko.ui;

import android.app.WallpaperManager;
import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;


public class WallpaperView extends AppCompatImageView {

    Context contextM;

    public WallpaperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        contextM = context;
    }

    public WallpaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        contextM = context;
    }


    public WallpaperView(Context context) {
        super(context);
        contextM = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(contextM);
        setImageDrawable(wallpaperManager.getDrawable());
    }
}
