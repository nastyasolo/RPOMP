package com.example.rpomp103;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentShow extends Fragment {
    private ListView listView;
    private DBHelper dbHelper;
    private NotesAdapter adapter;
    private EditText editTextSearch;
    private Button buttonSortById, buttonSortByTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        listView = view.findViewById(R.id.listView);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonSortById = view.findViewById(R.id.buttonSortById);
        buttonSortByTitle = view.findViewById(R.id.buttonSortByTitle);

        dbHelper = new DBHelper(getActivity());

        // Инициализация адаптера
        refreshList();

        // Обработчик для поиска
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Обработчик для сортировки по ID
        buttonSortById.setOnClickListener(v -> {
            Cursor cursor = dbHelper.getAllNotesSortedById();
            adapter.swapCursor(cursor);
            adapter.notifyDataSetChanged();
            Log.d("FragmentShow", "Список отсортирован по ID");
        });

        // Обработчик для сортировки по названию
        buttonSortByTitle.setOnClickListener(v -> {
            Cursor cursor = dbHelper.getAllNotesSortedByTitle();
            adapter.swapCursor(cursor);
            adapter.notifyDataSetChanged();
            Log.d("FragmentShow", "Список отсортирован по названию");
        });

        return view;
    }

    // Метод для обновления списка
    public void refreshList() {
        Cursor cursor = dbHelper.getAllNotesSortedById(); // По умолчанию сортируем по ID
        if (adapter == null) {
            adapter = new NotesAdapter(getActivity(), cursor);
            listView.setAdapter(adapter);
            Log.d("FragmentShow", "Адаптер инициализирован");
        } else {
            adapter.swapCursor(cursor);
            adapter.notifyDataSetChanged();
            Log.d("FragmentShow", "Список обновлен, курсор заменен");
        }
    }

    // Метод для поиска заметок по названию
    private void searchNotes(String query) {
        Cursor cursor = dbHelper.searchNotesByTitle(query);
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
        Log.d("FragmentShow", "Выполнен поиск по запросу: " + query);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FragmentShow", "Фрагмент возобновлен");
        refreshList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("FragmentShow", "Фрагмент видим: " + isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            refreshList();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.closeCursor();
        }
        dbHelper.close();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }
}