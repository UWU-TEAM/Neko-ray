package com.neko.marquee.text;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.AppCompatTextView;
import com.v2ray.ang.util.SpeedtestUtil;

public class LibVersions extends AppCompatTextView {
    private String memekVersion;

    private void JupokInfoSlur() {
        StringBuilder sb = new StringBuilder();
        sb.append(SpeedtestUtil.INSTANCE.getLibVersion()).toString();
        setText(sb);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    public LibVersions(Context context) {
        super(context);
        JupokInfoSlur();
    }

    public LibVersions(Context context, AttributeSet attrs) {
        super(context, attrs);
        JupokInfoSlur();
    }

    public LibVersions(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        JupokInfoSlur();
    }
}
