package com.example.shuanghu.stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shuanghu on 11/22/17.
 */

public class TableAdapter extends BaseAdapter {
    private Context context;
    private List<TableItem> datas;

    public TableAdapter(Context context,List<TableItem> datas) {
        this.context=context;
        this.datas=datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TableItem tableItem= (TableItem) getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.stock_table_list,null);
            viewHolder=new ViewHolder();
            viewHolder.arrowImg=(ImageView)view.findViewById(R.id.table_item_arrow);
            viewHolder.propertyTextView=(TextView)view.findViewById(R.id.table_item_property);
            viewHolder.valueTextView = (TextView) view.findViewById(R.id.table_item_value);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.propertyTextView.setText(tableItem.getProperty());
        viewHolder.valueTextView.setText(tableItem.getValue());
        if (tableItem.getProperty().matches("Change")) {
            viewHolder.arrowImg.setImageResource(tableItem.getArrow());
        }
        return view;
    }

    class ViewHolder{
        ImageView arrowImg;
        TextView propertyTextView;
        TextView valueTextView;
    }
}