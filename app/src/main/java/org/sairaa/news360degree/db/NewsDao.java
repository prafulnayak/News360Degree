package org.sairaa.news360degree.db;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NewsDao {
    @Query("SELECT * FROM News ORDER BY id DESC")
    DataSource.Factory<Integer, News> allNewsDetails();

    @Query("SELECT * FROM News WHERE title = :titleDesc")
    List<News> getSingleNews(String titleDesc);

    @Insert
    void insert(List<News> news);

    @Insert
    void insert(News news);

    @Delete
    void delete(News news);

}
