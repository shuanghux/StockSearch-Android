package com.example.shuanghu.stock;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
    WebView webView;
    String jsMessage;
    String target_symb;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        target_symb = getActivity().getIntent().getExtras().getString(MainActivity.EXTRA_SYMB_KEY);
        View rootView = inflater.inflate(R.layout.fragment_current, container, false);
        tableListView = (ListView) rootView.findViewById(R.id.stock_table);
        searchStock(target_symb);

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
        // Inflate the layout for this fragment
        return rootView;
    }


    public void searchStock(String target_symb) {
        final String thisSymb = target_symb;
        Log.i("Debug", "Button Pressed on" + thisSymb);
        final String url = WEB_URL + "table/" + thisSymb;
        // Volley request for stock table information
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Debug", "Volly Request: " + url);
//                        FeedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                        fd = FeedPref.edit();
//
//                        fd.putString(thisSymb, response);
//                        fd.apply();
                        Log.i("Table Info", response);
                        try {
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
                Log.e("Error Volley", "Volley error");

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

//    public List<TableItem> getTableItems() throws JSONException {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        Iterator<String> properties = tableInfoData.keys();
//        int imgUp = R.drawable.up;
//        int imgDown = R.drawable.down;
//        while (properties.hasNext()) {
//            String property = properties.next();
//            Log.d("Property",property);
//            String value = tableInfoData.getString(property);
//            Map<String,Object> map = new HashMap<>();
//            map.put("property",property);
//            map.put("value",value);
//
//            if (property.matches("Change")) {
//                if(value.matches("-")) {
//                    ImageView imgView;
//                    map.put("arrow", R.drawable.down);
//                } else {
//                    map.put("arrow",R.drawable.up);
//                }
//            }
//            list.add(map);
//        }
//        return list;
//    }

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
    }
}
