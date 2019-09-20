package kz.ftsystem.yel.fts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ftsystem.yel.fts.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.MyConstants;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import stfalcon.universalpickerdialog.UniversalPickerDialog;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, UniversalPickerDialog.OnPickListener, MyCallback {

    ArrayList<String> selectedFiles = new ArrayList<>();
    ArrayList<String> imgPaths = new ArrayList<>();
    ArrayList<String> docPaths = new ArrayList<>();
    String langFrom, langTo;
    String[] listLangFrom = new String[5];
    String[] listLangTo = new String[5];
    @BindView(R.id.etPagesCount)
    EditText etPageC;
    final String[] zipTypes = {".zip"};
    final String[] rarTypes = {".rar"};

    MaterialTextField materialTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String fcmToken = instanceIdResult.getToken();
                Log.d(MyConstants.TAG, "My token: " + fcmToken);
                DB preference = new DB(MainActivity.this);
                preference.open();
                String myId = preference.getVariable(MyConstants.MY_ID);
                String myToken = preference.getVariable(MyConstants.MY_TOKEN);
                preference.close();
                Backend backend = new Backend(MainActivity.this, MainActivity.this);
                backend.sendNewFcmToken(myId, myToken, fcmToken);
            }
        });


        ButterKnife.bind(this);
//        materialTextField = findViewById(R.id.materialTextField);
//        materialTextField.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                materialTextField.expand();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                assert imm != null;
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//            }
//        });
//        listLangFrom[0] = getString(R.string.lang_from_kz);
//        listLangFrom[1] = getString(R.string.lang_from_ru);
//        listLangFrom[2] = getString(R.string.lang_from_en);
//        listLangFrom[3] = getString(R.string.lang_from_ko);
//        listLangFrom[4] = getString(R.string.lang_from_ch);
//        listLangTo[0] = getString(R.string.lang_to_kz);
//        listLangTo[1] = getString(R.string.lang_to_ru);
//        listLangTo[2] = getString(R.string.lang_to_en);
//        listLangTo[3] = getString(R.string.lang_to_ko);
//        listLangTo[4] = getString(R.string.lang_to_ch);

        listLangTo[0] = getString(R.string.lang_to_kz);
        listLangTo[1] = getString(R.string.lang_to_ru);
        listLangTo[2] = getString(R.string.lang_to_en);
        listLangTo[3] = getString(R.string.lang_to_ko);
        listLangTo[4] = getString(R.string.lang_to_ch);

        DB preferences = new DB(this);
        preferences.open();
        if (preferences.getVariable(MyConstants.SHOW_TRAINING).equals("0")) {
            CharSequence selLang = getResources().getString(R.string.tt_select_language);
            CharSequence selPageC = getResources().getString(R.string.tt_select_page_count);
            CharSequence selImg = getResources().getString(R.string.tt_select_images);
            CharSequence selDoc = getResources().getString(R.string.tt_select_documents);
            CharSequence btnConf = getResources().getString(R.string.tt_btn_confirm);
            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.btnLangSel), selLang)
                                    .dimColor(R.color.dimColor)
                                    .outerCircleColor(R.color.colorAccent)
                                    .targetCircleColor(R.color.colorPrimary)
                                    .cancelable(false)
                                    .transparentTarget(true)
                                    .textColor(R.color.btnTextColor),
                            TapTarget.forView(findViewById(R.id.etPagesCount), selPageC)
                                    .dimColor(R.color.dimColor)
                                    .outerCircleColor(R.color.colorAccent)
                                    .targetCircleColor(R.color.colorPrimary)
                                    .cancelable(false)
                                    .transparentTarget(true)
                                    .textColor(R.color.btnTextColor),
                            TapTarget.forView(findViewById(R.id.btnImages), selImg)
                                    .dimColor(R.color.dimColor)
                                    .outerCircleColor(R.color.colorAccent)
                                    .targetCircleColor(R.color.colorPrimary)
                                    .transparentTarget(true)
                                    .textColor(R.color.btnTextColor),
                            TapTarget.forView(findViewById(R.id.btnDocs), selDoc)
                                    .dimColor(R.color.dimColor)
                                    .outerCircleColor(R.color.colorAccent)
                                    .targetCircleColor(R.color.colorPrimary)
                                    .transparentTarget(true)
                                    .textColor(R.color.btnTextColor),
                            TapTarget.forView(findViewById(R.id.btnConfirm), btnConf)
                                    .dimColor(R.color.btnTextColor)
                                    .outerCircleColor(R.color.colorAccent)
                                    .targetCircleColor(R.color.colorPrimary)
                                    .transparentTarget(true)
                                    .textColor(R.color.btnTextColor)
                    ).start();
            preferences.setVariable(MyConstants.SHOW_TRAINING, "1");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pageCount", String.valueOf(etPageC.getText()));
        outState.putString("langFrom", langFrom);
        outState.putString("langTo", langTo);
        outState.putStringArrayList("selectedFiles", selectedFiles);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etPageC.setText(savedInstanceState.getString("pageCount"));
        materialTextField.expand();
        langFrom = savedInstanceState.getString("langFrom");
        langTo = savedInstanceState.getString("langFrom");
        selectedFiles = savedInstanceState.getStringArrayList("selectedFiles");
    }

    private void select_language() {
        new UniversalPickerDialog.Builder(this)
                .setTitle(R.string.select_lang)
                .setBackgroundColor(getResources().getColor(R.color.splashScreenBg))
                .setContentTextColor(getResources().getColor(R.color.colorAccent))
                .setListener(this)
                .setInputs(
//                        new UniversalPickerDialog.Input(0, listLangFrom),
                        new UniversalPickerDialog.Input(0, listLangTo)
                )
                .show();
    }


    private void select_images() {
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            // Выбор изображения из Галереи
            FilePickerBuilder.getInstance().setMaxCount(50)
                    .setSelectedFiles(imgPaths)
                    .setActivityTheme(R.style.LibAppTheme)
                    .pickPhoto(this);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MyConstants.REQUEST_STORAGE_I);
        }
    }

    private void select_docs() {
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            FilePickerBuilder.getInstance().setMaxCount(50)
                    .addFileSupport("ZIP", zipTypes, R.drawable.ic_zip)
                    .addFileSupport("RAR", rarTypes, R.drawable.ic_rar)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.LibAppTheme)
                    .pickFile(this);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MyConstants.REQUEST_STORAGE_D);
        }
    }

    private void checkout() {
        EditText etPageCount = findViewById(R.id.etPagesCount);
        Intent intent = new Intent(this, FinishActivity.class);
//        intent.putExtra("lang", langFrom + " " + langTo);
        intent.putExtra("lang", langTo);
        intent.putExtra("comment", etPageCount.getText().toString());
        intent.putStringArrayListExtra("imgs", imgPaths);
        intent.putStringArrayListExtra("docs", docPaths);
        startActivity(intent);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLangSel:
                // Выбор языка
                select_language();
                break;
            case R.id.btnImages:
                select_images();
                break;
            case R.id.btnDocs:
                // Выбор файла из памяти
                select_docs();
                break;
            case R.id.btnConfirm:
                if (docPaths.size() > 0 || imgPaths.size() > 0) {
                    checkout();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.select_file_to_send), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == MainActivity.RESULT_OK && data != null) {
                    imgPaths = new ArrayList<>();
                    imgPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                break;
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == MainActivity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;
            case 555:
                Toast.makeText(this, "111", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MyConstants.REQUEST_STORAGE_I: {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(this, "Приложению необходим доступ к хранилищам для выбора изоброжения(и)", Toast.LENGTH_LONG).show();

                        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                    } else {
                        Toast.makeText(this, "Разрешите приложению доступ к хранилищам", Toast.LENGTH_SHORT).show();
                        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(appSettingsIntent, 555);
                    }
                } else {
                    //Button btnImage = findViewById(R.id.btnImages);
                    onClick(findViewById(R.id.btnImages));
                }
            }
            break;
            case MyConstants.REQUEST_STORAGE_D: {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(this, "Приложению необходим доступ к хранилищам для выбора документа(ов)", Toast.LENGTH_LONG).show();

                        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                    } else {
                        Toast.makeText(this, "Разрешите приложению доступ к хранилищам", Toast.LENGTH_SHORT).show();
                        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(appSettingsIntent, 555);
                    }
                } else {
                    onClick(findViewById(R.id.btnDocs));
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onPick(int[] selectedValues, int key) {
//        langFrom = listLangFrom[selectedValues[0]];
        langTo = listLangTo[selectedValues[0]];
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {

    }
}
