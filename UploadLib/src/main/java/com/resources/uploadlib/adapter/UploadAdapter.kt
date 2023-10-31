package com.resources.uploadlib.adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.makeramen.roundedimageview.RoundedImageView
import com.resources.uploadlib.*
import com.resources.uploadlib.bean.ResourcesBean
import com.resources.uploadlib.choose.ChooseActionState
import com.resources.uploadlib.choose.ResourcesState
import com.resources.uploadlib.choose.ResourcesType
import com.resources.uploadlib.databinding.AdapterUploadItemBinding
import com.resources.uploadlib.gallery.GalleryPreviewActivity


/**
 * @Author:BYZ
 * @Time:2023/10/19 10:08
 * @blame Android Team
 * @info
 */
class UploadAdapter(mList: MutableList<ResourcesBean>) : BaseQuickAdapter<ResourcesBean,
        BaseDataBindingHolder<AdapterUploadItemBinding>>(R.layout.adapter_upload_item, mList) {


    override fun convert(
        holder: BaseDataBindingHolder<AdapterUploadItemBinding>,
        item: ResourcesBean
    ) {

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
                    Glide.with(context)
                        .load(item.coverImage)
                        .error(R.drawable.shape_round_white)
                        .fallback(R.drawable.shape_round_white)
                        .placeholder(R.drawable.shape_round_white)
                        .into(rivContent)
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

    fun onItemClick(position: Int, index: Int) {
        when (index) {
            0 -> {//删除
                    this.data.removeAt(position)
                    checkAddItem()
            }
            1 -> {//主图
                when (getItem(position).type) {
                    ResourcesType.ADD -> {
                        (context as Activity).showChooseDialog(FILE_MAX - data.size + 1)
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
}
