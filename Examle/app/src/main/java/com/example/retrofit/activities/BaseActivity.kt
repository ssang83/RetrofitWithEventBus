package com.example.retrofit.activities

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.ScrollView
import android.widget.Toast
import com.example.retrofit.RetroFitApp
import com.example.retrofit.network.ApiClient
import com.example.retrofit.network.event.ApiErrorEvent
import com.example.retrofit.utils.Helper
import com.example.retrofit.utils.PermissionResult
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
open class BaseActivity : AppCompatActivity() {

    companion object {
        private const val SHOW_KEYBOARD_DELAY = 300L
    }

    private var KEY_PERMISSION = 0
    private var permissionResult: PermissionResult? = null
    private var permissionsAsk: Array<String>? = null

    private lateinit var mEventBus: EventBus
    protected lateinit var mApiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        mEventBus = EventBus.getDefault()
        mApiClient = RetroFitApp.app.apiClient

        if(!mEventBus.isRegistered(this)) {
            mEventBus.register(this)
        }
    }

    override fun onStart() {
        super.onStart()
        if(!mEventBus.isRegistered(this)) {
            mEventBus.register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        mEventBus.unregister(this)
    }

    override fun onResume() {
        super.onResume()
        if(!mEventBus.isRegistered(this)) {
            mEventBus.register(this)
        }
    }

    override fun onDestroy() {
        if(mEventBus.isRegistered(this)) {
            mEventBus.unregister(this)
        }
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode != KEY_PERMISSION) return

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
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, s)) {
                        permissionResult?.permissionForeverDenied()
                        return
                    }
                }
                permissionResult?.permissionDenied()
            }
        }
    }

    @Subscribe
    fun onEventMainThread(apiErrorEvent: ApiErrorEvent) {

    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * Shows toast message.
     *
     * @param msg
     */
    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Check if the Application required Permission is granted.
     *
     * @param context
     * @param permission
     * @return
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(
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
            if (ActivityCompat.checkSelfPermission(
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
     * Open KeyBoard
     *
     * @param v
     */
    protected fun showKeyBoardOnView(v: View) {
        v.requestFocus()
        Helper.showKeyBoard(this, v)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        v.requestFocus()
    }

    protected fun showKeyBoardOnViewDelay(v:View) {
        Handler().postDelayed( { showKeyBoardOnView(v) }, SHOW_KEYBOARD_DELAY)
    }

    /**
     * Hide KeyBoard
     *
     * @param v
     */
    protected fun hideKeyboard(v:View) {
        Helper.hideKeyboard(this, v)
        v.clearFocus()
    }

    protected fun focusOnView(scrollView: ScrollView, v: View) {
        Handler().post { scrollView.smoothScrollTo(0, v.bottom) }
    }

    /**
     * Ask Permission to be granted.
     *
     * @param permissionAsk
     */
    private fun internalRequestPermission(permissionAsk: Array<String>) {
        var arrayPermissionNotGranted: Array<String>
        val permissionsNotGranted:MutableList<String> = mutableListOf()

        for (aPermissionAsk in permissionAsk) {
            if (!isPermissionGranted(this, aPermissionAsk)) {
                permissionsNotGranted.add(aPermissionAsk)
            }
        }

        if (permissionsNotGranted.isEmpty()) {

            if (permissionResult != null)
                permissionResult?.permissionGranted()

        } else {
            arrayPermissionNotGranted = permissionsNotGranted.toTypedArray()
            ActivityCompat.requestPermissions(
                this,
                arrayPermissionNotGranted, KEY_PERMISSION
            )
        }
    }
}