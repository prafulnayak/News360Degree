package org.sairaa.news360degree.api;

import org.sairaa.news360degree.model.NewsList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
//    https://newsapi.org/v2/top-headlines?country=in&apiKey=c19366b11c0440848041a33b1745e3d1
    @GET("v2/everything")
    Call<NewsList> getResponse(@Query("q") String name,
                               @Query("from") String date,
                               @Query("sortBy") String popularity,
                               @Query("apiKey") String apiKey,
                               @Query("pageSize") int pageSize,
                               @Query("page") long page);
    @GET("v2/top-headlines")
    Call<NewsList> getTopHeadLine(@Query("country") String cpuntry,
                                  @Query("apiKey") String apiKey);
}
