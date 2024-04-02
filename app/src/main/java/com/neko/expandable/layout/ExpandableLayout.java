
package com.neko.expandable.layout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ExpandableLayout extends LinearLayout implements View.OnClickListener {
    private ImageView arrowIcon;
    private ExpandableView expandableContent;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Context context = getContext();
        this.expandableContent = findViewById(getResources(context, "id/expandable_view"));
        this.arrowIcon = findViewById(getResources(context, "id/arrow_button"));
        this.arrowIcon.setOnClickListener(this);
        initializeLogic();
    }

    public static int getResources(Context context, String str) {
        return context.getResources().getIdentifier(str, null, context.getPackageName());
    }

    @Override
    public void onClick(View view) {
        setOnclick(view);
    }

    private void setOnclick(View view) {
        if (this.expandableContent.isExpanded()) {
            this.expandableContent.collapse();
            this.expandableContent.setOrientation(ExpandableView.VERTICAL);
            this.arrowIcon.animate().setDuration(300L).rotation(0.0f);
            return;
        }
        this.expandableContent.expand();
        this.expandableContent.setOrientation(ExpandableView.VERTICAL);
        this.arrowIcon.animate().setDuration(300L).rotation(90.0f);
    }

    private void initializeLogic() {
        this.expandableContent.setExpansion(false);
        this.arrowIcon.setBackground(new RippleDrawable(new ColorStateList(new int[][]{new int[0]}, new int[]{-1118482}), null, null));
        this.arrowIcon.setClickable(true);
    }

    public ExpandableLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}

