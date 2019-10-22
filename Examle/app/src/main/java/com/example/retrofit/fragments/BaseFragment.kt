package com.example.retrofit.fragments

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.example.retrofit.R
import com.example.retrofit.RetroFitApp
import com.example.retrofit.network.ApiClient
import com.example.retrofit.utils.PermissionResult
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
open class BaseFragment : Fragment() {

    private var KEY_PERMISSION = 0
    private var permissionResult: PermissionResult? = null
    private var permissionsAsk: Array<String>? = null

    private lateinit var mEventBus: EventBus
    private lateinit var mContext: Context
    protected lateinit var mApiClient: ApiClient

    private var mDialog: Dialog? = null
    private var mHandler: Handler? = null
    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mEventBus = EventBus.getDefault()
        mApiClient = RetroFitApp.app.apiClient

        if(!mEventBus.isRegistered(this)) {
            mEventBus.register(this)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this)
        }
    }

    override fun onStop() {
        mEventBus.unregister(this)
        dismissProgress()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this)
        }
    }

    override fun onDestroy() {
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this)
        }

        dismissProgress()
        super.onDestroy()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != KEY_PERMISSION) return

        val permissionDenied = LinkedList<String>()
        var granted = true

        for (i in grantResults.indices) {

            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                granted = false
                permissionDenied.add(permissions[i])
            }
        }

        if (permissionResult != null) {
            if (granted) {
                permissionResult?.permissionGranted()
            } else {
                for (s in permissionDenied) {
                    if (!shouldShowRequestPermissionRationale(s)) {
                        permissionResult?.permissionForeverDenied()
                        return
                    }
                }
                permissionResult?.permissionDenied()
            }
        }
    }

    /**
     * Shows toast message.
     *
     * @param msg
     */
    fun showToast(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(layout: View, msg: String) {
        Snackbar.make(layout, msg, Snackbar.LENGTH_LONG).show()
    }

    /**
     * Check if the Application required Permission is granted.
     *
     * @param context
     * @param permission
     * @return
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if the Application required Permissions are granted.
     *
     * @param context
     * @param permissions
     * @return
     */
    fun isPermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true

        var granted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context, permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                granted = false
        }

        return granted
    }

    fun askCompactPermission(permission: String, permissionResult: PermissionResult) {
        KEY_PERMISSION = 200
        permissionsAsk = arrayOf(permission)
        this.permissionResult = permissionResult
        internalRequestPermission(permissionsAsk!!)
    }

    fun askCompactPermissions(permissions: Array<String>, permissionResult: PermissionResult) {
        KEY_PERMISSION = 200
        permissionsAsk = permissions
        this.permissionResult = permissionResult
        internalRequestPermission(permissionsAsk!!)
    }

    /**
     * Initialize Loading Dialog
     */
    protected fun initDialog(context: Context) {
        this.mContext = context
        mDialog = Dialog(mContext) // this or YourActivity
        mDialog?.setCanceledOnTouchOutside(false)
        mDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog?.getWindow()!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        LayoutInflater.from(mContext).apply {
            mView = inflate(R.layout.loader_layout, null, false)
        }

        val lottieAnimationView = mView?.findViewById<LottieAnimationView>(R.id.animation_view)
        lottieAnimationView?.speed = 2f

        mDialog?.setContentView(mView!!)

        mHandler = Handler()
    }

    protected fun dismissProgress() {
        if (mHandler != null && mDialog != null) {
            mHandler?.post { mDialog?.dismiss() }
        }
    }

    protected fun showProgress() {
        if (mHandler != null && mDialog != null) {

            mHandler?.post {
                if (!mDialog?.isShowing()!!) {
                    mDialog?.show()
                }
                //        hideKeyboard(edt);
            }
        }
    }

    /**
     * Ask Permission to be granted.
     *
     * @param permissionAsk
     */
    private fun internalRequestPermission(permissionAsk: Array<String>) {
        var arrayPermissionNotGranted: Array<String>
        val permissionsNotGranted = ArrayList<String>()

        for (aPermissionAsk in permissionAsk) {
            if (!isPermissionGranted(context!!, aPermissionAsk)) {
                permissionsNotGranted.add(aPermissionAsk)
            }
        }

        if (permissionsNotGranted.isEmpty()) {

            if (permissionResult != null)
                permissionResult?.permissionGranted()

        } else {
            arrayPermissionNotGranted = permissionsNotGranted.toTypedArray()
            requestPermissions(arrayPermissionNotGranted, KEY_PERMISSION)
        }
    }
}