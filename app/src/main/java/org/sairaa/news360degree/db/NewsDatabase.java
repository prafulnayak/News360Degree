package org.sairaa.news360degree.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {News.class}, version = 2,exportSchema = false)
public abstract class NewsDatabase extends RoomDatabase {

    private static final String LOG_TAG = NewsDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "newsorg.db";
    private static NewsDatabase sInstance;

    public static NewsDatabase getsInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG,"Creating new Database Instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        NewsDatabase.class,NewsDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG,"getting the Database Instance");
        return sInstance;
    }

    public abstract NewsDao newsDao();

}
