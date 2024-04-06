package com.neko.marquee.text;

import android.content.Context;
import android.util.*;
import android.util.AttributeSet;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.*;
import androidx.appcompat.widget.AppCompatTextView;
import java.util.Calendar;
import com.neko.R;

public class Grettings extends AppCompatTextView {

    @Override // android.view.View
    public boolean isFocused() {
        return true;
    }

    public Grettings(Context context) {
        super(context);
        greeting();
    }

    public Grettings(Context context, AttributeSet attrs) {
        super(context, attrs);
        greeting();
    }

    public Grettings(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        greeting();
    }
    
    private void greeting() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String language = getResources().getConfiguration().locale.getLanguage();

        StringBuilder sb = new StringBuilder();
        if (language.equals("in")) {
            if (timeOfDay >= 4 && timeOfDay < 9) {
                sb.append("🌤 Selamat Pagi...");
            } else if (timeOfDay >= 9 && timeOfDay < 16) {
                sb.append("⛅ Selamat Siang...");
            } else if (timeOfDay >= 16 && timeOfDay < 21) {
                sb.append("🌥️ Selamat Sore...");
            } else if (timeOfDay >= 21 && timeOfDay < 24) {
                sb.append("🌙 Selamat Malam...");
            } else {
                sb.append("💤 Waktunya Tidur...");
            }
        } else {
            if (timeOfDay >= 4 && timeOfDay < 9) {
                sb.append("🌤 Good Morning...");
            } else if (timeOfDay >= 9 && timeOfDay < 16) {
                sb.append("⛅ Good Afternoon...");
            } else if (timeOfDay >= 16 && timeOfDay < 21) {
                sb.append("🌥️ Good Evening...");
            } else if (timeOfDay >= 21 && timeOfDay < 24) {
                sb.append("🌙 Good Night...");
            } else {
                sb.append("💤 It's time to go to sleep...");
            }
        }
        setText(sb);
    }
}
