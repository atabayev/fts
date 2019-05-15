package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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


public class RegistrationActivity extends AppCompatActivity implements MyCallback {

    @BindView(R.id.regName)
    EditText name;
    @BindView(R.id.regSurename)
    EditText surname;
    @BindView(R.id.regEMAIL)
    EditText email;
    @BindView(R.id.regTelNum)
    EditText phone;

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

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(MyConstants.TAG, "onVerificationFailed " + e.getMessage());
                isVerifying = false;
                Toast.makeText(RegistrationActivity.this, getString(R.string.invalid_phone_number_format), Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.d(MyConstants.TAG, "onCodeSent " + verificationId);
                isVerifying = false;
                myVerificationId = verificationId;
                Intent intent = new Intent(RegistrationActivity.this, CodeEntry2Activity.class);
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("surname", surname.getText().toString());
                intent.putExtra("email", email.getText().toString());
                intent.putExtra("verificationId", myVerificationId);
                startActivity(intent);
            }
        };
    }


    public void onClickReg(View view) {
        if (!isVerifying) {
            String phNum = phone.getText().toString();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phNum,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallbacks);
            isVerifying = true;
        }

//        Backend backend = new Backend(this, this);
//        backend.registration(
//                name.getText().toString(),
//                surname.getText().toString(),
//                email.getText().toString(),
//                phone.getText().toString());
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
