package com.ap.i;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;

import com.tencent.mmkv.MMKV;

import com.lo1.AdMzki;

import z.m;

/**
 * Date：2025/9/25
 * Describe:
 * com.ap.i.G9
 */
public class G9 {
    public static long insAppTime = 0L; //installAppTime
    private static final MMKV mmkv = MMKV.defaultMMKV();
    public static x.h e;
    public static Application mApp;

    // 入口 记得做差异化
    public static void a(float f) {
        e = (x.h) (new k.a().a(9));
        mApp = m.d;
        pE("test_d_load");
        inIf(mApp);
        AdMzki.a2();
        //admin url
    }

    public static void pE(String string, String value) {
        e.a(string, value);
    }

    public static void pE(String string) {
        pE(string, "");
    }

    public static void postAd(String string) {
        e.c(string);
    }


    public static String getStr(String key) {
        return mmkv.decodeString(key, "");
    }

    public static void saveC(String ke, String con) {
        mmkv.encode(ke, con);
    }

    public static int getInt(String key) {
        return mmkv.decodeInt(key, 0);
    }

    public static void saveInt(String key, int i) {
        mmkv.encode(key, i);
    }

    private static void inIf(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            insAppTime = pi.firstInstallTime;
        } catch (Exception ignored) {
        }
    }
}
