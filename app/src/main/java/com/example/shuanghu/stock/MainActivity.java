package com.example.shuanghu.stock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    private ListView stockListView;
    SharedPreferences.Editor fd;
    SharedPreferences FeedPref;
    ListView listView;
    public static final String EXTRA_SYMB_KEY = "extra_symbol_key";
    public static final String WEB_URL = "http://shuang.us-east-1.elasticbeanstalk.com/";
    private AutoCompleteTextView search_box;
//    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FeedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        queue = Volley.newRequestQueue(getApplicationContext());
        fd = FeedPref.edit();
        initInputBox();
        init_btn_getQuote();
        try {
            showFavList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initInputBox() {
        search_box = findViewById(R.id.search_box);
        search_box.setThreshold(1);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getInputSuggest();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void init_btn_getQuote() {
        TextView btn_getQuote = findViewById(R.id.btn_getQuote);
        btn_getQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = ((AutoCompleteTextView) findViewById(R.id.search_box)).getText().toString();
                String words[] = inputText.split(" ", 2);
                final String target_symb = words[0];
                // Move to detail activity
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(EXTRA_SYMB_KEY, target_symb);
                startActivity(intent);
            }
        });
    }


    public void getInputSuggest() {
        final ArrayList<String> autoCompleteItems = new ArrayList<>();
        // Instantiate the RequestQueue.
        String symb = ((AutoCompleteTextView) findViewById(R.id.search_box)).getText().toString();
        if (symb.matches("^[a-zA-z]+$")) {
            String url = WEB_URL + "autocomplete/" + symb;
            // Volley request for autocomplete suggestions
            JsonArrayRequest jsonRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // the response is already constructed as a JSONObject!
                            try {
                                for (int i = 0; i < Math.min(response.length(), 5); i++) {
                                    JSONObject obj = response.getJSONObject(i);
                                    String symb = obj.getString("Symbol");
                                    String corp_name = obj.getString("Name");
                                    String exchange = obj.getString("Exchange");
                                    autoCompleteItems.add(symb + " - " + corp_name + " (" + exchange + ")");
                                }
                                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(
                                        getApplicationContext(),
                                        R.layout.auto_complete,
                                        autoCompleteItems);
                                search_box.setAdapter(autoCompleteAdapter);
                                search_box.setThreshold(1);
                                search_box.showDropDown();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
//            jsonRequest.setTag("autoComplete");
//        queue.cancelAll("autoComplete");
//            jsonRequest.setShouldCache(false);

//            queue.add(jsonRequest);
            AppController.getInstance().addToRequestQueue(jsonRequest);
        }
    }

    public void showFavList() throws JSONException {
//        ArrayList<String> favListItems = new ArrayList<>();

        listView = (ListView) findViewById(R.id.stock_list_view);

        SimpleAdapter adapter = new SimpleAdapter(this, getFavData(), R.layout.fav_list,
                new String[]{"symbol", "price", "change"},
                new int[]{R.id.fav_symb, R.id.fav_price, R.id.fav_change});
        listView.setAdapter(adapter);
    }

    private List<Map<String, Object>> getFavData() throws JSONException {
        FeedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, ?> allEntries = FeedPref.getAll();
        ArrayList<String> favKeys = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            favKeys.add(entry.getKey());
        }
        for (String key : favKeys) {
            String jsonStr = FeedPref.getString(key, null);
            JSONObject jsonObj = new JSONObject(jsonStr);
            String stock_symbol = jsonObj.getString("Stock Ticker Symbol");
            String price = jsonObj.getString("Last Price");
            String change = jsonObj.getString("Change (Change Percent)");

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("symbol", stock_symbol);
            map.put("price", price);
            map.put("change", change);
            list.add(map);
        }
        return list;
    }



    public void clearSP() {
        FeedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        fd = FeedPref.edit();
        fd.clear();
        fd.apply();
    }

}
