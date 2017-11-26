package com.example.shuanghu.stock;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.shuanghu.stock.MainActivity.WEB_URL;

public class NewsFragment extends Fragment {
    ListView newsListView;
    String target_symb;
    View rootView;
    List<NewsItem> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        target_symb = getActivity().getIntent().getExtras().getString(MainActivity.EXTRA_SYMB_KEY);
        rootView = inflater.inflate(R.layout.fragment_news, container, false);
        newsListView = (ListView) rootView.findViewById(R.id.news_table);




        String url = WEB_URL + "news/" + target_symb;
        // Volley request for autocomplete suggestions
        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // the response is already constructed as a JSONObject!
                        list = new ArrayList<NewsItem>();
                        try {
                            for (int i = 0; i < Math.min(response.length(), 5); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String title = obj.getString("title");
                                String author = obj.getString("author");
                                String date = obj.getString("pubDate");
                                String link = obj.getString("link");
                                NewsItem newsItem = new NewsItem(title,author,date,link);
                                list.add(newsItem);
                            }
                            NewsAdapter adapter = new NewsAdapter(getActivity(), R.layout.news_list,list);
                            newsListView.setAdapter(adapter);
                            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    NewsItem newsItem = (NewsItem) parent.getItemAtPosition(position);
                                    Uri link = Uri.parse(newsItem.getLink());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, link);
                                    startActivity(intent);
                                }
                            });
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
        AppController.getInstance().addToRequestQueue(jsonRequest);





        // Inflate the layout for this fragment
        return rootView;
    }


}
