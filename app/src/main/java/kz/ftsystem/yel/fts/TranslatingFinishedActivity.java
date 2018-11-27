package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ftsystem.yel.fts.R;

import java.util.HashMap;

import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.Backend;
import kz.ftsystem.yel.fts.backend.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;

public class TranslatingFinishedActivity extends AppCompatActivity implements MyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translating_finished);
    }

    public void onClick(View view) {
        DB preference = new DB(this);
        preference.open();
        String mid = preference.getVariable(MyConstants.MY_ID);
        String token = preference.getVariable(MyConstants.MY_TOKEN);
        String oid = preference.getVariable(MyConstants.ORDER_ID);
        preference.close();
        Backend backend = new Backend(this, this);
        backend.finishTheOrder(mid, token, oid);
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        switch (data.get("response")) {
            case "error_f":
                Toast.makeText(this, R.string.error_send, Toast.LENGTH_SHORT).show();
                break;
            case "denied":
                Toast.makeText(this, R.string.ora_access_denied, Toast.LENGTH_SHORT).show();
                break;
            case "error_no_order":
                Toast.makeText(this, R.string.ora_error_order_id, Toast.LENGTH_SHORT).show();
            case "ok":
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
