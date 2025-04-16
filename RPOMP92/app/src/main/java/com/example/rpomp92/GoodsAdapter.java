package com.example.rpomp92;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GoodsAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, Filterable {
    private Context context;
    private List<Good> goodsList;
    private List<Good> filteredGoodsList; // Отфильтрованный список
    private OnChangeListener onChangeListener;

    public GoodsAdapter(Context context, List<Good> goodsList, OnChangeListener onChangeListener) {
        this.context = context;
        this.goodsList = goodsList;
        this.filteredGoodsList = new ArrayList<>(goodsList); // Создаем копию списка
        this.onChangeListener = onChangeListener;
    }

    @Override
    public int getCount() {
        return filteredGoodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredGoodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_good, parent, false);
        }

        Good good = filteredGoodsList.get(position); // Используем отфильтрованный список
        TextView tvId = convertView.findViewById(R.id.tvId);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        CheckBox cbSelect = convertView.findViewById(R.id.cbSelect);

        tvId.setText(String.valueOf(good.getId()));
        tvName.setText(good.getName());
        tvPrice.setText(String.format("$%.2f", good.getPrice()));
        cbSelect.setChecked(good.isChecked());
        cbSelect.setTag(position);
        cbSelect.setOnCheckedChangeListener(this);

        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (int) buttonView.getTag();
        filteredGoodsList.get(position).setChecked(isChecked);

        if (onChangeListener != null) { // Проверяем, что слушатель не null
            onChangeListener.onDataChanged();
        }
    }

    public List<Good> getCheckedGoods() {
        List<Good> checkedGoods = new ArrayList<>();
        for (Good good : goodsList) {
            if (good.isChecked()) {
                checkedGoods.add(good);
            }
        }
        return checkedGoods;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Good> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(goodsList); // Показываем весь список
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Good good : goodsList) {
                        if (good.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(good);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredGoodsList.clear();
                filteredGoodsList.addAll((List<Good>) results.values);
                notifyDataSetChanged();
            }
        };
    }

}
