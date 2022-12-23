package com.codebox.podcaster.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by Codebox on 17/05/21
 */
class ViewUtil @Inject constructor(@ApplicationContext val context: Context) {

    fun dpToPx(dp: Float): Float {

        val r: Resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
    }


}