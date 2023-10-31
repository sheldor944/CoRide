package com.example.myapplication.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.helper.Callback;
import com.example.myapplication.helper.PermissionCallback;

public class DialogUtil {
    private static final String TAG = "DialogUtil";
    public static void askForConfirmation(Context context,
                                          String query,
                                          PermissionCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(query)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "Yes," handle the action here
                        // For example, delete something or proceed with an action
                        Log.d(TAG, "onClick: clicked on yes");
                        callback.onPermit();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "No," handle the action here
                        // For example, do nothing or close the dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
