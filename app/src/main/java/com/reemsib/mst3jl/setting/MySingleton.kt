package com.reemsib.mst3jl.setting

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class  MySingleton private constructor(context: Context) {
    private var mRequestQueue: RequestQueue

    // applicationContext is key, it keeps you from leaking the
    // Activity or BroadcastReceiver if someone passes one in.
    val requestQueue: RequestQueue
    get() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx?.applicationContext)
        }
        return mRequestQueue
    }

    init {
        mCtx = context
        mRequestQueue = requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    companion object {
        private var mInstance: MySingleton? = null
        private var mCtx: Context? = null

        @Synchronized
        fun getInstance(context: Context): MySingleton {
            if (mInstance == null) {
                mInstance = MySingleton(context)
            }
            return mInstance as MySingleton
        }
    }
    
}