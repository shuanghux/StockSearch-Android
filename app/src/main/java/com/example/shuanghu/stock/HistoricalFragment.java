package com.example.shuanghu.stock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.example.shuanghu.stock.AppController.TAG;
import static com.example.shuanghu.stock.MainActivity.WEB_URL;



public class HistoricalFragment extends Fragment {
    String target_symb;
    WebView webView;
    String priceJsonString;
    String jsMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        target_symb = getActivity().getIntent().getExtras().getString(MainActivity.EXTRA_SYMB_KEY);
        View rootView = inflater.inflate(R.layout.fragment_historical, container, false);
        // HighChart init
        webView = (WebView) rootView.findViewById(R.id.history_webview);
        // Override method so that not using default browser
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setVerticalScrollBarEnabled(true);

        webView.loadUrl("file:///android_asset/history_chart.html");
        //webView.addJavascriptInterface(new HistoryFragment.JsHandler(target_symb), "myMainHandler");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                mProgressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.evaluateJavascript("javascript:loadHistory('" + target_symb + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Toast.makeText(getContext(),"JS executed: " + value, Toast.LENGTH_SHORT).show();
                    }
                });
//                mProgressDialog.hide();
            }
        });
        return rootView;
    }

    public class JsHandler {
        String symbol;
        JsHandler(String symbol){
            this.symbol = symbol;
        }

        @JavascriptInterface
        public void sendToJava(String message) {
            jsMessage = message;
            //Toast.makeText(getContext(),jsMessage,Toast.LENGTH_SHORT).show();
            Log.d("Debug Log:" , jsMessage);
        }
    }





//    public void getPriceData(String target_symb) {
//        final String thisSymb = target_symb;
//        Log.i("Debug", "Button Pressed on" + thisSymb);
//        final String url = WEB_URL + "price/" + thisSymb;
//        // Volley request for stock table information
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("Debug: ", "Price data fetched");
//                        priceJsonString = response;
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Error Volley", "Volley error");
//
//                if (error == null || error.networkResponse == null) {
//                    return;
//                }
//                String body;
//                //get status code here
//                final String statusCode = String.valueOf(error.networkResponse.statusCode);
//                //get response body and parse with appropriate encoding
//                try {
//                    body = new String(error.networkResponse.data, "UTF-8");
//                    Log.e("Request Error", body);
//                } catch (UnsupportedEncodingException e) {
//                    // exception
//                }
//            }
//        });
//        // Add the request to the RequestQueue.
//        AppController.getInstance().addToRequestQueue(stringRequest);
//    }



}
