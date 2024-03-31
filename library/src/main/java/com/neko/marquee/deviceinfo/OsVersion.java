package com.neko.marquee.deviceinfo;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class OsVersion extends AppCompatTextView {
    private String memekVersion;

    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }

    private void JupokInfoSlur() {
        StringBuilder sb = new StringBuilder();
        sb.append(Build.VERSION.RELEASE);
        setText(sb);
    }

    public OsVersion(Context context) {
        super(context);
        JupokInfoSlur();
    }

    public OsVersion(Context context, AttributeSet attrs) {
        super(context, attrs);
        JupokInfoSlur();
    }

    public OsVersion(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        JupokInfoSlur();
    }
}
