package org.sairaa.news360degree.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogAction {
    private Context mContext;
    ProgressDialog progressDialog;
    public DialogAction(Context context) {
        mContext = context;

    }
    public void showDialog(String title, String msg){
        progressDialog = ProgressDialog.show(mContext, title, msg, true, false);
    }
    public  void hideDialog(){

        if(progressDialog != null) {

            progressDialog.dismiss();
            progressDialog = null;
        }

    }
}
