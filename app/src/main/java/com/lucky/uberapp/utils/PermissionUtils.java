package com.lucky.uberapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

/**
 * Utils class to manage permission stuffs
 * Created by lucky on 3/17/17.
 */

public class PermissionUtils {

    public static final int PERMISSION_LOCATION = 1;
    private static final String TAG = "PermissionUtils";

    public static void checkPermissions(final Activity activity, final String[] permissions, final String message, final int requestCode) {
        Log.i(TAG, "checkPermissions called for : " + requestCode + " from activity : " + activity.getLocalClassName());
        for (final String permission :
                permissions) {
            // check if permission granted
            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                // check if we need to show explanation
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setMessage(message).setTitle("Required Action").setCancelable(true).setNegativeButton("dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                        }
                    }).show();

                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                }
            } else {
                // we already have permission
            }
        }
    }

}
