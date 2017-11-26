package com.example.shuanghu.stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FavStockAdapter extends ArrayAdapter<FavStockItem> {

    private int resourceId;

    public FavStockAdapter(Context context, int textViewResourceId,
                        List<FavStockItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FavStockItem favStockItem = getItem(position); // 获取当前项的Fruit实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.stockName = (TextView) view.findViewById (R.id.fav_symb);
            viewHolder.stockPrice = (TextView) view.findViewById (R.id.fav_price);
            viewHolder.stockChange = (TextView) view.findViewById(R.id.fav_change);

            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }


        viewHolder.stockName.setText(favStockItem.getName());
        String changeStr = favStockItem.getChange() + "(" + favStockItem.getChangePercent() + "%)";
        viewHolder.stockChange.setText(changeStr);
        if (favStockItem.getChange().matches(".*-.*")){
            viewHolder.stockChange.setTextColor(getContext().getColor(R.color.red));
        } else {
            viewHolder.stockChange.setTextColor(getContext().getColor(R.color.green));
        }
        viewHolder.stockPrice.setText(favStockItem.getPrice());
        return view;
    }

    class ViewHolder {
        TextView stockName;
        TextView stockPrice;
        TextView stockChange;
    }

}