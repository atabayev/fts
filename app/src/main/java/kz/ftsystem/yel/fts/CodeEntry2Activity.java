package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ftsystem.yel.fts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.MyConstants;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;

public class CodeEntry2Activity extends AppCompatActivity implements MyCallback {

    @BindView(R.id.etCode)
    EditText etCode;

    String name;
    String surname;
    String email;
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_entry2);
        ButterKnife.bind(this);

        name = getIntent().getStringExtra("name");
        surname = getIntent().getStringExtra("surname");
        email = getIntent().getStringExtra("email");
        verificationId = getIntent().getStringExtra("verificationId");
    }

    @OnClick(R.id.btnCheck)
    public void onClick(View view) {
        String code = etCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    Log.d(MyConstants.TAG, user.getPhoneNumber());

                    Backend backend = new Backend(CodeEntry2Activity.this, CodeEntry2Activity.this);
                    backend.registration(name, surname, email, user.getPhoneNumber());
                    // TODO: Дальше идет старт чего-то
                } else {
                    Log.w(MyConstants.TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        // [START_EXCLUDE silent]
                        etCode.setError("Invalid code.");
                    }
                }
            }
        });
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
