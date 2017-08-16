package tr.com.wired.wiredapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    LayoutInflater li;
    BaseAdapter ba;
    SwipeRefreshLayout srl;
    Elements dS = new Elements();
    int page_size = 0;

    boolean endReached = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        srl = (SwipeRefreshLayout)findViewById(R.id.srl);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                fetchData();
            }
        });

        lv = (ListView)findViewById(R.id.lv);




        li = LayoutInflater.from(MainActivity.this);
        ba = new BaseAdapter() {
            @Override
            public int getCount() { return page_size >= dS.size() ? dS.size() : page_size; }

            @Override
            public Object getItem(int i) {
                return dS.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                int type = getItemViewType(i);
                if (view == null) view = li.inflate(R.layout.wired, null);
                TextView tbaslik = (TextView) view.findViewById(R.id.baslik);
                ImageView ivLogo = (ImageView) view.findViewById(R.id.logo);
                tbaslik.setText(dS.get(i).select("title").text());

                String resimURL = dS.get(i).getElementsByTag("media:thumbnail").attr("url");

                Picasso.with(MainActivity.this)
                        .load(resimURL)
                        .into(ivLogo);

                return view;
            }
        };

        lv.setAdapter(ba);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String postLinki = dS.get(i).select("link").text();
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(postLinki));
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("acilacak_adres", postLinki);
                startActivity(intent);
            }
        });

        fetchData();

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView,  int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                int curr = firstVisibleItem+visibleItemCount;
                Log.e("x","CURR : "+curr+" LAST : "+totalItemCount);

                if (curr == totalItemCount && !endReached)
                {
                    endReached = true;
                    page_size += 5;
                    ba.notifyDataSetChanged();
                }
                else
                {
                    endReached = false;
                }
            }
        });
    }

    public void fetchData()
    {
        new AsyncTask<String, String, String>()
        {
            protected String doInBackground(String... strings) {
                try {
                    dS = Jsoup
                            .connect("https://www.wired.com/feed/rss")
                            .ignoreContentType(true)
                            .timeout(30000)
                            .userAgent("Mozilla")
                            .get().select("item");

                    Log.e("x", "Post sayısı: " + dS.size());
                }
                catch (Exception e) { Log.e("x", "Err: " +e); }
                return null;
            }

            protected void onPostExecute(String s) {
                ba.notifyDataSetChanged();
                page_size = 5;
                endReached = false;
                srl.setRefreshing(false);
            }
        }.execute();
    }
}
