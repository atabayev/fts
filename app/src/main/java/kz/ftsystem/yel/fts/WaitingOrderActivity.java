package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ftsystem.yel.fts.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.MessageEvent;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;

public class WaitingOrderActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MyCallback {

    @BindView(R.id.woa_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.oraIdText)
    TextView tvId;

    @BindView(R.id.oraLangText)
    TextView tvLang;

    @BindView(R.id.oraPagesCountText)
    TextView tvPageCount;

    @BindView(R.id.oraPriceText)
    TextView tvPrice;

    @BindView(R.id.oraDateEndText)
    TextView tvDateEnd;

    @BindView(R.id.oraUrgencyText)
    TextView tvUrgency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_order);

        ButterKnife.bind(this);

        getOrderParams();
//        swipeRefreshLayout = findViewById(R.id.woa_swipe_container);

//        TextView tvId = findViewById(R.id.oraIdText);
//        TextView tvLang = findViewById(R.id.oraLangText);
//        TextView tvPageCount = findViewById(R.id.oraPagesCountText);
//        TextView tvPrice = findViewById(R.id.oraPriceText);
//        TextView tvDateEnd = findViewById(R.id.oraDateEndText);
//        TextView tvUrgency = findViewById(R.id.oraUrgencyText);

//        DB preferences = new DB(this);
//        preferences.open();
//
//        tvId.setText(preferences.getVariable(MyConstants.MY_ORDER_ID));
//        tvLang.setText(preferences.getVariable(MyConstants.MY_ORDER_LANG));
//        tvPageCount.setText(preferences.getVariable(MyConstants.MY_ORDER_PAGE_COUNT));
//        tvPrice.setText(preferences.getVariable(MyConstants.MY_ORDER_PRICE));
//        tvDateEnd.setText(preferences.getVariable(MyConstants.MY_ORDER_DATE_END));
//        tvUrgency.setText(preferences.getVariable(MyConstants.MY_ORDER_URGENCY));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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
        backend.doYouTranslateOrder(cid, token, oid);
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        swipeRefreshLayout.setRefreshing(false);
        switch (data.get("response")) {
            case "ready":
                tvId.setText(data.get("orderId"));
                tvLang.setText(data.get("language"));
                tvPageCount.setText(data.get("pagesCount"));
                tvPrice.setText(data.get("price"));
                tvDateEnd.setText(data.get("dateEnd"));
                tvUrgency.setText(data.get("urgency"));
                break;
            case "yes":
//                Toast.makeText(this, "Ваш заказ переведен и отправлен на Вашу почту", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, TranslatingFinishedActivity.class);
//                TODO:
                startActivity(intent);
                finish();
                break;
            case "no":
                Toast.makeText(this, "Все еще переводится...", Toast.LENGTH_SHORT).show();
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
