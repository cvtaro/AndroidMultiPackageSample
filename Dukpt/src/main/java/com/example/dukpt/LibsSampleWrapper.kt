package com.example.dukpt

import android.content.Context
import android.util.Log
import com.example.libsample.Dukpt
import com.example.libsample.LibMain

class LibsSampleWrapper {

    companion object {
        private var instance : LibsSampleWrapper? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: LibsSampleWrapper().also { instance = it }
        }
    }

    private lateinit var mContext: Context
    private lateinit var mDukpt: Dukpt
    private lateinit var mListener: DukptWrapperListener

    fun initLibSample(context: Context, listener: DukptWrapperListener) {
        mContext = context
        mListener = listener

        mDukpt = LibMain().mDukpt
        mDukpt.initDukptLib(mContext, LibDucptListener)
    }

    val LibDucptListener = object : Dukpt.ListenerDukptLib {
        override fun listenerDukptLib1() {
            super.listenerDukptLib1()
            Log.d("[dbg Dukpt wrapper]", "listenerDukptLib1()")
            mListener.listenerDukptLib1()
        }
        override fun listenerDukptLib2() {
            super.listenerDukptLib2()
            Log.d("[dbg Dukpt wrapper]", "listenerDukptLib1()")
            mListener.listenerDukptLib2()
        }
    }

    interface DukptWrapperListener {
        fun listenerDukptLib1()
        fun listenerDukptLib2()
    }

    fun runHandle() {
        mDukpt.runHandle()
    }

    fun readCnt1() : Int{
        Log.d("[dbg Dukpt wrapper]", "readCnt1()")
        return mDukpt.readCnt1()
    }
    fun readCnt2() : Int{
        Log.d("[dbg Dukpt wrapper]", "readCnt2()")
        return mDukpt.readCnt1()
    }

}