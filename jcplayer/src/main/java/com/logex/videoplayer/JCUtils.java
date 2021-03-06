package com.logex.videoplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.logex.utils.LogUtil;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by liguangxi
 * On 2016/02/21 12:25
 */
public class JCUtils {

    /**
     * 将秒转换为视频时长显示
     *
     * @param timeMs 秒
     * @return 视频时长
     */
    public static String stringForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 判断是否连接wifi
     *
     * @param context context
     * @return true wifi已连接 false未连接
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }

    public static void saveProgress(Context context, String url, long progress) {
        LogUtil.i("保存视频进度>>>" + url);
        if (progress < 5000) {
            progress = 0;
        }
        SharedPreferences spn = context.getSharedPreferences("JCVD_PROGRESS",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spn.edit();
        editor.putLong(context.hashCode() + ":" + url, progress).apply();
    }

    public static long getSavedProgress(Context context, String url) {
        LogUtil.i("获取视频进度>>>" + url);
        SharedPreferences spn = context.getSharedPreferences("JCVD_PROGRESS",
                Context.MODE_PRIVATE);
        return spn.getLong(context.hashCode() + ":" + url, 0);
    }
}
