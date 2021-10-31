package com.doctor.yumyum.common.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.DimenRes

/** dp to pixel **/

infix fun Context.dpToPx(dp: Float): Float {
    val displayMetrics = this.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
}

infix fun Context.dpToIntPx(dp: Float): Int {
    val displayMetrics = this.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics).toInt()
}

infix fun Context.pxToDp(px: Int): Int {
    val displayMetrics = this.resources.displayMetrics
    val dp = px / (displayMetrics.density)
    return dp.toInt()
}

infix fun Context.getDimenFloat(@DimenRes dimenRes: Int): Float =
    this.resources.getDimension(dimenRes)

infix fun Context.getDimenInt(@DimenRes dimenRes: Int): Int =
    this.resources.getDimensionPixelSize(dimenRes)
