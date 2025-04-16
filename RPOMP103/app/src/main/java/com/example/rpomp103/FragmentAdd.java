package com.example.rpomp103;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {
    private EditText editTextTitle, editTextDescription;
    private Button buttonAdd;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        dbHelper = new DBHelper(getActivity());

        buttonAdd.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();

            if (!title.isEmpty() && !description.isEmpty()) {
                // Добавляем заметку синхронно
                dbHelper.addNote(title, description);
                Toast.makeText(getActivity(), "Заметка добавлена", Toast.LENGTH_SHORT).show();
                editTextTitle.setText("");
                editTextDescription.setText("");

                // Обновляем список в FragmentShow
                FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("Show");
                if (fragmentShow != null && fragmentShow.isVisible()) {
                    fragmentShow.refreshList();
                }
            } else {
                Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbHelper.close();
    }
}