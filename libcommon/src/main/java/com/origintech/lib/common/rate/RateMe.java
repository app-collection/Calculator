package com.origintech.lib.common.rate;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import com.origintech.lib.common.R;

/**
 * Created by shuaibincheng on 16/5/15.
 */
public class RateMe {
    static public void rate(Context ctx) {
        String packageName = ctx.getPackageName();
        try {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("market://details?id=" + packageName)));
        }catch (ActivityNotFoundException e){
            Toast.makeText(ctx, ctx.getString(R.string.libcommon_no_app_store_found)
                    , Toast.LENGTH_SHORT).show();
        }
    }

    static private boolean checkNeedToRemind(Context ctx){
        SharedPreferences sp = ctx.getSharedPreferences("libcommon", Context.MODE_PRIVATE);
        boolean rated = sp.getBoolean("rated", false);
        int startedFromLastRemind = sp.getInt("last_remind_to_rate_time", -1);
        if(rated)
            return false;
        if(startedFromLastRemind == -1){
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("last_remind_to_rate_time", 3);  //启动下一次提醒
            editor.commit();
            return false;
        } else if(startedFromLastRemind > 0){
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("last_remind_to_rate_time", --startedFromLastRemind);  //启动下一次提醒
            editor.commit();
            return false;
        }
        return true;
    }

    static private void showRemindDialog(final Context ctx){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                .setMessage(R.string.libcommon_rate_me)
                .setPositiveButton(R.string.libcommon_rate_ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sp = ctx.getSharedPreferences("libcommon",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("rated", true);  //用户已评论过了
                        editor.commit();
                        dialog.dismiss();
                        rate(ctx);
                    }
                })
                .setNegativeButton(R.string.libcommon_rate_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sp = ctx.getSharedPreferences("libcommon",
                                Context.MODE_PRIVATE);
                        int startedFromLastRemind = sp.getInt("last_remind_to_rate_time", -1);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("last_remind_to_rate_time", 10);  //启动下一次提醒
                        editor.commit();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }
    static public void remindToRate(Context ctx){
        if(!checkNeedToRemind(ctx))
            return;
        //需要提醒用评论应用
        showRemindDialog(ctx);
    }
}
