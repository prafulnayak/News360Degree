package org.sairaa.news360degree.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.sairaa.news360degree.CommonUtils;
import org.sairaa.news360degree.R;
import org.sairaa.news360degree.api.ApiUtils;
import org.sairaa.news360degree.api.NewsApi;
import org.sairaa.news360degree.db.News;
import org.sairaa.news360degree.db.NewsDatabase;
import org.sairaa.news360degree.model.NewsList;

import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackGroundUtils {
    Context context;
    private static final String APIKEY = "c19366b11c0440848041a33b1745e3d1";
    private CommonUtils commonUtils;

    public BackGroundUtils(Context context) {
        this.context = context;
        commonUtils = new CommonUtils(context);
    }

    public void fatchLatestNews() {
        String countryName = context.getResources().getConfiguration().locale.getDisplayCountry();
        String countryCode = getCountryCode(countryName);
//        Toast.makeText(context,"country "+countryCode,Toast.LENGTH_SHORT).show();
        final NewsDatabase mDb = NewsDatabase.getsInstance(context);
        NewsApi newsApi = ApiUtils.getNewsApi();
        newsApi.getTopHeadLine(countryCode,APIKEY).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                final NewsList newsList = response.body();

                for(int i =0;i<newsList.getNewsDataList().size();i++){
                    final int position = i;
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            List<News> newsL = mDb.newsDao().getSingleNews(newsList.getNewsDataList().get(position).getTitle());
                            if(newsL.isEmpty()){
                                News news = new News(newsList.getNewsDataList().get(position).getAuthor() == null ?"NewsApi" :newsList.getNewsDataList().get(position).getAuthor(),
                                        newsList.getNewsDataList().get(position).getTitle() == null ? "" : newsList.getNewsDataList().get(position).getTitle(),
                                        newsList.getNewsDataList().get(position).getDescription() == null? "" :newsList.getNewsDataList().get(position).getDescription(),
                                        newsList.getNewsDataList().get(position).getUrl() == null? "":newsList.getNewsDataList().get(position).getUrl(),
                                        newsList.getNewsDataList().get(position).getUrlToImage()== null? "":newsList.getNewsDataList().get(position).getUrlToImage(),
                                        newsList.getNewsDataList().get(position).getPublishedAt()== null? "":newsList.getNewsDataList().get(position).getPublishedAt(),
                                        1);
                                insertNewsToDb(news,mDb);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {

            }
        });
    }

    private void insertNewsToDb(final News news, final NewsDatabase mDb) {
        String path = commonUtils.uploadImageToInternalStorage(news.getUrlToImage());
//         = uploadImageToInternalStorage(news.getUrlToImage(),getApplicationContext());
        news.setUrlToImage(path);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mDb.newsDao().insert(news);
            }
        });
    }

    private String getCountryCode(String countryName) {
        if (countryName.equals(context.getString(R.string.india))) {
            return context.getString(R.string.india_code);

        }
        return context.getString(R.string.india_code);
    }
}
