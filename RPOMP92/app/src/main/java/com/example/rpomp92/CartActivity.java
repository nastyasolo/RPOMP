package com.example.rpomp92;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private ListView listViewCart;
    private GoodsAdapter cartAdapter;
    private List<Good> checkedGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listViewCart = findViewById(R.id.listViewCart);

        // Добавляем хэдер
        View header = getLayoutInflater().inflate(R.layout.header, null);
        listViewCart.addHeaderView(header);

        // Устанавливаем заголовок
        TextView tvHeaderTitle = header.findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText("Корзина товаров");

        // Обработчик кнопки "Показать задачу"
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

        // Получаем переданные товары
        checkedGoods = getIntent().getParcelableArrayListExtra("checkedGoods");
        if (checkedGoods == null || checkedGoods.isEmpty()) {
            Toast.makeText(this, "No items found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartAdapter = new GoodsAdapter(this, checkedGoods, null); // Передаем null, так как обновление не требуется
        listViewCart.setAdapter(cartAdapter);
    }
}