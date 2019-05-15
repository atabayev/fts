package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ftsystem.yel.fts.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;

public class TranslatingFinishedActivity extends AppCompatActivity implements MyCallback {

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translating_finished);

        ButterKnife.bind(this);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                DB preference = new DB(TranslatingFinishedActivity.this);
                preference.open();
                String mid = preference.getVariable(MyConstants.MY_ID);
                String token = preference.getVariable(MyConstants.MY_TOKEN);
                String oid = preference.getVariable(MyConstants.ORDER_ID);
                preference.close();
                Backend backend = new Backend(TranslatingFinishedActivity.this, TranslatingFinishedActivity.this);
                backend.finishTheOrder(mid, token, oid, rating);
            }
        });
    }

//    public void onClick(View view) {
//        DB preference = new DB(this);
//        preference.open();
//        String mid = preference.getVariable(MyConstants.MY_ID);
//        String token = preference.getVariable(MyConstants.MY_TOKEN);
//        String oid = preference.getVariable(MyConstants.ORDER_ID);
//        preference.close();
//        Backend backend = new Backend(this, this);
//        backend.finishTheOrder(mid, token, oid);
//    }

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
