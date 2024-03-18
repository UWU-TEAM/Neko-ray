package com.neko.marquee.deviceinfo;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class Hardware extends AppCompatTextView {
    private String memekVersion;

    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }

    private void JupokInfoSlur() {
        StringBuilder sb = new StringBuilder();
        sb.append(Build.HARDWARE);
        setText(sb);
    }

    public Hardware(Context context) {
        super(context);
        JupokInfoSlur();
    }

    public Hardware(Context context, AttributeSet attrs) {
        super(context, attrs);
        JupokInfoSlur();
    }

    public Hardware(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        JupokInfoSlur();
    }
}
