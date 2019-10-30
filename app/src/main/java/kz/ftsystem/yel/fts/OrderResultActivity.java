package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ftsystem.yel.fts.R;


import java.util.HashMap;

import butterknife.ButterKnife;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;

public class OrderResultActivity extends AppCompatActivity implements MyCallback {

    TextView tvId;
    TextView tvLanguage;
    TextView tvPagesCount;
    TextView tvPrice;
    TextView tvDateEnd;
    TextView tvUrgency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_order_result);

        tvId = findViewById(R.id.oraIdText);
        tvLanguage = findViewById(R.id.oraLangText);
        tvPagesCount = findViewById(R.id.oraPagesCountText);
        tvPrice = findViewById(R.id.oraPriceText);
        tvDateEnd = findViewById(R.id.oraDateEndText);
        tvUrgency = findViewById(R.id.oraUrgencyText);


        getOrderParams();

//        if (getIntent().hasExtra("orderId")) {
//            tvId.setText(getIntent().getStringExtra("orderId"));
//            tvLanguage.setText(getIntent().getStringExtra("language"));
//            tvPagesCount.setText(getIntent().getStringExtra("pagesCount"));
//            tvPrice.setText(getIntent().getStringExtra("price"));
//            tvDateEnd.setText(getIntent().getStringExtra("dateEnd"));
//            tvUrgency.setText(getIntent().getStringExtra("urgency"));
//        } else {
//            DB preferences = new DB(this);
//            preferences.open();
//            String myId = preferences.getVariable(MyConstants.MY_ID);
//            String myToken = preferences.getVariable(MyConstants.MY_TOKEN);
//            String orderId = preferences.getVariable(MyConstants.ORDER_ID);
//            Backend backend = new Backend(this, this);
//            backend.getInfoAboutMyOrder(myId, myToken, orderId);
//            preferences.close();
//        }
    }

    private void getOrderParams() {
        DB preferences = new DB(this);
        preferences.open();
        String myId = preferences.getVariable(MyConstants.MY_ID);
        String myToken = preferences.getVariable(MyConstants.MY_TOKEN);
        String orderId = preferences.getVariable(MyConstants.ORDER_ID);
        preferences.close();
        Backend backend = new Backend(this, this);
        backend.getInfoAboutMyOrder(myId, myToken, orderId);
    }


    public void onClick(View view) {
        DB preferences = new DB(this);
        preferences.open();
        String cid = preferences.getVariable(MyConstants.MY_ID);
        String token = preferences.getVariable(MyConstants.MY_TOKEN);
        String oid = preferences.getVariable(MyConstants.ORDER_ID);
        preferences.close();
        switch (view.getId()) {
            case R.id.oraBtnAccept:
                preferences.open();
                preferences.setVariable(MyConstants.MY_ORDER_AMOUNT, tvPrice.getText().toString());
                preferences.close();
//                Intent intent = new Intent(this, CheckoutActivity.class);
                Intent intent = new Intent(this, SelectPayMethodActivity.class);
//                intent.putExtra("amount", tvPrice.getText().toString());
                startActivity(intent);
                finish();
                break;
            case R.id.oraBtnCancel:
                Backend backend = new Backend(this, this);
                backend.cancelOrder(cid, token, oid);
                break;
        }
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        switch (data.get("response")) {
            case "ready":
                tvId.setText(data.get("orderId"));
                tvLanguage.setText(data.get("language"));
                tvPagesCount.setText(data.get("pagesCount"));
                tvPrice.setText(data.get("price"));
                tvDateEnd.setText(data.get("dateEnd"));
                tvUrgency.setText(data.get("urgency"));
                break;
            case "cancel_ok":
                Toast.makeText(this, R.string.ora_order_canceled, Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;
            case "denied":
                Toast.makeText(this, R.string.ora_access_denied, Toast.LENGTH_SHORT).show();
                break;
            case "":
                Toast.makeText(this, R.string.ora_error_order_id, Toast.LENGTH_SHORT).show();
                break;
            case "error_f":
                Toast.makeText(this, R.string.error_send, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
