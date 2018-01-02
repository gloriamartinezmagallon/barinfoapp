package barinfo.navdev.barinfo.Utils;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import barinfo.navdev.barinfo.R;

public class AlertUtils {

    public interface OnErrorDialog{
        void run();
    }

    public static AlertDialog errorDialog(Context context, String message, final OnErrorDialog onErrorDialog){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.error);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onErrorDialog != null){
                    onErrorDialog.run();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }

    public interface OnYesNoDialog{
        void onYes();
        void onNo();
    }

    public static AlertDialog yesNoDialog(Context context, String title, String message, String yes, String no, final OnYesNoDialog onYesNoDialog){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onYesNoDialog != null){
                    onYesNoDialog.onYes();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onYesNoDialog != null){
                    onYesNoDialog.onNo();
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }

    public static ProgressDialog progressDialog(Context context, String message){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        return dialog;
    }
}
