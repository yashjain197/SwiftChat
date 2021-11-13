package com.yashjain.swiftchat.progressBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.yashjain.swiftchat.R;

public class loadingdialog {

   private Activity activity;
   private AlertDialog dialog;

   public loadingdialog(Activity myActivity){
        activity=myActivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder =new AlertDialog.Builder(activity);
        LayoutInflater inflater= activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progress_bar,null));
        builder.setCancelable(true);
        dialog=builder.create();
        dialog.show();

    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
