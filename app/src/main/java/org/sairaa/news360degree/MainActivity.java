package org.sairaa.news360degree;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import org.sairaa.news360degree.utils.CheckConnection;
import org.sairaa.news360degree.utils.CommonUtils;
import org.sairaa.news360degree.utils.DialogAction;

import java.io.IOException;
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
    private CheckConnection checkConnection;
    static DialogAction dialogAction;
    CommonUtils commonUtils;
    FloatingActionButton floatingActionButton;
    private DrawerLayout mDrawerLayout;
    private static final String APIKEY = "c19366b11c0440848041a33b1745e3d1";//"079dac74a5f94ebdb990ecf61c8854b7";

    @Override
    protected void onStop() {
        super.onStop();
        ServiceUtils serviceUtils = new ServiceUtils();
        serviceUtils.scheduleTask(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        NavigationView navigationView = findViewById(R.id.nav_view);

        checkConnection = new CheckConnection(this);
        floatingActionButton = findViewById(R.id.floatingActionButton2);
        dialogAction = new DialogAction(this);
        commonUtils = new CommonUtils(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        //viewmodel
        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);

        adapter = new NewsAdapter(this);
        //if network is connected retrieve top headline news and insert it to room
        if(checkConnection.isConnected()){
            insertNewsToDb(Executors.newSingleThreadExecutor(),"");
        }else
            Toast.makeText(this,getString(R.string.network),Toast.LENGTH_LONG).show();
        //retrieve news from Room for Top headline
        //here bookmark "1" means Top headline category;
        subscribeUi(adapter,1);
        //navigation drawer
        //start an intent to get category news details
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                switch (menuItem.getItemId()){
                    case R.id.nav_business:
                        intent.putExtra("category",getString(R.string.business_cat));
                        startActivity(intent);
                        break;
                    case  R.id.nav_entertainment:
                        intent.putExtra("category",getString(R.string.entertainment_cat));
                        startActivity(intent);
                        break;
                    case R.id.nav_health:
                        intent.putExtra("category",getString(R.string.health_cat));
                        startActivity(intent);
                        break;
                    case R.id.nav_science:
                        intent.putExtra("category",getString(R.string.science_cat));
                        startActivity(intent);
                        break;
                    case R.id.nav_sports:
                        intent.putExtra("category",getString(R.string.sports_cat));
                        startActivity(intent);
                        break;
                    case R.id.nav_technology:
                        intent.putExtra("category",getString(R.string.technology_cat));
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
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
    //set UI
    private void subscribeUi(final NewsAdapter adapter, int bookMark) {
        //observe if the data gets changed and notify the UI
        viewModel.getNewsListLiveData(bookMark).observe(this, new Observer<PagedList<News>>() {
            @Override
            public void onChanged(@Nullable PagedList<News> news) {
                //cleare the adapter
                adapter.submitList(null);
                //submit news list to adapter
                adapter.submitList(news);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //move to top of the recycler view
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
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            // Respond to a click on the "Insert data" menu option
            case R.id.action_insert_data:
                if(checkConnection.isConnected()){
                    insertNewsToDb(Executors.newSingleThreadExecutor(),"");
                }else
                    Toast.makeText(this,getString(R.string.network),Toast.LENGTH_LONG).show();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    //Retrieve top headline news and insert it into room and notifies user on new news arrival
    private void insertNewsToDb(final Executor executor, final String category) {
        //get local country name
        String countryName = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();
        //get the country code for retrival of news in respective country
        String countryCode = commonUtils.getCountryCode(countryName);
        final NewsDatabase mDb = NewsDatabase.getsInstance(this);
        NewsApi newsApi = ApiUtils.getNewsApi();
        dialogAction.showDialog(getString(R.string.app_name),getString(R.string.retrieve));
        //make retrofit call to retrieve category news of the respective country
        newsApi.getTopHeadLine(countryCode,APIKEY).enqueue(new Callback<NewsList>() {
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
                                // create news object to insert it into room
                                News news = new News(newsList.getNewsDataList().get(position).getAuthor() == null ? getString(R.string.newsApi) :newsList.getNewsDataList().get(position).getAuthor(),
                                        newsList.getNewsDataList().get(position).getTitle() == null ? "" : newsList.getNewsDataList().get(position).getTitle(),
                                        newsList.getNewsDataList().get(position).getDescription() == null? "" :newsList.getNewsDataList().get(position).getDescription(),
                                        newsList.getNewsDataList().get(position).getUrl() == null? "":newsList.getNewsDataList().get(position).getUrl(),
                                        newsList.getNewsDataList().get(position).getUrlToImage()== null ? "":newsList.getNewsDataList().get(position).getUrlToImage(),
                                        newsList.getNewsDataList().get(position).getPublishedAt()== null? "":newsList.getNewsDataList().get(position).getPublishedAt(),
                                        commonUtils.getBookMark(category));
                                try {
                                    //insert news object to room
                                    insertNewsToDbLocal(news,mDb);
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
    }

    private void insertNewsToDbLocal(final News news, final NewsDatabase mDb) throws IOException {
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
            }
        });
    }
}
