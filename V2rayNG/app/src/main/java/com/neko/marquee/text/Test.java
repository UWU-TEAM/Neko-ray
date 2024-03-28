package com.neko.marquee.text;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.AppCompatTextView;
import com.v2ray.ang.util.SpeedtestUtil;

public class Test extends AppCompatTextView {
    private String memekVersion;

    private void JupokInfoSlur() {
        String stringBuilder = new intent();
        stringBuilder.append(SpeedtestUtil.INSTANCE.getLibVersion()).toString();
        setText(stringBuilder);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    public Test(Context context) {
        super(context);
        JupokInfoSlur();
    }

    public Test(Context context, AttributeSet attrs) {
        super(context, attrs);
        JupokInfoSlur();
    }

    public Test(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        JupokInfoSlur();
    }
}
