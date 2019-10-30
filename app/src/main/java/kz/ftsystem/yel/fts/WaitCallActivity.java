package kz.ftsystem.yel.fts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.ftsystem.yel.fts.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ftsystem.yel.fts.backend.MyConstants;
import kz.ftsystem.yel.fts.backend.database.DB;

public class WaitCallActivity extends AppCompatActivity {

    @BindView(R.id.wcDeadline)
    TextView deadline;

    String deadlineToPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_call);
        ButterKnife.bind(this);

        DB preference = new DB(this);
        preference.open();
        deadlineToPay = preference.getVariable(MyConstants.DEADLINE_TO_PAY);
        preference.close();

        deadline.setText(deadlineToPay);
    }

    @Override
    public void onBackPressed() {

    }
}
