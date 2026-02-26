package com.floatingkeys;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.btnToggle);
        TextView tv = findViewById(R.id.tvStatus);

        btn.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
                Toast.makeText(this, "Grant permission then tap again", Toast.LENGTH_LONG).show();
                return;
            }
            if (FloatingKeyService.isRunning) {
                stopService(new Intent(this, FloatingKeyService.class));
                btn.setText("Show Floating Keys");
                tv.setText("Status: Hidden");
            } else {
                Intent s = new Intent(this, FloatingKeyService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
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
