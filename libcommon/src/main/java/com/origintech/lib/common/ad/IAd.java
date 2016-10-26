package com.origintech.lib.common.ad;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by shuaibincheng on 16/5/15.
 */
public interface IAd {
    //将广告添加到parent中
    void stopAd();
    void loadAd(LinearLayout parent);
    void setContext(Context ctx);
    void after(Date time);  //投放广告时间限制
    void refreshInterval(int seconds);
    static class AdProvider {
        static public IAd getAddProvider(Context context){

            PackageManager pm = context.getPackageManager();
            if(pm == null)
                return null;
            try{
                ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA);
                if(ai == null || ai.metaData == null)
                    return null;
                //获取ad provider
                String adProvider = ai.metaData.getString("adProvider");
                //反射获取对应的ad provider
                Class clz = Class.forName(adProvider);
                Object obj = clz.newInstance();
                //获取投放时间限制
                String afterTimeStr = ai.metaData.getString("afterTime");
                SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date afterTime = null;
                try{
                    if(afterTimeStr != null)
                        afterTime = smf.parse(afterTimeStr);
                }catch (ParseException e){
                    afterTime = null;
                }
                //获取广告刷新周期
                int refreshInterval = ai.metaData.getInt("refreshInterval");

                if(obj instanceof IAd) {
                    ((IAd) obj).setContext(context);
                    if(afterTime != null){
                        ((IAd)obj).after(afterTime);
                    }
                    ((IAd)obj).refreshInterval(refreshInterval);
                    return (IAd) obj;
                } else {
                    return null;
                }
            }catch (PackageManager.NameNotFoundException
                    | ClassNotFoundException
                    | IllegalAccessException
                    | InstantiationException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
