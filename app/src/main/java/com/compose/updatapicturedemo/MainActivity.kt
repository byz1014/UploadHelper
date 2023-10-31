package com.compose.updatapicturedemo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.resources.uploadlib.UpLoadFactory
import com.resources.uploadlib.bean.ResourcesBean

class MainActivity : AppCompatActivity() {
    val resList = mutableListOf<ResourcesBean>()
    val upLoadFactory by lazy { UpLoadFactory( this) }

    val rvBody:RecyclerView by lazy { findViewById(R.id.rv_body) }
    val tv_message:TextView by lazy { findViewById(R.id.tv_message) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        upLoadFactory.addAdapter(rvBody,this,15)
//        tv_message.setOnClickListener {
//        }

        upLoadFactory.addAdapter(rvBody,this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        upLoadFactory.onResultData(requestCode, resultCode, data)
    }
}