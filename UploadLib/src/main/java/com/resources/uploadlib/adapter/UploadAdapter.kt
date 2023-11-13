package com.resources.uploadlib.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import com.resources.uploadlib.*
import com.resources.uploadlib.bean.RequestCodeBean
import com.resources.uploadlib.bean.ResourcesBean
import com.resources.uploadlib.camera.getFirstFrame
import com.resources.uploadlib.choose.ChooseActionState
import com.resources.uploadlib.choose.ResourcesState
import com.resources.uploadlib.choose.ResourcesType
import com.resources.uploadlib.databinding.AdapterUploadItemBinding
import com.resources.uploadlib.gallery.GalleryPreviewActivity
import com.resources.uploadlib.util.*
import kotlin.math.max


/**
 * @Author:BYZ
 * @Time:2023/10/19 10:08
 * @blame Android Team
 * @info
 */
class UploadAdapter(mList: MutableList<ResourcesBean>,var uploadAction:()->Unit) : BaseQuickAdapter<ResourcesBean,
        BaseDataBindingHolder<AdapterUploadItemBinding>>(R.layout.adapter_upload_item, mList) {



    var actionStateList = mutableListOf<ChooseActionState>().apply {
        add(ChooseActionState.CHOOSE_PICTURE)
        add(ChooseActionState.CHOOSE_VIDEO)
        add(ChooseActionState.CHOOSE_TAKE_PHOTO)
        add(ChooseActionState.CHOOSE_CAMERA)
    }


    override fun convert(
        holder: BaseDataBindingHolder<AdapterUploadItemBinding>,item: ResourcesBean ) {

        holder.dataBinding?.apply {
            this.isLooker = IS_LOOKER
            this.mResourcesBean = item
            this.mAdapter = this@UploadAdapter
            this.mPosition = holder.adapterPosition
            ivSmallIcon.visibility = View.VISIBLE
            when (item.type) {
                ResourcesType.ADD -> {
                    rivContent.setImageResource(R.drawable.shape_round_white)
                    ivSmallIcon.setImageResource(R.mipmap.ic_upload_add)
                }
                ResourcesType.VIDEO -> {
                    if (item.coverImage == null) {
                        item.httpPath.ifEmpty { item.localPath }
                            .getImage(holder.adapterPosition) { position, mBitmap ->
                                item.coverImage = mBitmap
                                mBitmap?.apply {
                                    notifyItemChanged(position)
                                }
                            }
                    }
                    item.coverImage.showCoverImage(rivContent)
                    ivSmallIcon.setImageResource(R.mipmap.ic_upload_play)
                }
                ResourcesType.AUDIO -> {
                    ivSmallIcon.setImageResource(R.mipmap.ic_upload_recoder)
                }
                ResourcesType.PICTURE -> {
                    ivSmallIcon.visibility = View.GONE
                    var path = if (item.localPath.isEmpty()) {
                        item.httpPath
                    } else {
                        item.localPath
                    }
                    Glide.with(context)
                        .load(path)
                        .error(R.mipmap.uplib_ic_img_failed)
                        .fallback(R.mipmap.uplib_ic_img_failed)
                        .placeholder(R.mipmap.uplib_ic_img_loading)
                        .into(rivContent)
                }
            }
            if (item.state == ResourcesState.UPLOAD_ING) {
//                tvStatus.text = "上传中..."
            } else if (item.state == ResourcesState.UPLOAD_START) {
//                tvStatus.text = "开始上传"
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onItemClick(position: Int, index: Int) {
        when (index) {
            0 -> {//删除
//                this.data.removeAt(position)
                if(data[position].state == ResourcesState.UPLOAD_ING){
                    ToastUtils.showShort("上传中，不可删除")
                    return
                }
                checkAddItem(position)
            }
            1 -> {//主图
                when (getItem(position).type) {
                    ResourcesType.ADD -> {
                        ChooseDialog((context as Activity),actionStateList).apply {
                            setRequestCodeBean(mRequestCodeBean)
                            setPictureMaxSize(PICTURE_MAX_SIZE)
                            setVideoMaxSecond(VIDEO_MAX_SECOND)
                            setVideoMaxSize(VIDEO_MAX_SIZE  )
                        }.show(FILE_MAX - data.size + 1)
                    }
                    ResourcesType.PICTURE,
                    ResourcesType.VIDEO -> {
                        Intent(context, GalleryPreviewActivity::class.java).apply {
                            putExtra("resJson", Gson().toJson(this@UploadAdapter.data))
                            putExtra("position", position)
                            var maxIndex = this@UploadAdapter.data.size
                            this@UploadAdapter.data.any { it.type == ResourcesType.ADD }.apply {
                                if (this) {
                                    maxIndex = this@UploadAdapter.data.size - 1
                                }
                            }
                            putExtra("maxNum", maxIndex)
                            context.startActivity(this)
                        }
                    }
                }
            }
            -1 -> {//error 重新上传
                getItem(position).state = ResourcesState.UPLOAD_START
                notifyItemChanged(position)
                getItem(position).onUpLoad {
                    if(it.state == ResourcesState.UPLOAD_LOGIN_FAIL){
                        uploadAction()
                    }
                    getItemPosition(it).apply {
                        handler.post {
                            notifyItemChanged(this)
                        }
                    }
                }
            }
        }
    }

    var handler = Handler(Looper.getMainLooper())


    fun Bitmap?.showCoverImage(rivContent: RoundedImageView) {
        Glide.with(context)
            .load(this)
            .error(R.drawable.shape_round_white)
            .fallback(R.drawable.shape_round_white)
            .placeholder(R.drawable.shape_round_white)
            .into(rivContent)
    }


    /**
     * 获取视频第一帧是个耗时任务
     *
     */
    fun String.getImage(position: Int, action: (position: Int, bitmap: Bitmap?) -> Unit) {
        Thread {
            getFirstFrame().apply {
                handler.post {
                    action(position, this)
                }
            }
        }.start()
    }

    private var IS_LOOKER = false //是否只读
    private var FILE_MAX = 10009//文件最大数量
    private var VIDEO_MAX_SECOND = 15//视频最大时长
    private var VIDEO_MAX_SIZE = 260.0F//视频最大内存 M
    private var PICTURE_MAX_SIZE = 5.0F//图片大小 M
    var mRequestCodeBean = RequestCodeBean()

    fun setIsLooker(isLooker:Boolean){
        this.IS_LOOKER = isLooker
    }
    fun getIsLooker():Boolean{
        return  this.IS_LOOKER
    }

    fun setFileMax(fileMax:Int){
        this.FILE_MAX = fileMax
    }
    fun setVideoMaxSecond(maxSecond:Int){
        this.VIDEO_MAX_SECOND = maxSecond
    }
    fun setVideoMaxSize(maxSize:Float){
        this.VIDEO_MAX_SIZE = maxSize
    }
    fun setPictureMaxSize(maxSize:Float){
        this.PICTURE_MAX_SIZE = maxSize
    }


}

