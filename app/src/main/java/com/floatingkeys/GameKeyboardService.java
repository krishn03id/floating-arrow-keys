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

        setupKey(view.findViewById(R.id.key_up), KeyEvent.KEYCODE_DPAD_UP);
        setupKey(view.findViewById(R.id.key_down), KeyEvent.KEYCODE_DPAD_DOWN);
        setupKey(view.findViewById(R.id.key_left), KeyEvent.KEYCODE_DPAD_LEFT);
        setupKey(view.findViewById(R.id.key_right), KeyEvent.KEYCODE_DPAD_RIGHT);
        setupKey(view.findViewById(R.id.key_space), KeyEvent.KEYCODE_SPACE);

        view.findViewById(R.id.btn_hide).setOnClickListener(v -> requestHideSelf(0));

        return view;
    }

    // Handles physical press and hold
    private void setupKey(View button, final int keycode) {
        button.setOnTouchListener((v, event) -> {
            InputConnection ic = getCurrentInputConnection();
            if (ic == null) return false;

            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                // Key pressed down physically
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
                v.setPressed(true);
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                // Key released physically
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keycode));
                v.setPressed(false);
            }
            return true;
        });
    }
}
