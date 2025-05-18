package com.example.airplanemodeipviewer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView ipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button settingsBtn = findViewById(R.id.openSettingsBtn);
        ipView = findViewById(R.id.ipTextView);
        Button refreshBtn = findViewById(R.id.refreshBtn);

        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            startActivity(intent);
        });

        refreshBtn.setOnClickListener(v -> new FetchIPTask().execute());

        // Initial fetch
        new FetchIPTask().execute();
    }

    private class FetchIPTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://api.ipify.org?format=json");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                InputStream is = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();
                JSONObject json = new JSONObject(sb.toString());
                return json.getString("ip");
            } catch (Exception e) {
                e.printStackTrace();
                return "IP not found";
            }
        }

        @Override
        protected void onPostExecute(String ip) {
            ipView.setText("Your public IP: " + ip);
        }
    }
}
