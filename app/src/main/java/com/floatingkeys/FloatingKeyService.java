package com.floatingkeys;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import androidx.core.app.NotificationCompat;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private View floatingView;
    private static final String CHANNEL_ID = "FloatingKeyChannel";

    @Override
    public IBinder onBind(Intent intent) {
        return null; 
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        // 1. Create Notification Channel & Notification (REQUIRED for Android 14+)
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Floating Arrow Keys")
                .setContentText("Keys are active")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Make sure to use your own icon here later
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        // 2. Start Foreground Service IMMEDIATELY 
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(1, notification);
        }

        // 3. Set up the floating window using the SERVICE Context
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        // UNCOMMENT THIS ONCE YOU HAVE YOUR XML LAYOUT READY:
        // floatingView = LayoutInflater.from(this).inflate(R.layout.your_floating_layout, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        // UNCOMMENT THIS ONCE YOU HAVE YOUR XML LAYOUT READY:
        // windowManager.addView(floatingView, params);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Floating Keys Status",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up the view when the service is killed
        if (floatingView != null && windowManager != null) {
            windowManager.removeView(floatingView);
        }
    }
}
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
