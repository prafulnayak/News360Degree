package org.sairaa.news360degree;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.widget.Toast;

import org.sairaa.news360degree.db.News;
import org.sairaa.news360degree.db.NewsDatabase;

public class NewsViewModel extends AndroidViewModel {

    private LiveData<PagedList<News>> newsListLiveData;
    private Application application;
    public NewsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    //LiveData
    //bookmark is used to retrieve categories news from room

    public LiveData<PagedList<News>> getNewsListLiveData(int bookMark) {
        
        newsListLiveData = null;
        DataSource.Factory<Integer,News> factory = NewsDatabase.getsInstance(application).newsDao().allNewsDetailsCatogory(bookMark);
        //config the pagedList
        //setPageSize(10) retrieves 10 sets of news object in single instance
        PagedList.Config pagConfig = new PagedList.Config.Builder().setPageSize(10).setEnablePlaceholders(false).build();
        LivePagedListBuilder<Integer, News> pagedListBuilder = new LivePagedListBuilder(factory,pagConfig);
        newsListLiveData = pagedListBuilder.build();
        return newsListLiveData;
    }
}
