package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ftsystem.yel.fts.R;

import java.util.HashMap;

import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;

public class SplashScreenActivity extends AppCompatActivity implements MyCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        Intent intent = new Intent(this, RegistrationActivity.class);
//        startActivity(intent);

        DB preferences = new DB(this);
        preferences.open();
        String phoneNumber = preferences.getVariable(MyConstants.MY_PHONE_NUM);
        if (!phoneNumber.equals("0")) {
            switch (preferences.getVariable(MyConstants.AUTO_ENTER)) {
                case "1":
                    Backend backend = new Backend(this, this);
                    backend.authentication(phoneNumber);
                    break;
                case "0":
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    startActivity(intent);
                    break;
            }
        } else {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
        }
        preferences.close();
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        switch (data.get("response")) {
            case "access":
                DB preferences = new DB(this);
                preferences.open();
                preferences.setVariable(MyConstants.MY_ID, data.get("id"));
                preferences.setVariable(MyConstants.MY_TOKEN, data.get("token"));
                Intent intent = new Intent(this, MainActivity.class);
                switch (data.get("status")) {
                    case "0":
                        intent = new Intent(this, MainActivity.class);
                        break;
                    case "1":
                        intent = new Intent(this, OrderAcceptedActivity.class);
                        break;
                    case "2":
                        intent = new Intent(this, OrderResultActivity.class);
                        break;
                    case "3":
                        intent = new Intent(this, WaitingTranslatorActivity.class);
                        break;
                    case "4":
                        intent = new Intent(this, WaitingOrderActivity.class);
                        break;
                    case "6":
                        intent = new Intent(this, TranslatingFinishedActivity.class);
                        break;
                }
                startActivity(intent);
                finish();
                break;
            case "denied":
                Intent intentA = new Intent(this, AuthenticationActivity.class);
                startActivity(intentA);
                finish();
                break;
            case "error":
                Intent intentB = new Intent(this, AuthenticationActivity.class);
                startActivity(intentB);
                Toast.makeText(this, R.string.error_server_not_available, Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
