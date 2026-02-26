package com.floatingkeys;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQ_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assuming you have a basic layout
        // setContentView(R.layout.activity_main); 

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            // Ask for permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            // DO NOTHING HERE! Starting a service right now will crash Android 16.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the service safely when the app is fully visible again
        if (Settings.canDrawOverlays(this)) {
            startFloatingService();
        } else {
            Toast.makeText(this, "Please grant permission for the app to work.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startFloatingService() {
        Intent intent = new Intent(this, FloatingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
}
                    startForegroundService(s);
                else startService(s);
                btn.setText("Hide Floating Keys");
                tv.setText("Status: Visible");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button btn = findViewById(R.id.btnToggle);
        TextView tv = findViewById(R.id.tvStatus);
        if (FloatingKeyService.isRunning) {
            btn.setText("Hide Floating Keys");
            tv.setText("Status: Visible");
        } else {
            btn.setText("Show Floating Keys");
            tv.setText("Status: Hidden");
        }
    }
}
