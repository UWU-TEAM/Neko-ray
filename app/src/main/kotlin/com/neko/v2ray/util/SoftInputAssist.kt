package com.neko.v2ray.util

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout

class SoftInputAssist(activity: Activity) {
    private var rootView: View? = null
    private var contentContainer: ViewGroup? = null
    private var viewTreeObserver: ViewTreeObserver? = null
    private val listener = ViewTreeObserver.OnGlobalLayoutListener { possiblyResizeChildOfContent() }
    private val contentAreaOfWindowBounds = Rect()
    private var rootViewLayout: FrameLayout.LayoutParams? = null
    private var usableHeightPrevious = 0

    init {
        contentContainer = activity.findViewById(android.R.id.content) as ViewGroup
        rootView = contentContainer?.getChildAt(0)
        rootViewLayout = rootView?.layoutParams as FrameLayout.LayoutParams
    }

    fun onPause() {
        if (viewTreeObserver?.isAlive == true) {
            viewTreeObserver?.removeOnGlobalLayoutListener(listener)
        }
    }

    fun onResume() {
        if (viewTreeObserver == null || viewTreeObserver?.isAlive == false) {
            viewTreeObserver = rootView?.viewTreeObserver
        }

        viewTreeObserver?.addOnGlobalLayoutListener(listener)
    }

    fun onDestroy() {
        rootView = null
        contentContainer = null
        viewTreeObserver = null
    }

    private fun possiblyResizeChildOfContent() {
        contentContainer?.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds)
        val usableHeightNow = contentAreaOfWindowBounds.bottom

        if (usableHeightNow != usableHeightPrevious) {
            rootViewLayout?.height = usableHeightNow
            rootView?.layout(contentAreaOfWindowBounds.left, contentAreaOfWindowBounds.top, contentAreaOfWindowBounds.right, contentAreaOfWindowBounds.bottom)
            rootView?.requestLayout()

            usableHeightPrevious = usableHeightNow
        }
    }
}
