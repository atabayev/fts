package kz.ftsystem.yel.fts;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ftsystem.yel.fts.R;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.ftsystem.yel.fts.backend.MyConstants;

public class Pay3DSActivity extends AppCompatActivity {

    @BindView(R.id.wv3DS)
    WebView webView;

    String md = "";
    String paReq = "";
    String acsUrl = "";
    String termUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_3ds);

        ButterKnife.bind(this);

        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        if (getIntent().hasExtra("md")) {
            md = getIntent().getStringExtra("md");
        }

        if (getIntent().hasExtra("paReq")) {
            paReq = getIntent().getStringExtra("paReq");
        }

        if (getIntent().hasExtra("acsUrl")) {
            acsUrl = getIntent().getStringExtra("acsUrl");
        }

        termUrl = MyConstants.BASE_URL + "payment/term_url/";
        String postData;
        try {
            postData = "MD="+URLEncoder.encode(md, "UTF-8") +
                    "&PaReq="+URLEncoder.encode(paReq, "UTF-8") +
                    "&TermUrl="+URLEncoder.encode(termUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            postData = "";
            e.printStackTrace();
        }
        webView.postUrl(acsUrl, postData.getBytes());
    }

}


class MyWebViewClient extends WebViewClient {
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(request.getUrl().toString());
        return true;
    }

    // Для старых устройств
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
