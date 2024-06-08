package com.buc.gradution.interfaceType;

import com.buc.gradution.model.ScanOutputModel;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ScanInterface {
        // Old API url  "vzrad2/1"
        // New API url  "xraysflow/1"
        @POST("dental_yolo_v8/420")
        Call<ScanOutputModel> postImage(
                @Query("api_key") String apiKey,
                @Query("image") String image
        );
        @POST("oral_detection_diseases/1")
        Call<ScanOutputModel> postCapturedImage(
                @Query("api_key") String apiKey,
                @Query("image") String image
        );
}
