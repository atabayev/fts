package kz.ftsystem.yel.fts;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ftsystem.yel.fts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.SimpleTimeZone;

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

public class AuthenticationActivity extends AppCompatActivity implements MyCallback {

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private Switch isAutoEnter;
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    @BindView(R.id.chbAutoEnter)
    CheckBox checkBox;
    @BindView(R.id.edPhoneNumber)
    EditText phoneNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentication);
        // TODO: Реализовать аутентификацию по СМС
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        FormatWatcher formatWatcher = new MaskFormatWatcher(
                MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        );
        formatWatcher.installOn(phoneNum);
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        //FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (mVerificationInProgress) {
//            startPhoneNumberVerification(phoneNum.getText().toString());
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
//    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnter:
                String editedPhoneNumber = phoneNum.getText().toString().substring(4, 7) +
                        phoneNum.getText().toString().substring(9, 12) +
                        phoneNum.getText().toString().substring(13, 15) +
                        phoneNum.getText().toString().substring(16, 18);
                if (phoneNum.getText().toString().isEmpty()) {
                    phoneNum.setError(getResources().getString(R.string.enter_phone_num));
                    break;
                }
                DB preferences = new DB(this);
                preferences.open();
                if (checkBox.isChecked()) {
                    preferences.setVariable(MyConstants.AUTO_ENTER, "1");
                } else {
                    preferences.setVariable(MyConstants.AUTO_ENTER, "0");
                }
                preferences.setVariable(MyConstants.MY_PHONE_NUM, phoneNum.getText().toString());
                preferences.close();
                Backend backend = new Backend(this, this);
                backend.authentication(editedPhoneNumber);
                break;
            case R.id.btnReg:
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

//    private void startPhoneNumberVerification(String s) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNum.getText().toString(),
//                60,
//                TimeUnit.DAYS.SECONDS,
//                this,
//                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                    @Override
//                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                        mVerificationInProgress = true;
//                        Log.d(MyConstants.TAG, "onVerificationCompleted");
//                        signInWithPhoneAuthCredential(phoneAuthCredential);
//                    }
//
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//                        Log.d(MyConstants.TAG, "onVerificationFailed");
//                    }
//
//                    @Override
//                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                        Log.d(MyConstants.TAG, "onCodeSent");
//                        mVerificationId = verificationId;
//                    }
//                }
//        );
//    }

//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(MyConstants.TAG, "signInWithCredential:success");
//
//                            FirebaseUser user = task.getResult().getUser();
//                            // ...
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            Log.w(MyConstants.TAG, "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
//                        }
//                    }
//                });
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        switch (data.get("response")) {
            case "error_f":
                Toast.makeText(this, getResources().getString(R.string.field_error), Toast.LENGTH_LONG).show();
                break;
            case "denied":
                Toast.makeText(this, getResources().getString(R.string.auth_false), Toast.LENGTH_LONG).show();
                break;
            case "access":
                DB preferences = new DB(this);
                preferences.open();
                preferences.setVariable(MyConstants.MY_ID, data.get("id"));
                preferences.setVariable(MyConstants.MY_TOKEN, data.get("token"));
                Intent intent = new Intent(this, MainActivity.class);
                switch (data.get("status")) {
                    case "0":
                        intent = new Intent(this, MainActivity.class);
                        break;
                    case "1":
                        intent = new Intent(this, OrderAcceptedActivity.class);
                        break;
                    case "2":
                        intent = new Intent(this, OrderResultActivity.class);
                        if (!preferences.getVariable("orderId").equals("0")) {
                            intent.putExtra("orderId", preferences.getVariable("orderId"));
                            intent.putExtra("language", preferences.getVariable("language"));
                            intent.putExtra("pagesCount", preferences.getVariable("pagesCount"));
                            intent.putExtra("price", preferences.getVariable("price"));
                            intent.putExtra("dateEnd", preferences.getVariable("dateEnd"));
                            intent.putExtra("urgency", preferences.getVariable("urgency"));
                        }
                        break;
                    case "3":
                        intent = new Intent(this, WaitingTranslatorActivity.class);
                        break;
                    case "4":
                        intent = new Intent(this, WaitingOrderActivity.class);
                        break;
                    case "6":
                        intent = new Intent(this, TranslatingFinishedActivity.class);
                        break;
                }
                startActivity(intent);
                break;
            case "error":
                Toast.makeText(this, R.string.error_server_not_available, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, "Unknown error!", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
