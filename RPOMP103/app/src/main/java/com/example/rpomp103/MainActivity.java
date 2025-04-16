package com.example.rpomp103;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);


        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);


        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final String[] titles = {"Show", "Add", "Del", "Update"};

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new FragmentShow();
                    case 1:
                        return new FragmentAdd();
                    case 2:
                        return new FragmentDel();
                    case 3:
                        return new FragmentUpdate();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        findViewById(R.id.buttonTaskInfo).setOnClickListener(v -> showTaskInfoDialog());
    }

    private void showTaskInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Разработала Сологуб ПО-11");
        builder.setMessage("Задача: Практическое задание\n" +
                "Одним из доступных способов заранее подготовьте базу данных. База данных, содержащая менее 20 записей будет считаться отсутсвующей.\n" +
                "1. Разработать приложение MyNotes, представляющее собой View Pager.\n" +
                "2. Поместить в View Pager четыре фрагмента: FragmentShow, FragmentAdd, FragmentDel, FragmentUpdate.\n" +
                "3. В View Pager добавить верхнее меню вкладок (PagerTabStrip) с заголовками Show, Add, Del, Update.\n" +
                "4. Во фрагменте FragmentShow реализовать кастомизированный список заметок ListView с помощью собственного адаптера.\n" +
                "5. В каждом пункте списка отобразить следующую информацию о заметке пользователя: номер, описание заметки.\n" +
                "6. Хранение, а также предоставление информации о заметках адаптеру реализовать с помощью базы данных SQLite.\n" +
                "7. Во фрагменте FragmentAdd реализовать функционал добавления новой заметки посредством ввода описания заметки в поле EditText и добавления информации в базу данных SQLite по нажатию кнопки Add.\n" +
                "8. Во фрагменте FragmentDel реализовать функционал удаления новой заметки посредством ввода ее номера в поле EditText и удаления информации из базы данных SQLite по нажатию кнопки Del.\n" +
                "9. Во фрагменте FragmentUpdate реализовать функционал обновления существующей заметки посредством ввода ее номера в поле EditText, ввода нового описания в поле EditText и обновления информации в базе данных SQLite по нажатию кнопки Update.\n" +
                "10. База данных, содержащая менее 20 записей будет считаться отсутсвующей.\n");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}