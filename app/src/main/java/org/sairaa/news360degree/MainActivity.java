package org.sairaa.news360degree;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.sairaa.news360degree.api.ApiUtils;
import org.sairaa.news360degree.api.NewsApi;
import org.sairaa.news360degree.db.News;
import org.sairaa.news360degree.db.NewsDatabase;
import org.sairaa.news360degree.model.NewsList;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private NewsViewModel viewModel;
    private static int id = 5;

    private static final String QUERY = "Apple";
    private static final String FROM = "2018-09-26";
    private static final String SORTBY="popularity";
    private static final String APIKEY = "079dac74a5f94ebdb990ecf61c8854b7";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        adapter = new NewsAdapter(this);
        recyclerView.setAdapter(adapter);

        subscribeUi(adapter);
    }

    private void subscribeUi(final NewsAdapter adapter) {
        viewModel.getNewsListLiveData().observe(this, new Observer<PagedList<News>>() {
            @Override
            public void onChanged(@Nullable PagedList<News> news) {
//                Log.e("data sub: ",news.get(1).getTitle());
//                Log.e("data sub: ",news.get(0).getTitle());
                adapter.submitList(news);
                adapter.notifyDataSetChanged();
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
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummy(final Executor executor) {
        final NewsDatabase mDb = NewsDatabase.getsInstance(this);
        NewsApi newsApi = ApiUtils.getNewsApi();
        newsApi.getTopHeadLine("in",APIKEY).enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                final NewsList newsList = response.body();

                for(int i =0;i<newsList.getNewsDataList().size();i++){
                    Log.e("hello","success"+newsList.getNewsDataList().get(i).getUrl());
                    final int position = i;
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<News> newsL = mDb.newsDao().getSingleNews(newsList.getNewsDataList().get(position).getTitle());
                            if(newsL.isEmpty()){
                                News news = new News(newsList.getNewsDataList().get(position).getAuthor() == null ?"NewsApi" :newsList.getNewsDataList().get(position).getAuthor(),
                                        newsList.getNewsDataList().get(position).getTitle(),
                                        newsList.getNewsDataList().get(position).getDescription(),
                                        newsList.getNewsDataList().get(position).getUrl(),
                                        newsList.getNewsDataList().get(position).getUrlToImage(),
                                        newsList.getNewsDataList().get(position).getPublishedAt(),1);
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
//        newsApi.getResponse(QUERY,FROM,SORTBY,APIKEY,5,1).enqueue(new Callback<NewsList>() {
//            @Override
//            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
//                NewsList newsList = response.body();
//                for(int i =0;i<newsList.getNewsDataList().size();i++){
//                    Log.e("hello","success"+newsList.getNewsDataList().get(i).getUrl());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<NewsList> call, Throwable t) {
//
//            }
//        });
//        id = id++;

        final News news = new News("aaa","yyyyy","aaas","dddd","dddddf","sdded",1);


    }

    private void insertNewsToDb(final News news, final NewsDatabase mDb) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mDb.newsDao().insert(news);
            }
        });
    }
}
