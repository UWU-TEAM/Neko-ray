package com.neko.themeengine

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerSize
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

class CircleImageView @JvmOverloads constructor(
    context: Context, attrs:
    AttributeSet? = null, defStyleAttr: Int = -1
) : ShapeableImageView(context, attrs, defStyleAttr) {

    init {
        val shapeAppearanceModel = ShapeAppearanceModel()
            .toBuilder()
            .setAllCornerSizes(CornerSize {
                return@CornerSize it.height() / 2
            })
            .build()
        background = MaterialShapeDrawable(shapeAppearanceModel)
    }

    override fun setBackgroundColor(color: Int) {
        (background as MaterialShapeDrawable).fillColor = ColorStateList.valueOf(color)
    }

    fun setBackgroundColorRes(@ColorRes color: Int) {
        setBackgroundColor(ResourcesCompat.getColor(resources, color, context.theme))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    private lateinit var shapeDrawableAnimator: ValueAnimator

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                shapeDrawableAnimator = ValueAnimator.ofFloat(2f, 4f)
                shapeDrawableAnimator.addUpdateListener {
                    (background as MaterialShapeDrawable).setCornerSize(CornerSize { rect ->
                        return@CornerSize rect.height() / it.animatedValue as Float
                    })
                }
                shapeDrawableAnimator.start()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                shapeDrawableAnimator.reverse()
            }
        }
        return super.onTouchEvent(event)
    }
}
