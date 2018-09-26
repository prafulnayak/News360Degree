package org.sairaa.news360degree;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import org.sairaa.news360degree.db.News;
import org.sairaa.news360degree.db.NewsDatabase;

public class NewsViewModel extends AndroidViewModel {

    private LiveData<PagedList<News>> newsListLiveData;
    private Application application;
    public NewsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        DataSource.Factory<Integer,News> factory = NewsDatabase.getsInstance(application).newsDao().allNewsDetails();
        PagedList.Config pagConfig = new PagedList.Config.Builder().setPageSize(2).setEnablePlaceholders(true).build();
        LivePagedListBuilder<Integer, News> pagedListBuilder = new LivePagedListBuilder(factory,pagConfig);
        newsListLiveData = pagedListBuilder.build();
    }

    public LiveData<PagedList<News>> getNewsListLiveData() {
        return newsListLiveData;
    }
}
