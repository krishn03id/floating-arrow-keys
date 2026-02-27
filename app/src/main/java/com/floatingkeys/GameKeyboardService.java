package com.floatingkeys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class GameKeyboardService extends InputMethodService {

    private BroadcastReceiver showKeyboardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Forces the keyboard to slide up when the floating button is tapped!
            requestShowSelf(0);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter("com.floatingkeys.SHOW_KEYBOARD");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(showKeyboardReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(showKeyboardReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(showKeyboardReceiver);
    }

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.keyboard_view, null);

        setupKey(view.findViewById(R.id.key_up), KeyEvent.KEYCODE_DPAD_UP);
        setupKey(view.findViewById(R.id.key_down), KeyEvent.KEYCODE_DPAD_DOWN);
        setupKey(view.findViewById(R.id.key_left), KeyEvent.KEYCODE_DPAD_LEFT);
        setupKey(view.findViewById(R.id.key_right), KeyEvent.KEYCODE_DPAD_RIGHT);
        setupKey(view.findViewById(R.id.key_space), KeyEvent.KEYCODE_SPACE);

        view.findViewById(R.id.btn_hide).setOnClickListener(v -> requestHideSelf(0));

        return view;
    }

    private void setupKey(View button, final int keycode) {
        button.setOnTouchListener((v, event) -> {
            InputConnection ic = getCurrentInputConnection();
            if (ic == null) return false;

            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
                v.setPressed(true);
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keycode));
                v.setPressed(false);
            }
            return true;
        });
    }
}
