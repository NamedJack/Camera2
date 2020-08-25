package com.android.camera2

import android.util.Size
import java.lang.Long.signum

internal class CompareSizesByArea :Comparator<Size>{
    override fun compare(lhs: Size, rhs: Size): Int =
        signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)

}