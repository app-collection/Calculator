package com.origintech.lib.common.ad;

import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;

import org.json.JSONObject;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by shuaibincheng on 16/5/15.
 */
public class BaiduAds implements IAd {
    private Context context = null;
    private Date afterTime = null;
    private int adInterval = 0;
    private String placementId = "";

    private Handler handler = new Handler();
    private Runnable current = null;

    @Override
    public void stopAd() {
        if(current != null){
            handler.removeCallbacks(current);
        }
    }
    public  int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    @Override
    public void loadAd(LinearLayout parent) {
        this.addBaiduBanner(parent);
    }

    @Override
    public void setContext(Context ctx) {
        this.context = ctx;
        AdSettings.setSupportHttps(true);
    }

    @Override
    public void after(Date time) {
        this.afterTime = time;
    }

    @Override
    public void refreshInterval(int seconds) {
        this.adInterval = seconds;
    }

    @Override
    public void setPlacementId(String id) {
        this.placementId = id;
    }

    private AdView makeBannerView(){
        AdView adView = new AdView(context,"5395024");
        adView.setListener(new AdViewListener() {
            @Override
            public void onAdReady(AdView adView) {

            }

            @Override
            public void onAdShow(JSONObject jsonObject) {
            }

            @Override
            public void onAdClick(JSONObject jsonObject) {
            }

            @Override
            public void onAdFailed(String s) {
                System.out.println("load ad failed");
            }

            @Override
            public void onAdSwitch() {

            }

            @Override
            public void onAdClose(JSONObject jsonObject) {

            }
        });
        return adView;
    }
    //百度banner广告
    //百度Banner广告
    private void addBaiduBanner(final ViewGroup container){
        if(afterTime != null){
            Date now = new Date();
            if(!now.after(afterTime))
                return;
        }
        container.removeAllViews();
        if(adInterval == 0) {
            container.addView(makeBannerView());
        }else{
            current = new Runnable(){
                @Override
                public void run() {
                    System.out.println("添加新的广告");
                    container.removeAllViews();
                    container.addView(makeBannerView());
                    handler.postDelayed(this, adInterval * 1000);
                }
            };
            handler.post(current);
        }
    }
}
