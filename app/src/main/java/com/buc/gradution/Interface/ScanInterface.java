package com.buc.gradution.Interface;

import com.buc.gradution.Model.ScanOutputModel;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ScanInterface {
        // Old API url  "vzrad2/1"
        // New API url  "xraysflow/1"
        @POST("dental_diseases/2")
        Call<ScanOutputModel> postImage(
                @Query("api_key") String apiKey,
                @Query("image") String image
        );
}
