package com.sumon.bundleapp.installer.ui.activities;

import com.sumon.bundleapp.installer.R;

import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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
        textView.setText(renderMarkdownAsHtml(readEulaAsset()));
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

    private Spanned renderMarkdownAsHtml(String markdown) {
        // Convert basic markdown to HTML for rendering
        String html = markdown
                .replaceAll("(?m)^## (.+)$", "<h3>$1</h3>")
                .replaceAll("(?m)^# (.+)$", "<h2>$1</h2>")
                .replaceAll("(?m)^(.+)$", "<p>$1</p>")
                .replaceAll("<p></p>", "<br/>")
                .replaceAll("<p><h", "<h")
                .replaceAll("h2></p>", "h2>")
                .replaceAll("h3></p>", "h3>")
                .replaceAll("<p><br/>", "<br/>");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(html);
        }
    }
}
