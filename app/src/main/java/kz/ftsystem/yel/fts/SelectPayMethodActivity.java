package kz.ftsystem.yel.fts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.ftsystem.yel.fts.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectPayMethodActivity extends AppCompatActivity {

    @BindView(R.id.spmPayByCard)
    Button payByCard;

    @BindView(R.id.spmPayByOrder)
    Button payByOrder;
    @BindView(R.id.spmPayByPhys)
    Button payByPhys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pay_method);

        ButterKnife.bind(this);
    }




    @OnClick(R.id.spmPayByCard)
    void payingByCard() {
        Intent intent = new Intent(this, CheckoutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.spmPayByPhys)
    void payingByPhys() {
        Intent intent = new Intent(this, PayPhysicalActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.spmPayByOrder)
    void payingByOrder() {
        Intent intent = new Intent(this, PayByOrderActivity.class);
        startActivity(intent);
    }
}
