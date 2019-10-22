package com.example.retrofit.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.retrofit.R
import com.example.retrofit.adapters.ImageAdapter
import com.example.retrofit.network.event.ApiErrorEvent
import com.example.retrofit.network.event.ApiErrorWithMessageEvent
import com.example.retrofit.network.response.ImageListResponse
import kotlinx.android.synthetic.main.fragment_image_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
class ImagesFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        private var mColumnCount = 2
        private val ARG_COLUMN_COUNT = "column-count"

        fun newInstance(columnCount:Int) =
            ImagesFragment().apply {
                Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private val LOGTAG = "ImagesFragment"
    private val IMAGE_LIST_REQUEST_TAG = "$LOGTAG.imageListRequest"

    private lateinit var mAdapter:ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null) {
            mColumnCount = arguments!!.getInt(ARG_COLUMN_COUNT)
        }

        mAdapter = ImageAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(mColumnCount <= 1) {
            list.layoutManager = LinearLayoutManager(context!!)
        } else {
            list.layoutManager = GridLayoutManager(context!!, mColumnCount)
        }

        list.adapter = mAdapter
        swipeLayout.setOnRefreshListener(this)

        initDialog(context!!)
        loadImages()
    }

    override fun onRefresh() {
        loadImages()
    }

    // ============================================================================================
    // EventBus callbacks
    // ============================================================================================

    /**
     * Response of Image list.
     *
     * @param imageListResponse ImageListResponse
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(response:ImageListResponse) {
        when(response.requestTag) {
            IMAGE_LIST_REQUEST_TAG -> {
                dismissProgress()
                mAdapter.updateData(response.imageResults)
                swipeLayout.isRefreshing = false
            }
        }
    }

    /**
     * EventBus listener. An API call failed. No error message was returned.
     *
     * @param event ApiErrorEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event:ApiErrorEvent) {
        when(event.requestTag) {
            IMAGE_LIST_REQUEST_TAG -> {
                dismissProgress()
                showToast(getString(R.string.error_server_problem))
                swipeLayout.isRefreshing = false
            }
        }
    }

    /**
     * EventBus listener. An API call failed. An error message was returned.
     *
     * @param event ApiErrorWithMessageEvent Contains the error message.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event:ApiErrorWithMessageEvent) {
        when(event.requestTag) {
            IMAGE_LIST_REQUEST_TAG -> {
                dismissProgress()
                showToast(event.resultMsgUser)
                swipeLayout.isRefreshing = false
            }
        }
    }

    private fun loadImages() {
        showProgress()

        if(!mApiClient.isRequestRunning(IMAGE_LIST_REQUEST_TAG)) {
            mApiClient.getImageList(IMAGE_LIST_REQUEST_TAG)
        }
    }
}