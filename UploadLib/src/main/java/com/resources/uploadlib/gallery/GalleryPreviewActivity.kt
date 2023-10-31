package com.resources.uploadlib.gallery

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.resources.uploadlib.R
import com.resources.uploadlib.adapter.GalleryAdapter
import com.resources.uploadlib.bean.ResourcesBean
import com.resources.uploadlib.choose.ResourcesType
import com.resources.uploadlib.databinding.ActivityGalleryPreviewBinding
/**
 * @Author:BYZ
 * @Time:2023/10/28 11:22
 * @blame Android Team
 * @info
 */
class GalleryPreviewActivity : AppCompatActivity() {
    var mArrayResourcesBeanjson = ""
    var mResourceIndex = -1
    var maxIndex = 9
    var mFragmentList = mutableListOf<PreviewFragment>()

    val mBinding: ActivityGalleryPreviewBinding by lazy {
        DataBindingUtil.setContentView(
            this,
            R.layout.activity_gallery_preview
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra("resJson")?.apply {  mArrayResourcesBeanjson = this }
        mResourceIndex = intent.getIntExtra("position",0)
        maxIndex = intent.getIntExtra("maxNum",0)
        mBinding.mShowIndex = "${mResourceIndex+1}/$maxIndex"
        var mList:MutableList<ResourcesBean> = Gson().fromJson(mArrayResourcesBeanjson, object : TypeToken<MutableList<ResourcesBean>>(){}.type)
        mFragmentList.clear()
        mList.forEach {
            if(it.type != ResourcesType.ADD){
                mFragmentList.add(getPreviewFragment(it))
            }
        }
        mBinding.mGalleryAdapter = GalleryAdapter(this,mFragmentList)
        mBinding.ivBack.setOnClickListener { finish() }


        mBinding.vpBody.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mBinding.mShowIndex = "${position+1}/$maxIndex"
            }

        })
        Handler(Looper.getMainLooper()).postDelayed({
            mBinding.vpBody.setCurrentItem(mResourceIndex,false)
        },100)
    }



    fun getPreviewFragment(mBean: ResourcesBean):PreviewFragment{
        return PreviewFragment(mBean)
    }
}