package com.neko.expandable.layout

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

class ExpandableLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs), View.OnClickListener {
    private lateinit var arrowIcon: ImageView
    private lateinit var expandableContent: ExpandableView

    override fun onFinishInflate() {
        super.onFinishInflate()
        val context = context
        expandableContent = findViewById(getResources(context, "id/expandable_view"))
        arrowIcon = findViewById(getResources(context, "id/arrow_button"))
        arrowIcon.setOnClickListener(this)
        initializeLogic()
    }

    override fun onClick(view: View) {
        setOnclick(view)
    }

    private fun setOnclick(view: View) {
        if (expandableContent.isExpanded) {
            expandableContent.collapse()
            expandableContent.orientation = ExpandableView.VERTICAL
            arrowIcon.animate().setDuration(300L).rotation(0.0f)
            return
        }
        expandableContent.expand()
        expandableContent.orientation = ExpandableView.VERTICAL
        arrowIcon.animate().setDuration(300L).rotation(90.0f)
    }

    private fun initializeLogic() {
        expandableContent.setExpansion(false)
        arrowIcon.background = RippleDrawable(ColorStateList(arrayOf(intArrayOf()), intArrayOf(-0x8a8a8b)), null, null)
        arrowIcon.isClickable = true
    }

    companion object {
        fun getResources(context: Context, str: String): Int {
            return context.resources.getIdentifier(str, null, context.packageName)
        }
    }
}
