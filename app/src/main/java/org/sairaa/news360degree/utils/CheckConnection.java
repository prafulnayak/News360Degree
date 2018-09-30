package org.sairaa.news360degree.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckConnection {

    private Context mContext;
    public CheckConnection(Context context) {
        mContext = context;

    }

    /*
        Get Network Information
     */
    public NetworkInfo getNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    /*
        Check whether connected to network or not
     */

    public Boolean isConnected(){
        NetworkInfo networkInfo = getNetworkInfo();
        return networkInfo != null &&  networkInfo.isConnectedOrConnecting();
    }

}
