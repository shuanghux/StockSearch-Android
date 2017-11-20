package com.example.shuanghu.stock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView search_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.searchButton);
        search_box = findViewById(R.id.search_box);
        search_box.setThreshold(1);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final ArrayList<String> autoCompleteItems = new ArrayList<>();
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String symb = ((AutoCompleteTextView) findViewById(R.id.search_box)).getText().toString();
                String url = "http://shuang.us-east-1.elasticbeanstalk.com/autocomplete/" + symb;
                // Volley request for autocomplete suggestions
                JsonArrayRequest jsonRequest = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                // the response is already constructed as a JSONObject!
                                try {
                                    for (int i = 0; i < Math.min(response.length(),5) ; i++) {
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
                jsonRequest.setTag("autoComplete");
                queue.cancelAll("autoComplete");
                queue.add(jsonRequest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}
