package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.ftsystem.yel.fts.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;


public class RegistrationActivity extends AppCompatActivity implements MyCallback {

    @BindView(R.id.regName) EditText name;
    @BindView(R.id.regSurename) EditText surname;
    @BindView(R.id.regEMAIL) EditText email;
    @BindView(R.id.regTelNum) EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
    }

    public void onClick(View view) {
        Backend backend = new Backend(this, this);
        backend.registration(
                name.getText().toString(),
                surname.getText().toString(),
                email.getText().toString(),
                phone.getText().toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", String.valueOf(name.getText()));
        outState.putString("surname", String.valueOf(surname.getText()));
        outState.putString("email", String.valueOf(email.getText()));
        outState.putString("telNum", String.valueOf(phone.getText()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        name.setText(savedInstanceState.getString("name"));
        surname.setText(savedInstanceState.getString("surname"));
        email.setText(savedInstanceState.getString("email"));
        phone.setText(savedInstanceState.getString("telNum"));
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
