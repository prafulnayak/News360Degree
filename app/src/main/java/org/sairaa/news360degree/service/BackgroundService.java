package org.sairaa.news360degree.service;



import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobParameters;
public class BackgroundService extends JobService {

    @Override
    public boolean onStartJob(final JobParameters job) {
        BackgroundTask backgroundTask = new BackgroundTask(this) {
            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        backgroundTask.execute();
        return true;
    }


    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return true;
    }

    public static class BackgroundTask extends AsyncTask<Void, Void, Void>{

        BackgroundService myService;


        public BackgroundTask(BackgroundService myService) {
            this.myService = myService;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            BackGroundUtils utils = new BackGroundUtils(myService);
            utils.fatchLatestNews();
            return null;
        }

    }
}
