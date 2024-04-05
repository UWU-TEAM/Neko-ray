package com.neko.marquee.text;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import com.neko.v2ray.BuildConfig;

public class AutoMarqueeVersiTextView extends AppCompatTextView {
    private String memekVersion;

    private void JupokInfoSlur() {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.VERSION_NAME);
        setText(sb);
    }

    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }

    public AutoMarqueeVersiTextView(Context context) {
        super(context);
        JupokInfoSlur();
    }

    public AutoMarqueeVersiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        JupokInfoSlur();
    }

    public AutoMarqueeVersiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        JupokInfoSlur();
    }
}
