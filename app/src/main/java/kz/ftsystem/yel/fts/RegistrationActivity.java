package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ftsystem.yel.fts.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;


public class RegistrationActivity extends AppCompatActivity implements MyCallback {

    @BindView(R.id.regName)
    EditText name;
    @BindView(R.id.regSurename)
    EditText surname;
    @BindView(R.id.regEMAIL)
    EditText email;
    @BindView(R.id.regTelNum)
    EditText phone;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.createAcc)
    Button btnCreateAcc;

    Boolean isVerifying = false;
    String myVerificationId = "";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(MyConstants.TAG, "onVerificationCompleted " + phoneAuthCredential);
                isVerifying = false;
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(MyConstants.TAG, "onVerificationFailed " + e.getMessage());
                isVerifying = false;
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Toast.makeText(RegistrationActivity.this, getString(R.string.invalid_phone_number_format), Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.d(MyConstants.TAG, "onCodeSent " + verificationId);
                isVerifying = false;
                myVerificationId = verificationId;
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                btnCreateAcc.setEnabled(true);
                Intent intent = new Intent(RegistrationActivity.this, CodeEntry2Activity.class);
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("surname", surname.getText().toString());
                intent.putExtra("email", email.getText().toString());
                intent.putExtra("verificationId", myVerificationId);
                startActivity(intent);
            }
        };

        FormatWatcher formatWatcher = new MaskFormatWatcher(
                MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        );
        formatWatcher.installOn(phone);
    }


    public void onClickReg(View view) {
        btnCreateAcc.setEnabled(false);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if (!isVerifying) {
            String phNum = "+7" +
                    phone.getText().toString().substring(4, 7) +
                    phone.getText().toString().substring(9, 12) +
                    phone.getText().toString().substring(13, 15) +
                    phone.getText().toString().substring(16, 18);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phNum,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallbacks);
            isVerifying = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", String.valueOf(name.getText()));
        outState.putString("surname", String.valueOf(surname.getText()));
        outState.putString("email", String.valueOf(email.getText()));
        outState.putString("telNum", String.valueOf(phone.getText()));
        outState.putBoolean("isVerifying", isVerifying);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        name.setText(savedInstanceState.getString("name"));
        surname.setText(savedInstanceState.getString("surname"));
        email.setText(savedInstanceState.getString("email"));
        phone.setText(savedInstanceState.getString("telNum"));
        isVerifying = savedInstanceState.getBoolean("isVerifying");
    }


    @Override
    public void fromBackend(HashMap<String, String> data) {
        switch (data.get("response")) {
            case "error":
                Toast.makeText(this, getResources().getString(R.string.cant_create_acc), Toast.LENGTH_SHORT).show();
                break;
            case "record_ex":
                Toast.makeText(this, getResources().getString(R.string.reg_record_exists), Toast.LENGTH_SHORT).show();
                break;
            case "field_er":
                Toast.makeText(this, getResources().getString(R.string.field_error), Toast.LENGTH_SHORT).show();
                break;
            case "ok":
                DB preferences = new DB(this);
                preferences.open();
                preferences.setVariable(MyConstants.MY_ID, data.get("id"));
                preferences.setVariable(MyConstants.MY_TOKEN, data.get("token"));
                preferences.close();
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
