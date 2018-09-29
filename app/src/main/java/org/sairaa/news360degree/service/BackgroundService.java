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
            protected void onPostExecute(String s) {
//                super.onPostExecute(s);


                Toast.makeText(getApplicationContext(), "back" + s, Toast.LENGTH_LONG).show();
                jobFinished(job, false);
            }
        };
        backgroundTask.execute();
        return true;
    }


    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
//         if(asyncTask != null)
//            asyncTask.cancel(true);
        return true;
    }

    public static class BackgroundTask extends AsyncTask<Context, Void, String>{

        BackgroundService myService;


        public BackgroundTask(BackgroundService myService) {
            this.myService = myService;
        }

        @Override
        protected String doInBackground(Context... contexts) {
            BackGroundUtils utils = new BackGroundUtils(myService);
            utils.fatchLatestNews();
            return "hello back fro";
        }
    }
}
