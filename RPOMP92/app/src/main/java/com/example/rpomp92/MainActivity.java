package com.example.rpomp92;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnChangeListener, View.OnClickListener {
    private ListView listView;
    private TextView tvTotalItems;
    private Button btnShowCheckedItems;
    private GoodsAdapter goodsAdapter;
    private List<Good> goodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        // Добавляем хэдер с SearchView
        View header = getLayoutInflater().inflate(R.layout.header, null);
        listView.addHeaderView(header);

        TextView tvHeaderTitle = header.findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText("Список товаров");

        Button btnTask = header.findViewById(R.id.btnTask);
        btnTask.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Формулировка задачи")
                    .setMessage("Практическое задание\n" +
                            "1. Разработать приложение MiniShop, состоящее из двух Activity (см. рисунки 3.3, 3.4 в источнике).\n" +
                            "2. В первом Activity создать список ListView с Header и Footer.\n" +
                            "3. В Footer разместить текстовое поле (TextView) для ввода количества ак-\n" +
                            "тивированных пользователем товаров, кнопку Show Checked Items для перехода в корзину товаров.\n" +
                            "4. Реализовать кастомизированный список ListView с помощью собственно-\n" +
                            "го адаптера, наследующего класс BaseAdapter.\n" +
                            "5. В каждом пункте списка отобразить следующую информацию о товаре:\n" +
                            "идентификационный номер, название, стоимость, чекбокс для возможности выбора товара пользователем.\n" +
                            "6. В текстовом поле (TextView) Footer списка динамически отображать об-\n" +
                            "щее текущее количество активированных товаров.\n" +
                            "7. При нажатии кнопки Show Checked Items реализовать переход во второе\n" +
                            "Activity с корзиной товаров.\n" +
                            "8. Корзину товаров реализовать в виде нового кастомизированного списка с\n" +
                            "выбранными товарами.\n" +
                            "9. Продемонстрировать работу приложения MiniShop на эмуляторе или ре-\n" +
                            "альном устройстве.\n")
                    .setPositiveButton("OK", null)
                    .show();
        });

        SearchView searchView = header.findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (goodsAdapter != null) {
                        goodsAdapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });
        } else {
            Log.e("MainActivity", "SearchView is null!");
        }

        goodsList = new ArrayList<>();
        goodsList.add(new Good(1, "Apple iPhone 15", 999.99, false));
        goodsList.add(new Good(2, "Samsung Galaxy S23", 899.99, false));
        goodsList.add(new Good(3, "Google Pixel 8", 799.99, false));
        goodsList.add(new Good(4, "OnePlus 11", 699.99, false));
        goodsList.add(new Good(5, "Xiaomi Mi 13", 599.99, false));
        goodsList.add(new Good(6, "Sony Xperia 1 V", 1299.99, false));
        goodsList.add(new Good(7, "Huawei P60 Pro", 1099.99, false));
        goodsList.add(new Good(8, "Sologub PO-11 Phone", 1999.99, false));
        goodsList.add(new Good(9, "Nokia G60", 399.99, false));
        goodsList.add(new Good(10, "Motorola Edge 40", 499.99, false));

        goodsAdapter = new GoodsAdapter(this, goodsList, this);
        listView.setAdapter(goodsAdapter);

        // Добавляем футер
        View footer = getLayoutInflater().inflate(R.layout.footer, null);
        listView.addFooterView(footer);

        tvTotalItems = footer.findViewById(R.id.tvTotalItems);
        btnShowCheckedItems = footer.findViewById(R.id.btnShowCheckedItems);
        btnShowCheckedItems.setOnClickListener(this);
    }

    @Override
    public void onDataChanged() {
        int totalChecked = goodsAdapter.getCheckedGoods().size();
        tvTotalItems.setText("Количество телефонов: " + totalChecked);
    }

    @Override
    public void onClick(View v) {
        List<Good> checkedGoods = goodsAdapter.getCheckedGoods();
        if (checkedGoods.isEmpty()) {
            Toast.makeText(this, "No items selected!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, CartActivity.class);
            intent.putParcelableArrayListExtra("checkedGoods", new ArrayList<>(checkedGoods));
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
