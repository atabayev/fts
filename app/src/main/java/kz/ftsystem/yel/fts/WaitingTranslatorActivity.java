package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.ftsystem.yel.fts.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.MessageEvent;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;

public class WaitingTranslatorActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MyCallback {

//    @BindView(R.id.wtaSwipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_translator);

//        ButterKnife.bind(this);

        swipeRefreshLayout = findViewById(R.id.wtaSwipeContainer);

        TextView tvId = findViewById(R.id.wtaIdText);
        TextView tvLang = findViewById(R.id.wtaLangText);
        TextView tvPageCount = findViewById(R.id.wtaPagesCountText);
        TextView tvPrice = findViewById(R.id.wtaPriceText);
        TextView tvDateEnd = findViewById(R.id.wtaDateEndText);
        TextView tvUrgency = findViewById(R.id.wtaUrgencyText);

        DB preferences = new DB(this);
        preferences.open();

        tvId.setText(preferences.getVariable(MyConstants.MY_ORDER_ID));
        tvLang.setText(preferences.getVariable(MyConstants.MY_ORDER_LANG));
        tvPageCount.setText(preferences.getVariable(MyConstants.MY_ORDER_PAGE_COUNT));
        tvPrice.setText(preferences.getVariable(MyConstants.MY_ORDER_PRICE));
        tvDateEnd.setText(preferences.getVariable(MyConstants.MY_ORDER_DATE_END));
        tvUrgency.setText(preferences.getVariable(MyConstants.MY_ORDER_URGENCY));
        preferences.close();

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        swipeRefreshLayout.setRefreshing(false);
        switch (data.get("response")) {
            case "yes":
                Toast.makeText(this, "Переводчик найден, Ваш заказ в процессе перевода", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, WaitingOrderActivity.class);
                startActivity(intent);
                finish();
                break;
            case "no":
                Toast.makeText(this, "Введется поиск переводчика...", Toast.LENGTH_SHORT).show();
                break;
            case "error_f":
                Toast.makeText(this, R.string.error_send, Toast.LENGTH_SHORT).show();
                break;
            case "denied":
                Toast.makeText(this, R.string.ora_access_denied, Toast.LENGTH_SHORT).show();
                break;
            case "error_no_order":
                Toast.makeText(this, R.string.ora_error_order_id, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        DB preferences = new DB(this);
        preferences.open();
        String cid = preferences.getVariable(MyConstants.MY_ID);
        String token = preferences.getVariable(MyConstants.MY_TOKEN);
        String oid = preferences.getVariable(MyConstants.ORDER_ID);
        Backend backend = new Backend(this, this);
        backend.doYouFindTranslator(cid, token, oid);
        preferences.close();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.message.equals("1")) {
            refresh();
        }
    }

    // This method will be called when a SomeOtherEvent is posted
    @Subscribe
    public void handleSomethingElse(MessageEvent event) {
//        doSomethingWith(event);
    }
}

