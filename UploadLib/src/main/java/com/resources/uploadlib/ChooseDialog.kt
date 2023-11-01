package com.resources.uploadlib

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkPermission
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.resources.uploadlib.base.BaseDialog
import com.resources.uploadlib.bean.RequestCodeBean
import com.resources.uploadlib.camera.CameraVideoActivity
import com.resources.uploadlib.choose.ChooseActionState
import com.resources.uploadlib.databinding.ActionItemBinding
import com.resources.uploadlib.databinding.DialogChooseBinding
import com.resources.uploadlib.util.*


/**
 * @Author:BYZ
 * @Time:2023/10/19 13:57
 * @blame Android Team
 * @info
 */
class ChooseDialog(mActivity: Activity, var actionList: MutableList<ChooseActionState>) :
    BaseDialog<DialogChooseBinding>(mActivity) {

    var mRequestCodeBean = RequestCodeBean()

    override fun isTouchOutCancel(): Boolean = true

    override fun onLayout(): Int = R.layout.dialog_choose

    override fun onWeight(binding: DialogChooseBinding) {
        binding.llItemBody.removeAllViews()
        actionList.forEach {
            binding.llItemBody.addView(getItemView(it))
            binding.tvCancel.setOnClickListener { dismiss() }
        }
    }

    fun getItemView(actionState: ChooseActionState): View {
        return DataBindingUtil.inflate<ActionItemBinding>(
            LayoutInflater.from(mActivity),
            R.layout.action_item,
            null,
            false
        ).apply {
            this.mState = actionState
            this.mDialog = this@ChooseDialog
        }.root
    }

    var MAX = 0

    fun show(sum: Int) {
        super.show()
        this.MAX = sum
    }


    fun onItemClick(mState: ChooseActionState) {
        dismiss()
        when (mState) {
            ChooseActionState.CHOOSE_VIDEO -> {//选择视频
                PermissionUtils.permission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).onPermission {
                    PictureSelector
                        .create(mActivity)
                        .ChooseVideo(MAX, VIDEO_MAX_SECOND  * 1000,mRequestCodeBean.mChooseVideoCode)
                }
            }
            ChooseActionState.CHOOSE_PICTURE -> {//选择图片
                PermissionUtils.permission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).onPermission {
                    PictureSelector
                        .create(mActivity)
                        .ChoosePicture(MAX,mRequestCodeBean.mChoosePictureCode)
                }
            }
            ChooseActionState.CHOOSE_PICTURE_AND_VIDEO -> {//选择图片和视频
                PermissionUtils.permission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).onPermission {
                    PictureSelector
                        .create(mActivity)
                        .TakePhoto(mState.actionCode)
                }
            }
            ChooseActionState.CHOOSE_TAKE_PHOTO -> {//拍摄图片
                PermissionUtils.permission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).onPermission {
                    PictureSelector
                        .create(mActivity)
                        .TakePhoto(mRequestCodeBean.mPictureCode)
                }
            }
            ChooseActionState.CHOOSE_CAMERA -> {//拍摄视频
                PermissionUtils.permission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                ).onPermission {
                    Intent(mActivity, CameraVideoActivity::class.java).apply {
                        mActivity.startActivityForResult(this,mRequestCodeBean.mVideoCode)
                    }
                }
            }
        }

        mListener?.apply {
            onActionItem(mState)
        }
    }


    var mListener: OnChooseItemListener? = null

    interface OnChooseItemListener {
        fun onActionItem(state: ChooseActionState)
    }


    fun PermissionUtils.onPermission(action: () -> Unit) {
        callback(object :
            PermissionUtils.SimpleCallback {
            override fun onGranted() {
                action()
            }
            override fun onDenied() {
                ToastUtils.showShort("缺少必要权限，请手动授权")
            }
        }).request()
    }

}