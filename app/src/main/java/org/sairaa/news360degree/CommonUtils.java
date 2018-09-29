package org.sairaa.news360degree;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
        return saveImage(context,bitmap);
//        return null;
    }

    private String saveImage(Context context, Bitmap bitmap) {
        int uniqueInteger = (int) ((new Date().getTime()) % Integer.MAX_VALUE);
        Log.e("Time","unique time"+String.valueOf(uniqueInteger));
        String filename = String.valueOf(uniqueInteger)+".jpg";
        File file = new File(context.getFilesDir(), filename);

        //Log.i(LOG_TAG,"Test : "+String.valueOf(mypath));
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
//        Log.i(LOG_TAG_BUTIL,"Test : "+String.valueOf(file.getAbsolutePath()));
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
}
