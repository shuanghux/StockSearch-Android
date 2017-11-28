package com.example.shuanghu.stock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.shuanghu.stock.MainActivity.WEB_URL;



public class CurrentFragment extends Fragment {
    ListView tableListView;
    private JSONObject tableInfoData;
    String tableInfoString;
    WebView webView;
    Button changeBtn;
    Button favBtn;
    Button fbBtn;
    View rootView;
    String jsMessage;
    String target_symb;
    String curr_type = "PRICE";
    SharedPreferences.Editor fd;
    SharedPreferences FeedPref;
    boolean isTypeChanged = false;
    boolean isTableReady = false;
    boolean isChartReady = false;
    ShareDialog shareDialog;
    String shareUrl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        target_symb = getActivity().getIntent().getExtras().getString(MainActivity.EXTRA_SYMB_KEY).toUpperCase();
        rootView = inflater.inflate(R.layout.fragment_current, container, false);
        tableListView = (ListView) rootView.findViewById(R.id.stock_table);
        favBtn = rootView.findViewById(R.id.detail_fav_btn);
        fbBtn = rootView.findViewById(R.id.detail_facebook_btn);
        searchStock(target_symb);
        FacebookSdk.setApplicationId("123944834967352");
        shareDialog = new ShareDialog(getActivity());



        // Btn Change init
        initChangeBtn();

        // Fav Button init
        initFavBtn();

        // FB Button init
        initFbBtn();

        // HighChart init
        webView = (WebView) rootView.findViewById(R.id.main_webview);
        // Override method so that not using default browser
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setVerticalScrollBarEnabled(true);

        webView.loadUrl("file:///android_asset/main_chart.html");
        webView.addJavascriptInterface(new CurrentFragment.JsHandler(target_symb), "myMainHandler");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                mProgressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.evaluateJavascript("javascript:load_price('" + target_symb + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Toast.makeText(getContext(),"JS executed: " + value, Toast.LENGTH_SHORT).show();
                    }
                });
//                mProgressDialog.hide();
            }
        });


        // Spinner Initiate
        Spinner indicator_spinner = (Spinner) rootView.findViewById(R.id.indicator_spinner);
        indicator_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                isTypeChanged = false;

                String[] languages = getResources().getStringArray(R.array.indicator_labels);

                if (!curr_type.equals(languages[pos].toUpperCase())) {
                    curr_type = languages[pos].toUpperCase();
                    Toast.makeText(getContext(), "你点击的是:"+languages[pos], Toast.LENGTH_SHORT).show();
                    isTypeChanged = true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }


    public void searchStock(String target_symb) {
        isTableReady = false;
        initFavBtn();
        final String thisSymb = target_symb;
        Log.i("Debug", "Button Pressed on" + thisSymb);
        final String url = WEB_URL + "table/" + thisSymb;
        // Volley request for stock table information
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isTableReady = true;
                        initFavBtn();
                        Log.i("Debug", "Volly Request: " + url);
//                        FeedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                        fd = FeedPref.edit();
//
//                        fd.putString(thisSymb, response);
//                        fd.apply();
                        Log.i("Table Info", response);
                        try {
                            tableInfoString = response;
                            tableInfoData = new JSONObject(response);
                            showInfoTable(tableListView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Log.i("SharedPreference", FeedPref.getString(thisSymb, null));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error Volley", "Volley error: " + error.toString());

                if (error == null || error.networkResponse == null) {
                    return;
                }
                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                    Log.e("Request Error", body);
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        });
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                4000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void showInfoTable(ListView tableListView) throws JSONException {
        SimpleAdapter adapter = new SimpleAdapter(getContext(), getTableData(), R.layout.stock_table_list,
                new String[]{"property", "value", "arrow"},
                new int[]{R.id.table_item_property, R.id.table_item_value, R.id.table_item_arrow});



        tableListView.setAdapter(adapter);
    }

    private List<Map<String, Object>> getTableData() throws JSONException {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Iterator<String> properties = tableInfoData.keys();
        int imgUp = R.drawable.up;
        int imgDown = R.drawable.down;
        while (properties.hasNext()) {
            String property = properties.next();
            Log.d("Property",property);
            String value = tableInfoData.getString(property);
            Map<String,Object> map = new HashMap<>();
            map.put("property",property);
            map.put("value",value);

            if (property.matches(".*Change.*")) {
                Log.d("Debug: ","Change property detected");
                if(value.matches(".*-.*")) {
                    map.put("arrow", R.drawable.down);
                } else {
                    map.put("arrow",R.drawable.up);
                }
            } else {
                map.put("arrow",R.drawable.invisible);
            }
            list.add(map);
        }
        return list;
    }


    public class JsHandler {
        String symbol;
        JsHandler(String symbol){
            this.symbol = symbol;
        }

        @JavascriptInterface
        public void sendToJava(String message) {
            jsMessage = message;
            Toast.makeText(getContext(),jsMessage,Toast.LENGTH_SHORT).show();
            Log.d("Debug Log:" , jsMessage);
        }
        @JavascriptInterface
        public void sendUrlToJava(String message) {
            shareUrl = message;
            Toast.makeText(getContext(),shareUrl +" Ready to share",Toast.LENGTH_SHORT).show();
            Log.i("IMGURL",shareUrl);
            isChartReady = true;
        }
    }

    public void loadChart(String type) {
        isChartReady = false;
        initFbBtn();
        type = type.toUpperCase();
        if(type.equals("PRICE")) {
            webView.evaluateJavascript("javascript:load_price('" + target_symb + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    isChartReady = true;
                    initFbBtn();
                    Toast.makeText(getContext(),"JS executed: PRICE", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            webView.evaluateJavascript("javascript:load_Chart('" + curr_type + "','"+ target_symb +"')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    isChartReady = true;
                    initFbBtn();
                    Toast.makeText(getContext(),"JS executed: Chart", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void initChangeBtn() {
        changeBtn = rootView.findViewById(R.id.change_btn);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTypeChanged) {
                    isChartReady = false;
                    loadChart(curr_type);
                }
            }
        });
    }

    public void initFavBtn() {
        favBtn = rootView.findViewById(R.id.detail_fav_btn);
//        FeedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        FeedPref = getContext().getSharedPreferences("FavListSP", 0);
        if (isTableReady) {
            favBtn.setEnabled(true);
        } else {
            favBtn.setEnabled(false);
        }
        if(FeedPref.contains(target_symb)) {
            Log.i("Debug","Found FD-"+target_symb);
            favBtn.setBackgroundResource(R.drawable.filled);
        } else {
            Log.i("Debug","Cannot Found FD-"+target_symb);
            favBtn.setBackgroundResource(R.drawable.empty);
        }
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FeedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                FeedPref = getContext().getSharedPreferences("FavListSP", 0);
                fd = FeedPref.edit();
                if(FeedPref.contains(target_symb)) {
                    // remove action
                    fd.remove(target_symb.toUpperCase());
                    favBtn.setBackgroundResource(R.drawable.empty);
                } else {
                    // add action
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(tableInfoString);
                        jsonObject.put("Date", System.currentTimeMillis());
                        tableInfoString = jsonObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fd.putString(target_symb.toUpperCase(),tableInfoString);
                    favBtn.setBackgroundResource(R.drawable.filled);
                }
                fd.apply();
            }
        });
    }

    public void initFbBtn() {
        fbBtn = rootView.findViewById(R.id.detail_facebook_btn);
        if (isChartReady) {
            fbBtn.setEnabled(true);
        } else {
            fbBtn.setEnabled(false);
        }
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"FB clicked",Toast.LENGTH_SHORT).show();
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(shareUrl))
                        .build();
                shareDialog.show(content);
            }
        });
    }
}
