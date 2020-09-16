package com.firmino.rigkontrol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;

public class SplashScreen extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private TextView mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_splashscreen);
        findViewById(R.id.Splash_Main).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mStatus = findViewById(R.id.Splash_Status);
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mStatus.setText(R.string.premission_granted);
                checkFiles();
            } else {
                mStatus.setText(R.string.erro_permission);
                new Handler().postDelayed(this::finish, 5000);
            }
        }
    }

    private void checkPermissions() {
        ((TextView) findViewById(R.id.Splash_Version)).setText(String.format("Version: %s", BuildConfig.VERSION_NAME));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else checkFiles();
    }

    private void checkFiles() {
        mStatus.setText(R.string.check_dirs);
        File dirPresets = new File(Environment.getExternalStorageDirectory() + File.separator + "Rig Kontrol" + File.separator + "Presets");
        File dirRacks = new File(Environment.getExternalStorageDirectory() + File.separator + "Rig Kontrol" + File.separator + "Racks");
        if (!(dirPresets.exists() && dirRacks.exists())) {
            if (dirPresets.mkdirs() && dirRacks.mkdirs()) allDone();
            else {
                mStatus.setText(R.string.cant_make_preset_directory);
                new Handler().postDelayed(this::finish, 5000);
            }
        } else allDone();
    }

    private void allDone() {
        mStatus.setText(R.string.loading_sucessfully);
        startActivity(new Intent(SplashScreen.this, MainActivity.class));
        finish();
    }

}