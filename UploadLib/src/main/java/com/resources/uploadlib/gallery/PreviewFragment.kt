package com.resources.uploadlib.gallery

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.resources.uploadlib.R
import com.resources.uploadlib.bean.ResourcesBean
import com.resources.uploadlib.camera.getFirstFrame
import com.resources.uploadlib.choose.ResourcesType
import com.resources.uploadlib.databinding.FragmentPreviewBinding
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import java.io.File


/**
 * @Author:BYZ
 * @Time:2023/10/28 9:50
 * @blame Android Team
 * @info
 */
class PreviewFragment(var mResourcesBean: ResourcesBean) : Fragment() {

    var mFragmentBinding:FragmentPreviewBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return DataBindingUtil
            .inflate<FragmentPreviewBinding>(
                inflater,
                R.layout.fragment_preview,
                container, false
            ).apply {
                mFragmentBinding  = this
                isVideo = (mResourcesBean.type == ResourcesType.VIDEO)
                when(mResourcesBean.type){
                    ResourcesType.VIDEO ->{
                        showVideo(this)
                    }
                    ResourcesType.PICTURE->{
                        showPicture(this)
                    }
                }
            }.root
    }

    private fun showPicture(mBinding: FragmentPreviewBinding) {
        mResourcesBean.localPath.ifEmpty { mResourcesBean.httpPath }.apply {
            Glide.with(requireActivity())
                .load(this)
                .into(mBinding.uploadPhotoView)
        }
    }

    private fun showVideo(mBinding: FragmentPreviewBinding) {
        mResourcesBean.localPath.ifEmpty { mResourcesBean.httpPath }.let {
            var thumbImageView = ImageView(requireActivity())
            thumbImageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            it.getImage { bitmap ->
                thumbImageView.setImageBitmap(bitmap)
                GSYVideoOptionBuilder().apply {
                    setThumbImageView(thumbImageView)
                        .setIsTouchWiget(true)
                        .setRotateViewAuto(false)
                        .setLockLand(false)
                        .setAutoFullWithSize(true)
                        .setShowFullAnimation(false)
                        .setNeedLockFull(true)
                        .setUrl(it)
                        .setCacheWithPlay(false)
                        .build(mBinding.svpDetailPlayer)
                }
            }



        }
    }


    var handler = Handler(Looper.getMainLooper())


    private fun String.getImage(action:(Bitmap?)->Unit){
        Thread{
            getFirstFrame().apply {
                handler.post {
                    action(this)
                }
            }
        }.start()
    }


    override fun onPause() {
        if(mResourcesBean.type == ResourcesType.VIDEO){
            mFragmentBinding?.apply {this.svpDetailPlayer.currentPlayer.onVideoPause() }
        }
        super.onPause()
    }

    override fun onResume() {
        if(mResourcesBean.type == ResourcesType.VIDEO){
            mFragmentBinding?.apply { this.svpDetailPlayer.currentPlayer.onVideoResume(false)  }
        }
        super.onResume()
    }

    override fun onDestroy() {
        if(mResourcesBean.type == ResourcesType.VIDEO){
            mFragmentBinding?.apply {  this.svpDetailPlayer.currentPlayer.release() }
        }
        super.onDestroy()
    }

}