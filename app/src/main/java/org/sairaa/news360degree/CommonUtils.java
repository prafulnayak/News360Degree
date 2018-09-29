package org.sairaa.news360degree;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.sairaa.news360degree.service.BackgroundService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
    private Context context;

    public CommonUtils(Context context) {
        this.context = context;
    }

    public String uploadImageToInternalStorage(String urlToImage) {
        Bitmap bitmap = null;
        bitmap = getBitmapFromUrl(urlToImage);
        if(bitmap == null)
            return "";
        return saveImage(context,bitmap);
//        return null;
    }

    private String saveImage(Context context, Bitmap bitmap) {
        int uniqueInteger = (int) ((new Date().getTime()) % Integer.MAX_VALUE);
        String filename = String.valueOf(uniqueInteger)+".jpg";
        File file = new File(context.getFilesDir(), filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    private Bitmap getBitmapFromUrl(String urlToImage) {
        URLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = new URL(urlToImage).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                bitmap = BitmapFactory.decodeStream((InputStream)connection.getContent(), null, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String getDate(String dateString) {

        try{
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            Date date = format1.parse(dateString);
            DateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            return sdf.format(date);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return "xx";
        }
    }

    public static String getTime(String dateString) {

        try{
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            Date date = format1.parse(dateString);
            DateFormat sdf = new SimpleDateFormat("h:mm a");
            Date netDate = (date);
            return sdf.format(netDate);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return "xx";
        }
    }


    public static long getRandomNumber() {
        long x = (long) ((Math.random() * ((100000 - 0) + 1)) + 0);
        return x;
    }

    public static void showNotification(Context myService, String s){
        int uniqueInteger = 0;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(myService,s);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.news360);
            builder.setLargeIcon(BitmapFactory.decodeResource(myService.getResources(), R.drawable.news360));
            builder.setColor(myService.getResources().getColor(R.color.colorAccent));
        } else {
            builder.setSmallIcon(R.drawable.news360);
        }
        builder.setContentTitle(myService.getString(R.string.app_name));
        builder.setContentText(s);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        builder.setAutoCancel(true);
        Intent intent = new Intent(myService, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(myService);
        stackBuilder.addNextIntent(intent);
        uniqueInteger = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) myService.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueInteger, builder.build());

    }
}
