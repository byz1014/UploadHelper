package com.resources.uploadlib.http

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.resources.uploadlib.bean.Data
import com.resources.uploadlib.bean.FileResultBean
import com.resources.uploadlib.bean.ResourcesBean
import com.resources.uploadlib.choose.ResourcesState
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


/**
 * @Author:BYZ
 * @Time:2023/10/23 15:18
 * @blame Android Team
 * @info
 */

open class UpLoadTask(var outAction: (mBean: ResourcesBean) -> Unit) : ViewModel() {

    /**
     * 公共方法
     * 启动一个上传任务
     */
    fun initUploadTask(mResourcesBean: ResourcesBean) {
        mResourcesBean.uploadTask()
    }

    /**
     * 这是一个上传任务
     */
    private fun ResourcesBean.uploadTask() {
        val videoFile = File(this.localPath)
        val videoRequestBody = RequestBody.create(this.mediaType.toMediaTypeOrNull(), videoFile)
        this.state = ResourcesState.UPLOAD_START

      var mProgressBody = ProgressRequestBody(videoRequestBody,this,object : ProgressListener {
          override fun onProgress(progress: Int, resBean: ResourcesBean?) {
              this@uploadTask.state = ResourcesState.UPLOAD_ING
              this@uploadTask.progress = "$progress%"
              outAction(this@uploadTask)
          }

          override fun onProgressDone(resBean: ResourcesBean?) {
              this@uploadTask.state = ResourcesState.UPLOAD_ING
              this@uploadTask.progress = "100%"
              outAction(this@uploadTask)
          }
      })

        val videoPart = MultipartBody
            .Part
            .createFormData("file", videoFile.name, mProgressBody)
        UploadRetrofit().uploadApi.uploadFile(videoPart)
            .enqueue(uploadCallBack(this) { bean, resultBean ->
                when(resultBean.code){
                    200->{
                        bean.isUpload = true
                        bean.httpPath = resultBean.data.url
                        bean.id = resultBean.data.adjunctId
                        bean.state = ResourcesState.UPLOAD_SUCCESS
                    }
                    401->{
                        bean.state = ResourcesState.UPLOAD_LOGIN_FAIL
                    }
                    -579->{
                        bean.state = ResourcesState.UPLOAD_HTTP_FAIL
                    }
                    else->{
                    bean.state = ResourcesState.UPLOAD_FAIL
                    }
                }
                outAction(bean)
            })



    }

    /**
     * 自定义回调方法
     * 能够拿到当前请求的对象方便外部处理
     */
    private fun uploadCallBack(
        mBean: ResourcesBean,
        action: (mBean: ResourcesBean, code: FileResultBean) -> Unit
    ): Callback<FileResultBean> {
        return object : Callback<FileResultBean> {
            override fun onResponse(
                call: Call<FileResultBean>?,
                response: Response<FileResultBean>? ) {
                response?.apply {
                    var bean = this.body()
                    if (bean != null) {
                        action(mBean, bean)
                    }
                }
            }

            override fun onFailure(call: Call<FileResultBean>?, t: Throwable?) {
             var msg =  if(t == null)  "" else  t.message?:""
                action(mBean, FileResultBean(-579,Data("","",""),msg,"",0.0))
            }
        }
    }

}





