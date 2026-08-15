package com.sumon.bundleapp.installer.ui.activities;

import com.sumon.bundleapp.installer.R;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EulaActivity extends ThemedActivity {

    private static final String EULA_ASSET_PATH = "EULA/EULA.md";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eula);

        TextView textView = findViewById(R.id.tv_eula_text);
        textView.setText(readEulaAsset());
    }

    private String readEulaAsset() {
        AssetManager assetManager = getAssets();
        StringBuilder sb = new StringBuilder();

        try (InputStream inputStream = assetManager.open(EULA_ASSET_PATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            return getString(R.string.eula_load_error);
        }

        return sb.toString();
    }
}
