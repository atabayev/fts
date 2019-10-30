package kz.ftsystem.yel.fts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ftsystem.yel.fts.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.MyConstants;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;

public class PayByOrderActivity extends AppCompatActivity implements MyCallback {

    @BindView(R.id.poBtnBack)
    ImageButton btnBack;

    @BindView(R.id.poBtnConfirm)
    Button confirm;

    @BindView(R.id.poProgressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_by_order);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.poBtnConfirm)
    void onClickConfirm() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        DB preferences = new DB(this);
        preferences.open();
        String myId = preferences.getVariable(MyConstants.MY_ID);
        String myToken = preferences.getVariable(MyConstants.MY_TOKEN);
        String orderId = preferences.getVariable(MyConstants.ORDER_ID);
        preferences.close();
        Backend backend = new Backend(this, this);
        backend.payByOrder(myId, myToken, orderId);
    }

    @OnClick(R.id.poBtnBack)
    void onClickBack() {
        finish();
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        switch (data.get("response")) {
            case "denied":
                Toast.makeText(this, R.string.ora_access_denied, Toast.LENGTH_SHORT).show();
                break;
            case "error_f":
                Toast.makeText(this, R.string.error_send, Toast.LENGTH_SHORT).show();
                break;
            case "error_no_order":
                Toast.makeText(this, R.string.ora_error_order_id, Toast.LENGTH_SHORT).show();
                break;
            case "ok":
//                TODO: Запустить активити ожидания
                DB preferences = new DB(this);
                preferences.open();
                preferences.setVariable(MyConstants.DEADLINE_TO_PAY, data.get("deadline"));
                preferences.close();
                Intent intent = new Intent(this, WaitCallActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
