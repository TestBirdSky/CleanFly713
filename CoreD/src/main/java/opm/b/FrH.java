package opm.b;


import android.os.Handler;
import android.os.Message;

import opm.z.cd;

/**
 * Date：2025/7/28
 * Describe:
 */
public class FrH extends Handler {
    @Override
    public void handleMessage(Message message) {
        cd.c(message.what);
    }
}
