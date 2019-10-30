package kz.ftsystem.yel.fts;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ftsystem.yel.fts.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

//        отправляет асинхронный запрос на ICanHazIp, чтоб узнать свой ip, необходимо передать IP для эквайринга
        new GetIp(this).execute();

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
            finish();
        }
        preferences.close();
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        switch (data.get("response")) {
            case "access":
                DB preferences = new DB(this);
                preferences.open();
                preferences.setVariable(MyConstants.MY_ID, data.get("cid"));
                preferences.setVariable(MyConstants.MY_TOKEN, data.get("token"));
                preferences.setVariable(MyConstants.ORDER_ID, data.get("oid"));
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
                    case "25":
                        intent = new Intent(this, WaitPayActivity.class);
                        break;
                    case "26":
                        intent = new Intent(this, WaitCallActivity.class);
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

class GetIp extends AsyncTask<Void, Void, String> {

    Context context;

    public GetIp(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String res = "";
        try {
            URL url = new URL("http://icanhazip.com/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                res = in.readLine();
                in.close();
                //Если запрос выполнен удачно, читаем полученные данные и далее, делаем что-то
            } else {
                //Если запрос выполнен не удачно, делаем что-то другое
            }
        } catch (Exception e) {
            Log.d(MyConstants.TAG, e.getMessage());
        }
        return res;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        DB preferences = new DB(context);
        preferences.open();
        preferences.setVariable(MyConstants.MY_IP_ADDR, s);
        preferences.close();
    }
}
