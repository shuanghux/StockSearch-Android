package com.example.shuanghu.stock;

/**
 * Created by shuanghu on 11/25/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsItem> {

    private int resourceId;

    public NewsAdapter(Context context, int textViewResourceId,
                        List<NewsItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsItem newsItem = getItem(position); // 获取当前项的Fruit实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.newsTitle = (TextView) view.findViewById (R.id.news_title);
            viewHolder.newsAuthor = (TextView) view.findViewById(R.id.news_author);
            viewHolder.newsDate = (TextView) view.findViewById(R.id.news_date);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.newsTitle.setText(newsItem.getTitle());
        viewHolder.newsAuthor.setText(newsItem.getAuthor());
        viewHolder.newsDate.setText(newsItem.getDate());
        return view;
    }

    class ViewHolder {

        TextView newsTitle;
        TextView newsAuthor;
        TextView newsDate;

    }

}