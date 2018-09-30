package org.sairaa.news360degree.service;

import android.content.Context;

import org.sairaa.news360degree.utils.CommonUtils;
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
    //fatch latest news and insert it into room and notifies user on new news arrival
    public void fatchLatestNews() {
        //get local country name
        String countryName = context.getResources().getConfiguration().locale.getDisplayCountry();
        //get the country code required for retrival of respective country news
        String countryCode = getCountryCode(countryName);
        //get instance of room database
        final NewsDatabase mDb = NewsDatabase.getsInstance(context);
        NewsApi newsApi = ApiUtils.getNewsApi();
        //make retrofit call to retrieve top headline of the respective country
        newsApi.getTopHeadLine(countryCode,APIKEY).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                final NewsList newsList = response.body();

                for(int i =0;i<newsList.getNewsDataList().size();i++){
                    final int position = i;
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            //check whether same news object available or not
                            //if not available insert that news object to room database
                            List<News> newsL = mDb.newsDao().getSingleNews(newsList.getNewsDataList().get(position).getTitle());
                            if(newsL.isEmpty()){
                                News news = new News(newsList.getNewsDataList().get(position).getAuthor() == null ?"NewsApi" :newsList.getNewsDataList().get(position).getAuthor(),
                                        newsList.getNewsDataList().get(position).getTitle() == null ? "" : newsList.getNewsDataList().get(position).getTitle(),
                                        newsList.getNewsDataList().get(position).getDescription() == null? "" :newsList.getNewsDataList().get(position).getDescription(),
                                        newsList.getNewsDataList().get(position).getUrl() == null? "":newsList.getNewsDataList().get(position).getUrl(),
                                        newsList.getNewsDataList().get(position).getUrlToImage()== null? "":newsList.getNewsDataList().get(position).getUrlToImage(),
                                        newsList.getNewsDataList().get(position).getPublishedAt()== null? "":newsList.getNewsDataList().get(position).getPublishedAt(),
                                        1);
                                //insert to room database
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
    //insert to room database
    private void insertNewsToDb(final News news, final NewsDatabase mDb) {
        if(!news.getUrlToImage().equals("")){
            //insert image to internal storage and get the path and set it to news object
            String path = commonUtils.uploadImageToInternalStorage(news.getUrlToImage());
            news.setUrlToImage(path);
        }
        //format the date and time and set it to news object
        String dateTime = CommonUtils.getDate(news.getPublishedAt()).concat(" ").concat(CommonUtils.getTime(news.getPublishedAt()));
        news.setPublishedAt(dateTime);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mDb.newsDao().insert(news);
                //if new data is inserted to database notify to user
                if(!news.getTitle().isEmpty())
                    CommonUtils.showNotification(context,news.getTitle());
            }
        });
    }

    private String getCountryCode(String countryName) {
        if (countryName.equals(context.getString(R.string.india))) {
            return context.getString(R.string.india_code);

        }
        //you can add other countries code to access respective country news
        return context.getString(R.string.india_code);
    }
}
