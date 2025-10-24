package com.caz;


import android.os.Handler;
import android.os.Message;

/**
 * Date：2025/7/28
 * Describe:
 */
public class Styls extends Handler {
    @Override
    public void handleMessage(Message message) {
        Ac.c(message.what);
    }
}
