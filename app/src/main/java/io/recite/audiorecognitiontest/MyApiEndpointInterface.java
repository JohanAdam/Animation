package io.recite.audiorecognitiontest;

import io.recite.audiorecognitiontest.model.QuranSearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by johan on 27/7/2017.
 */

public interface MyApiEndpointInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter
    @GET("api/quransearch")
    Call<QuranSearchResult> searchayat(@Header("X-ZUMO-AUTH") String token,
                                       @Header("ZUMO-API-VERSION") String ver,
                                       @Query("searchword") String searchAyat);

}