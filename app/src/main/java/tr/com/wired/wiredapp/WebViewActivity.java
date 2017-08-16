package tr.com.wired.wiredapp;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.wagnerandade.coollection.Coollection.*;
import com.wagnerandade.coollection.query.order.Order;

public class WebViewActivity extends AppCompatActivity {
    Elements dS = new Elements();
    String lastURL = "";
    WebView web;
    String adres;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //web= new WebView(WebViewActivity.this);
        setContentView(R.layout.activity_web_view);

        web = (WebView)findViewById(R.id.webview);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setPluginState(WebSettings.PluginState.ON);
        web.getSettings().setAllowContentAccess(true);
        web.getSettings().setDomStorageEnabled(true);

        web.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //Log.e("x","Alert Geldi : "+message);
                return super.onJsAlert(view, url, message, result);
            }
        });
        web.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Log.e("x", "Yükleme Tamamlandı : " + url);
                String javaScript ="javascript:alert('hi');";
                web.loadUrl(javaScript);
                lastURL = url;
            }
        });

        adres = getIntent().getExtras().getString("acilacak_adres");
        web.loadUrl(adres);
    }

    public void btnWord_OnClick(View view) {
        new AsyncTask<String, String, String>()
        {
            protected String doInBackground(String... strings) {
                try {
                    dS = Jsoup
                            .connect(adres)
                            .timeout(30000)
                            .userAgent("Mozilla")
                            .get().select("main");

                    Log.e("x", "Metin:" + dS.text().toString());
                }
                catch (Exception e) { Log.e("x", "Err: " +e); }
                return null;
            }

            protected void onPostExecute(String s) {
                List<String> item = Arrays.asList(dS.text().toString().split(" "));

                HashMap<String, Integer> map = new HashMap<>();

                for (String t : item) {
                    if (map.containsKey(t)) {
                        map.put(t, map.get(t) + 1);

                    } else {
                        map.put(t, 1);
                    }
                }

                List<Words> items = new ArrayList<>();
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    if (key.length() > 3)
                    {
                        if(map.get(key) > 1)
                        {
                            Words w = new Words(key, map.get(key));
                            items.add(w);
                            //Log.e("x", "Kelime eklendi.");
                        }
                    }
                }

                List<Words> newItems = from(items).orderBy("count", Order.DESC).all();
                List<String> son = new ArrayList<>();
                for(int i=0; i<5; i++)
                {
                    //Log.e("x", "sorted list: " + newItems.get(i).getWord());
                    //Toast.makeText(WebViewActivity.this, "Sık çıkan kelimeler: " + newItems.get(i).getWord(), Toast.LENGTH_SHORT).show();
                    son.add(newItems.get(i).getWord());

                }
                new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle("Sık Çıkan Kelimeler")
                        .setMessage("" + son.toString().replace("[","").replace("]",""))
                        .setNegativeButton("Ok", null).show();
            }
        }.execute();
    }
}
