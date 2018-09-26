package khudyakov.cryptoportfolio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    App.info.updateQuotations();
                    while (!App.info.ready()) {
                        sleep(1000);
                    }
                } catch (Exception e) {
                    Log.d("Kirill", e.getMessage());
                } finally {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };
        splashThread.start();
    }
}
