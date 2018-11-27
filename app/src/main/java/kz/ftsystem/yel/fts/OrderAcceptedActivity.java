package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import com.ftsystem.yel.fts.R;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.Backend;
import kz.ftsystem.yel.fts.backend.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;

public class OrderAcceptedActivity extends AppCompatActivity implements MyCallback, SwipeRefreshLayout.OnRefreshListener {

//    DB preferences;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_accepted);
        ButterKnife.bind(this);

        DB preferences = new DB(this);
        preferences.open();
        TextView yourId = findViewById(R.id.yourId);
        yourId.append(" " + preferences.getVariable(MyConstants.ORDER_ID));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        preferences.close();
    }

    @Override
    public void onBackPressed() {
        DB preferences = new DB(this);
        preferences.open();
        if (preferences.getVariable(MyConstants.IS_ORDERED).equals("1")) {
            super.onBackPressed();
            Intent intent = new Intent(this, SplashScreenActivity.class);
            intent.putExtra("just_ordered", true);
            startActivity(intent);
        }
        preferences.close();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        DB preferences = new DB(this);
        preferences.open();
        String myId = preferences.getVariable(MyConstants.MY_ID);
        String myToken = preferences.getVariable(MyConstants.MY_TOKEN);
        String orderId = preferences.getVariable(MyConstants.ORDER_ID);
        preferences.close();
        Backend backend = new Backend(this, this);
        backend.getInfoAboutMyOrder(myId, myToken, orderId);
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        swipeRefreshLayout.setRefreshing(false);
        switch (data.get("response")) {
            case "ready":
                Intent intent = new Intent(this, OrderResultActivity.class);
                intent.putExtra("orderId", data.get("orderId"));
                intent.putExtra("language", data.get("language"));
                intent.putExtra("pagesCount", data.get("pagesCount"));
                intent.putExtra("price", data.get("price"));
                intent.putExtra("dateEnd", data.get("dateEnd"));
                intent.putExtra("urgency", data.get("urgency"));
                startActivity(intent);
                finish();
                break;
            case "no":
                Toast.makeText(this, R.string.oaa_order_in_processed, Toast.LENGTH_SHORT).show();
                break;
            case "error_f":
                Toast.makeText(this, R.string.field_error, Toast.LENGTH_SHORT).show();
                break;
            case "error_o":
                Toast.makeText(this, R.string.error_orders_id, Toast.LENGTH_SHORT).show();
                break;
            case "denied":
                Toast.makeText(this, R.string.unknown_client, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, R.string.error_send, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
