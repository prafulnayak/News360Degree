package org.sairaa.news360degree;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.sairaa.news360degree.R;
import org.sairaa.news360degree.api.ApiUtils;
import org.sairaa.news360degree.api.NewsApi;
import org.sairaa.news360degree.db.News;
import org.sairaa.news360degree.db.NewsDatabase;
import org.sairaa.news360degree.model.NewsList;
import org.sairaa.news360degree.utils.CheckConnection;
import org.sairaa.news360degree.utils.CommonUtils;
import org.sairaa.news360degree.utils.DialogAction;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private NewsViewModel viewModel;
    private CheckConnection checkConnection;
    static DialogAction dialogAction;
    CommonUtils commonUtils;
    FloatingActionButton floatingActionButton;
    private DrawerLayout mDrawerLayout;
    private static final String APIKEY = "c19366b11c0440848041a33b1745e3d1";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        //get the category details from intent
        String category = intent.getStringExtra(getString(R.string.category));

        Toolbar toolbar = findViewById(R.id.toolbar_cat);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(getSupportActionBar() != null){
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(category);
        }

        checkConnection = new CheckConnection(this);
        floatingActionButton = findViewById(R.id.fab_cat);
        dialogAction = new DialogAction(this);
        commonUtils = new CommonUtils(this);
        recyclerView = findViewById(R.id.recyclerview_cat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);

        adapter = new NewsAdapter(this);
        //if network is connected retrieve category news and insert it to room
        if(checkConnection.isConnected())
            insertNewsToDb(Executors.newSingleThreadExecutor(),category);
        else
            Toast.makeText(this,getString(R.string.network),Toast.LENGTH_LONG).show();
        //set UI
        subscribeUi(adapter,commonUtils.getBookMark(category));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    //Retrieve categories news and insert it into room and notifies user on new news arrival
    private void insertNewsToDb(final Executor executor, final String category) {
        //get local country name
        String countryName = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();
        //get the country code required for retrival of respective country news
        String countryCode = commonUtils.getCountryCode(countryName);
        //get instance of room database
        final NewsDatabase mDb = NewsDatabase.getsInstance(this);
        NewsApi newsApi = ApiUtils.getNewsApi();
        dialogAction.showDialog(getString(R.string.app_name),getString(R.string.retrieve));
        //make retrofit call to retrieve category news of the respective country
        newsApi.getTopHeadLineCategory(countryCode,category,APIKEY).enqueue(new Callback<NewsList>() {

            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {

                final NewsList newsList = response.body();

                for(int i =0;i<newsList.getNewsDataList().size();i++){

                    final int position = i;
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            //check whether same news object available or not
                            //if not available insert that news object to room database
                            List<News> newsL = mDb.newsDao().getSingleNews(newsList.getNewsDataList().get(position).getTitle());
                            if(newsL.isEmpty()){

                                News news = new News(newsList.getNewsDataList().get(position).getAuthor() == null ? getString(R.string.newsApi) :newsList.getNewsDataList().get(position).getAuthor(),
                                        newsList.getNewsDataList().get(position).getTitle() == null ? "" : newsList.getNewsDataList().get(position).getTitle(),
                                        newsList.getNewsDataList().get(position).getDescription() == null? "" :newsList.getNewsDataList().get(position).getDescription(),
                                        newsList.getNewsDataList().get(position).getUrl() == null? "":newsList.getNewsDataList().get(position).getUrl(),
                                        newsList.getNewsDataList().get(position).getUrlToImage()== null ? "":newsList.getNewsDataList().get(position).getUrlToImage(),
                                        newsList.getNewsDataList().get(position).getPublishedAt()== null? "":newsList.getNewsDataList().get(position).getPublishedAt(),
                                        commonUtils.getBookMark(category));
                                try {
                                    //insert to room database
                                    insertNewsToDbLocal(news,mDb);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }
//                subscribeUi(adapter,commonUtils.getBookMark(category));
                dialogAction.hideDialog();

            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                dialogAction.hideDialog();
            }
        });
    }
    //insert to room database
    private void insertNewsToDbLocal(final News news, final NewsDatabase mDb) throws IOException {
        if(!news.getUrlToImage().equals("")){
            //insert image to internal storage and get the path and set it to news object
            String path = commonUtils.uploadImageToInternalStorage(news.getUrlToImage());
            news.setUrlToImage(path);
        }
        //format the date and time and set it to news object
        String dateTime = CommonUtils.getDate(news.getPublishedAt()).concat(", ").concat(CommonUtils.getTime(news.getPublishedAt()));
        news.setPublishedAt(dateTime);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mDb.newsDao().insert(news);
            }
        });
    }

    private void subscribeUi(final NewsAdapter adapter, int bookMark) {

        viewModel.getNewsListLiveData(bookMark).observe(this, new Observer<PagedList<News>>() {
            @Override
            public void onChanged(@Nullable PagedList<News> news) {
                recyclerView.removeAllViews();
                adapter.submitList(null);
                adapter.submitList(news);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
//                dialogAction.hideDialog();
            }
        });
//        dialogAction.hideDialog();
    }
}
