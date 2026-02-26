package com.floatingkeys;

import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class GameKeyboardService extends InputMethodService {

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.keyboard_view, null);

        // Bind the physical keycodes to our virtual buttons
        setupKey(view.findViewById(R.id.key_up), KeyEvent.KEYCODE_DPAD_UP);
        setupKey(view.findViewById(R.id.key_down), KeyEvent.KEYCODE_DPAD_DOWN);
        setupKey(view.findViewById(R.id.key_left), KeyEvent.KEYCODE_DPAD_LEFT);
        setupKey(view.findViewById(R.id.key_right), KeyEvent.KEYCODE_DPAD_RIGHT);
        setupKey(view.findViewById(R.id.key_space), KeyEvent.KEYCODE_SPACE);

        // Allow closing the keyboard so it doesn't block the screen forever
        view.findViewById(R.id.btn_hide).setOnClickListener(v -> requestHideSelf(0));

        return view;
    }

    // Handles TOUCH to support HOLDING DOWN buttons for gaming
    private void setupKey(View button, int keycode) {
        button.setOnTouchListener((v, event) -> {
            InputConnection ic = getCurrentInputConnection();
            if (ic == null) return false;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Button pressed down - send press event
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
                v.setPressed(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // Button released - send release event
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keycode));
                v.setPressed(false);
            }
            return true;
        });
    }
}
