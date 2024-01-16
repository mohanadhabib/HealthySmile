package com.buc.gradution.Service;

import com.buc.gradution.Model.ScanOutputModel;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ScanInterface {
        @POST("vzrad2/1")
        Call<ScanOutputModel> postImage(
                @Query("api_key") String apiKey,
                @Query("image") String image
        );
}
