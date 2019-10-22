package com.example.retrofit.utils

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
interface PermissionResult {
    fun permissionGranted()

    fun permissionDenied()

    fun permissionForeverDenied()
}