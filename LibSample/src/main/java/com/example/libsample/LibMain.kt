package com.example.libsample

import android.content.Context
import android.util.Log

class LibMain {
    private lateinit var mContext: Context
    private lateinit var mListener: LibsEventListener
    lateinit var mDukpt: Dukpt

    fun initLibMain(context: Context, listener: LibsEventListener) {
        mContext = context
        mListener = listener

        mDukpt = Dukpt.getInstance()
    }

    interface LibsEventListener {
        fun listener1()
        fun listener2()
        fun listener3()
    }
}

class Dukpt() {

        private lateinit var mContext: Context
        private lateinit var mListener: ListenerDukptLib
        private var mCnt1 = 0;
        private var mCnt2 = 0;

    companion object {
        private var instance: Dukpt? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: Dukpt().also { instance = it }
        }
    }

    fun initDukptLib(context: Context, listener: ListenerDukptLib) {
        mContext = context
        mListener = listener
    }

        interface ListenerDukptLib {
            fun listenerDukptLib1() {
                Log.d("[dbg Dukpt Lib]", "listenerDukptLib1()")
            }

            fun listenerDukptLib2() {
                Log.d("[dbg Dukpt Lib]", "listenerDukptLib2()")
            }
        }

        fun runHandle() {
            Thread {
                Thread.sleep(2000)
                mCnt1++
                mListener.listenerDukptLib1()
                Thread.sleep(2000)
                mCnt2++
                mListener.listenerDukptLib2()
                Thread.sleep(1000)
                mCnt1++
                mCnt2++
                printCnts()
            }.start()
        }

        private fun printCnts() {
            Log.d("[dbg Dukpt Lib]", "printCnts() cnt1 = $mCnt1, cnt2 = $mCnt2")
        }

        fun readCnt1(): Int {
            Log.d("[dbg Dukpt Lib]", "readCnt1() cnt1 = $mCnt1")
            return mCnt1
        }

        fun readCnt2(): Int {
            Log.d("[dbg Dukpt Lib]", "readCnt2() cnt2 = $mCnt2")
            return mCnt1
        }
}
