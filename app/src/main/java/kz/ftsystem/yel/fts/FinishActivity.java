package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ftsystem.yel.fts.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class FinishActivity extends AppCompatActivity implements MyCallback {

    private String lang, comment;
    private ArrayList<String> imgPaths = new ArrayList<>();
    ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<ImageView> images = new ArrayList<>();
    int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;

    LinearLayout.LayoutParams lParams;
    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.llImages)
    LinearLayout llImages;
    @BindView(R.id.llMain)
    LinearLayout llMain;
    @BindView(R.id.pgBar)
    ProgressBar progressBar;
    @BindView(R.id.tvPgBar)
    TextView tvPgBar;
    @BindView(R.id.btnOrder)
    Button btnOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        ButterKnife.bind(this);
        lang = getIntent().getStringExtra("lang");
        comment = getIntent().getStringExtra("comment");
        imgPaths = getIntent().getStringArrayListExtra("imgs");
        docPaths = getIntent().getStringArrayListExtra("docs");

        lParams = new LinearLayout.LayoutParams(matchParent, wrapContent);
        lParams.setMargins(8, 8, 8, 8);
        lParams.width = 500;
        lParams.height = 500;
        lParams.gravity = Gravity.START;

        progressBar.setVisibility(ProgressBar.INVISIBLE);
        tvPgBar.setVisibility(TextView.INVISIBLE);


        View.OnClickListener oclBtn = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < imgPaths.size() - 1; i++) {
                    if (view.getId() == images.get(i).getId()) {
                        Intent intent = new Intent(FinishActivity.this, FullImageViewActivity.class);
                        intent.putExtra("uri", imgPaths.get(i));
                        startActivity(intent);
                    }
                }
            }
        };

        for (int i = 0; i < imgPaths.size(); i++) {
            try {
                images.add(new ImageView(this));
                images.get(i).setId(3000 + i);
                images.get(i).setOnClickListener(oclBtn);
                llImages.addView(images.get(i), lParams);
                Picasso.with(this)
                        .load("file://" + imgPaths.get(i))
                        .error(R.drawable.ic_error_outline_white_24dp)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        //.resize(500, 500)
                        .into(images.get(i));
            } catch (Exception e) {
                Log.d(MyConstants.TAG, "error: " + e.getMessage());
            }
        }
        int j = 0;
        int resId = R.drawable.ic_unknown;
        int docsIndex = imgPaths.size() + docPaths.size();
        for (int i = imgPaths.size(); i < docsIndex; i++) {
            images.add(new ImageView(this));
            images.get(i).setId(3000 + i);
            images.get(i).setOnClickListener(oclBtn);
            llImages.addView(images.get(i), lParams);
            switch (docPaths.get(j).substring(docPaths.get(j).lastIndexOf(".") + 1)) {
                case "ppt":
                    resId = R.drawable.ic_ppt;
                    break;
                case "pptx":
                    resId = R.drawable.ic_ppt;
                    break;
                case "xls":
                    resId = R.drawable.ic_xls;
                    break;
                case "xlsx":
                    resId = R.drawable.ic_xls;
                    break;
                case "doc":
                    resId = R.drawable.ic_doc;
                    break;
                case "docx":
                    resId = R.drawable.ic_doc;
                    break;
                case "pdf":
                    resId = R.drawable.ic_pdf;
                    break;
                case "txt":
                    resId = R.drawable.ic_txt;
                    break;
                case "zip":
                    resId = R.drawable.ic_zip;
                    break;
                case "rar":
                    resId = R.drawable.ic_rar;
                    break;
            }
            Picasso.with(this)
                    .load(resId)
                    .error(R.drawable.ic_error_outline_white_24dp)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    //.resize(500, 500)
                    .into(images.get(i));
            j++;
        }
        TextView tvLang = findViewById(R.id.tvLang);
        TextView tvComment = findViewById(R.id.etComment);
        tvLang.setText(lang);
        tvComment.setText(comment);
    }

    public void onClick(View view) {
        Backend backend = new Backend(this, this);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvPgBar.setVisibility(TextView.VISIBLE);
        backend.sendingData(imgPaths, lang, comment);
        btnOrder.setEnabled(false);
    }

//    @NonNull
//    private RequestBody createPartFromString(String descriptionString) {
//        return RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
//    }
//
//    @NonNull
//    private MultipartBody.Part prepareFilePart(String partName, String filePath) {
//        File file = FileUtils.getFile(String.valueOf(filePath));
//        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
//        RequestBody requestFile =
//                RequestBody.create(
//                        MediaType.parse(mimeType),
//                        file);
//        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
//    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        tvPgBar.setVisibility(TextView.INVISIBLE);
        btnOrder.setEnabled(true);
        switch (data.get("response")) {
            case "error_f":
                Toast.makeText(this, getResources().getString(R.string.field_error), Toast.LENGTH_SHORT).show();
                break;
            case "error_a":
                Toast.makeText(this, R.string.error_attachment, Toast.LENGTH_SHORT).show();
                break;
            case "denied":
                Toast.makeText(this, getResources().getString(R.string.unknown_client), Toast.LENGTH_SHORT).show();
                break;
            case "ord_added":
                DB preferences = new DB(this);
                preferences.open();
                preferences.setVariable(MyConstants.ORDER_ID, data.get("id"));
                preferences.setVariable(MyConstants.IS_ORDERED, "1");
                Intent intent = new Intent(this, OrderAcceptedActivity.class);
                startActivity(intent);
                preferences.close();
                finish();
                break;
        }
    }
}
