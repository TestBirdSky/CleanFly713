package c;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.raven.tabor.TaborHelper;
import com.raven.tabor.core.CenterHelper;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Date：2025/10/16
 * Describe:
 * c.a
 */
public class a {
    public static LinkedList<String> f = new LinkedList<>();
    public static Application d;

    public static ArrayList<Activity> a = new ArrayList<>();

    public static boolean b() {
        return TaborHelper.INSTANCE.getMEventHelper().finish();
    }

    public static void c(Context c, String s) {
        CenterHelper.INSTANCE.checkFcm(c, s);
    }
}
