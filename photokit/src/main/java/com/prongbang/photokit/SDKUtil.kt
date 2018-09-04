package com.prongbang.photokit

import android.os.Build

object SDKUtil {

    fun isMarshmallowAndHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun isNougatAndHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

}