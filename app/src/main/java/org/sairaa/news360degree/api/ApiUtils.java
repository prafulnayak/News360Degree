package org.sairaa.news360degree.api;

import android.util.Log;

public class ApiUtils {
    public static final String NEWS_URL="https://newsapi.org";

    public static NewsApi getNewsApi(){
        return RetrofitClient.getClient(NEWS_URL).create(NewsApi.class);
    }

}
