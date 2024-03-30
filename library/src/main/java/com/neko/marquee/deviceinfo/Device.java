package com.neko.marquee.deviceinfo;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class Device extends AppCompatTextView {
    private String memekVersion;

    private void JupokInfoSlur() {
        StringBuilder sb = new StringBuilder();
        sb.append(Build.DEVICE);
        setText(sb);
    }

    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }

    public Device(Context context) {
        super(context);
        JupokInfoSlur();
    }

    public Device(Context context, AttributeSet attrs) {
        super(context, attrs);
        JupokInfoSlur();
    }

    public Device(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        JupokInfoSlur();
    }
}