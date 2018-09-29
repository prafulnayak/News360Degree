package org.sairaa.news360degree;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.sairaa.news360degree.api.ApiUtils;
import org.sairaa.news360degree.api.NewsApi;
import org.sairaa.news360degree.db.News;
import org.sairaa.news360degree.db.NewsDatabase;
import org.sairaa.news360degree.model.NewsList;
import org.sairaa.news360degree.service.ServiceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private NewsViewModel viewModel;
    private static int id = 5;
    DialogAction dialogAction;
    CommonUtils commonUtils;
    FloatingActionButton floatingActionButton;

    private static final String APIKEY = "c19366b11c0440848041a33b1745e3d1";//"079dac74a5f94ebdb990ecf61c8854b7";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"ondestroy",Toast.LENGTH_SHORT).show();
        ServiceUtils serviceUtils = new ServiceUtils();
        serviceUtils.scheduleTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.floatingActionButton2);
        dialogAction = new DialogAction(this);
        commonUtils = new CommonUtils(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        adapter = new NewsAdapter(this);
        recyclerView.setAdapter(adapter);
        insertDummy(Executors.newSingleThreadExecutor());
        subscribeUi(adapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    floatingActionButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 ||dy<0 && floatingActionButton.isShown())
                    floatingActionButton.hide();
            }
        });

    }

    private String getCountryCode(String countryName) {
        if (countryName.equals(getString(R.string.india))) {
            return getString(R.string.india_code);

        }
        return getString(R.string.india_code);
    }

    private void subscribeUi(final NewsAdapter adapter) {

        viewModel.getNewsListLiveData().observe(this, new Observer<PagedList<News>>() {
            @Override
            public void onChanged(@Nullable PagedList<News> news) {
//                Log.e("data sub: ",news.get(1).getTitle());
//                Log.e("data sub: ",news.get(0).getTitle());

                recyclerView.setAdapter(adapter);
                adapter.submitList(news);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummy(Executors.newSingleThreadExecutor());
                //displayDatabaseInfo();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummy(final Executor executor) {
        String countryName = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();
        String countryCode = getCountryCode(countryName);
        Toast.makeText(this,"country "+countryCode,Toast.LENGTH_SHORT).show();
        final NewsDatabase mDb = NewsDatabase.getsInstance(this);
        NewsApi newsApi = ApiUtils.getNewsApi();
        dialogAction.showDialog("News","Retrieving");
        newsApi.getTopHeadLine(countryCode,APIKEY).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                final NewsList newsList = response.body();

                for(int i =0;i<newsList.getNewsDataList().size();i++){

//                    Log.e("hello","success"+newsList.getNewsDataList().get(i).getUrl());
                    final int position = i;
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<News> newsL = mDb.newsDao().getSingleNews(newsList.getNewsDataList().get(position).getTitle());
                            if(newsL.isEmpty()){


                                News news = new News(newsList.getNewsDataList().get(position).getAuthor() == null ?"NewsApi" :newsList.getNewsDataList().get(position).getAuthor(),
                                        newsList.getNewsDataList().get(position).getTitle() == null ? "" : newsList.getNewsDataList().get(position).getTitle(),
                                        newsList.getNewsDataList().get(position).getDescription() == null? "" :newsList.getNewsDataList().get(position).getDescription(),
                                        newsList.getNewsDataList().get(position).getUrl() == null? "":newsList.getNewsDataList().get(position).getUrl(),
//                                        newsList.getNewsDataList().get(position).getUrlToImage()== null? "": path,
                                        newsList.getNewsDataList().get(position).getUrlToImage()== null? "":newsList.getNewsDataList().get(position).getUrlToImage(),
                                        newsList.getNewsDataList().get(position).getPublishedAt()== null? "":newsList.getNewsDataList().get(position).getPublishedAt(),
                                        1);
                                try {
                                    insertNewsToDb(news,mDb);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
                dialogAction.hideDialog();

            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                dialogAction.hideDialog();
            }
        });
//        subscribeUi(adapter);
    }

    private void insertNewsToDb(final News news, final NewsDatabase mDb) throws IOException {
        String path = commonUtils.uploadImageToInternalStorage(news.getUrlToImage());
//         = uploadImageToInternalStorage(news.getUrlToImage(),getApplicationContext());
        news.setUrlToImage(path);
        String dateTime = CommonUtils.getDate(news.getPublishedAt()).concat(", ").concat(CommonUtils.getTime(news.getPublishedAt()));
        news.setPublishedAt(dateTime);
        Log.e("hello","imageUri: "+path);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mDb.newsDao().insert(news);
            }
        });
    }
}
