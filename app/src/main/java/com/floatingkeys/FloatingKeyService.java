package com.floatingkeys;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.core.app.NotificationCompat;

public class FloatingKeyService extends Service {

    public static boolean isRunning = false;
    private static final String CH = "fk";
    private WindowManager wm;
    private View root;
    private WindowManager.LayoutParams lp;
    private int ix, iy;
    private float itx, ity;

    @Override public IBinder onBind(Intent i) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel c = new NotificationChannel(CH, "Floating Keys",
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }

        Intent ni = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, ni, PendingIntent.FLAG_IMMUTABLE);
        Notification n = new NotificationCompat.Builder(this, CH)
                .setContentTitle("Floating Keys Active")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentIntent(pi).setOngoing(true).build();
        startForeground(1, n);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        root = LayoutInflater.from(this).inflate(R.layout.floating_keys, null);

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.y = 80;

        wm.addView(root, lp);
        bindButtons();
        bindDrag();
    }

    private void bindButtons() {
        int[][] map = {
            {R.id.btnUp,    KeyEvent.KEYCODE_DPAD_UP},
            {R.id.btnDown,  KeyEvent.KEYCODE_DPAD_DOWN},
            {R.id.btnLeft,  KeyEvent.KEYCODE_DPAD_LEFT},
            {R.id.btnRight, KeyEvent.KEYCODE_DPAD_RIGHT},
            {R.id.btnSpace, KeyEvent.KEYCODE_SPACE},
        };
        for (int[] entry : map) {
            View v = root.findViewById(entry[0]);
            int kc = entry[1];
            v.setOnTouchListener((vv, e) -> {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    vv.setPressed(true);
                    sendKey(kc, KeyEvent.ACTION_DOWN);
                    return true;
                }
                if (e.getAction() == MotionEvent.ACTION_UP
                        || e.getAction() == MotionEvent.ACTION_CANCEL) {
                    vv.setPressed(false);
                    sendKey(kc, KeyEvent.ACTION_UP);
                    return true;
                }
                return false;
            });
        }
    }

    private void sendKey(int code, int action) {
        long t = SystemClock.uptimeMillis();
        root.getRootView().dispatchKeyEvent(
            new KeyEvent(t, t, action, code, 0, 0,
                KeyEvent.KEYCODE_UNKNOWN, 0,
                KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_SOFT_KEYBOARD, 0x101));
    }

    private void bindDrag() {
        LinearLayout handle = root.findViewById(R.id.dragHandle);
        handle.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ix = lp.x; iy = lp.y;
                    itx = e.getRawX(); ity = e.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    lp.x = ix + (int)(e.getRawX() - itx);
                    lp.y = iy - (int)(e.getRawY() - ity);
                    wm.updateViewLayout(root, lp);
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (root != null && wm != null) wm.removeView(root);
    }
}
