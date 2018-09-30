package org.sairaa.news360degree.service;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;


public class ServiceUtils {
    private static final int INTERVAL_MINUTES = 15;
    private static final int INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(INTERVAL_MINUTES));
    private static final int SYNC_FLEX_TIMEWINDOW = INTERVAL_SECONDS;
    private static final String RTAG = "reminder_tag";
    private static boolean sInitialized;
    synchronized public void scheduleTask(Context context) {
        if(sInitialized)
            return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job job = dispatcher.newJobBuilder()
                .setService(BackgroundService.class)
                .setTag(RTAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(INTERVAL_SECONDS,INTERVAL_SECONDS+SYNC_FLEX_TIMEWINDOW))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(job);
        sInitialized = true;
    }
}
