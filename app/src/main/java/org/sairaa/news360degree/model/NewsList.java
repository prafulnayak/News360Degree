package org.sairaa.news360degree.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsList {

    private String status;
    private String totalResults;
    @SerializedName("articles")
    private List<NewsData> newsDataList;

    public List<NewsData> getNewsDataList() {
        return newsDataList;
    }

    public String getStatus() {
        return status;
    }

    public String getTotalResults() {
        return totalResults;
    }
}
