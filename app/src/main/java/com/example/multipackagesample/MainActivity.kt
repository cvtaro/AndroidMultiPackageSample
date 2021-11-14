package com.example.multipackagesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.dukpt.LibsSampleWrapper

class MainActivity : AppCompatActivity() {

    private lateinit var mDukptWrapper : LibsSampleWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDukptWrapper = LibsSampleWrapper.getInstance()
        mDukptWrapper.initLibSample(this, object : LibsSampleWrapper.DukptWrapperListener{
            override fun listenerDukptLib1() {
                Log.d("[dbg Dukpt app]", "listenerDukptLib1()")
            }
            override fun listenerDukptLib2() {
                Log.d("[dbg Dukpt app]", "listenerDukptLib1()")
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // 初期化が終わったら　OKとか
        mDukptWrapper.runHandle()
    }

}