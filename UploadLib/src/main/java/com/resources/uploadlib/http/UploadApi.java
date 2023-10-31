package com.resources.uploadlib.http;


import com.resources.uploadlib.bean.FileResultBean;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * @author LZY
 * @time 2021/3/29.
 */
public interface UploadApi {

    @Multipart
    @POST("/smart/file/uploadWithSave")
    Call<FileResultBean> uploadFile(@Part MultipartBody.Part image);

    @Streaming
    @GET("rcm/upload/downloadTemplate")
    Call<ResponseBody> downloadFile(@Query("uploadFilePath") String fileUrl, @Query("fileName") String fileName);
}
