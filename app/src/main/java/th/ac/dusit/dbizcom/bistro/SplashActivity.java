package th.ac.dusit.dbizcom.bistro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import th.ac.dusit.dbizcom.bistro.etc.MyPrefs;
import th.ac.dusit.dbizcom.bistro.model.User;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                User user = MyPrefs.getUserPref(SplashActivity.this);
                Intent intent = user == null ?
                        (new Intent(SplashActivity.this, LoginActivity.class)) :
                        (new Intent(SplashActivity.this, MainActivity.class));
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
