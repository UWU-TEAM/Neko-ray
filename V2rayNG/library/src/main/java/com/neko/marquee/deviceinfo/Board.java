package com.neko.marquee.deviceinfo;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class Board extends AppCompatTextView {
    private String memekVersion;

    private void JupokInfoSlur() {
        StringBuilder sb = new StringBuilder();
        sb.append(Build.BOARD);
        setText(sb);
    }

    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }

    public Board(Context context) {
        super(context);
        JupokInfoSlur();
    }

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        JupokInfoSlur();
    }

    public Board(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        JupokInfoSlur();
    }
}
