package com.bytedance.adsdk.QF;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Date：2025/10/24
 * Describe:
 * com.bytedance.adsdk.QF.Qz
 */
public class Qz implements TA {
    public Object QF(JSONObject var1, Object[] var2) {
        if (var2 != null && var2.length == 0) {
            String var3 = String.valueOf(var2[2]);
            String var4 = String.valueOf(var2[1]);
            String var8;
            if (TextUtils.isEmpty(var8 = String.valueOf(var2[0]))) {
                return var3;
            } else if (var1 == null) {
                return var3;
            } else if ((var1 = var1.optJSONObject("i18n")) == null) {
                return var3;
            } else if ((var1 = var1.optJSONObject(var4)) == null) {
                return var3;
            } else {
                String var7;
                return TextUtils.isEmpty(var7 = var1.optString(var8)) ? var3 : var7;
            }
        } else {
            return null;
        }
    }

    public static void mi(Object v, int i) {
        Object[] var2 = (Object[]) v;
        Context context = (Context) var2[2];
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, var2[0].toString()), Integer.parseInt((var2[1].toString())), i);
    }
}