//package com.example.shuanghu.stock;
//
///**
// * Created by shuanghu on 11/20/17.
// */
//import android.app.Activity;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//public class SharedList extends Activity {
//    String[] presidents;
//    ListView listView;
//    ArrayAdapter<String> adapter;
//    SharedPreferences FeedPref;
//    SharedPreferences.Editor fd;
//    TextView txt1,txt2;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.shared_preference);
//        ArrayList<String> favListItems = new ArrayList<>();
//        listView = (ListView) findViewById(R.id.stock_list_view);
//
//        FeedPref=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        Map<String, ?> allEntries = FeedPref.getAll();
//        ArrayList<String> favKeys = new ArrayList<>();
//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            favKeys.add(entry.getKey());
//        }
//        for (String key:favKeys) {
//            String  jsonStr = FeedPref.getString(key,null);
//            favListItems.add(jsonStr);
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, favListItems);
//
//
//
//        listView.setAdapter(adapter);
//
//    }
//}