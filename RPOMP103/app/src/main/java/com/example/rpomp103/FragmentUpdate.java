package com.example.rpomp103;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {
    private EditText editTextId, editTextTitle, editTextDescription;
    private Button buttonUpdate;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        editTextId = view.findViewById(R.id.editTextId);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);

        dbHelper = new DBHelper(getActivity());

        buttonUpdate.setOnClickListener(v -> {
            String id = editTextId.getText().toString();
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();

            if (!id.isEmpty() && !title.isEmpty() && !description.isEmpty()) {
                // Обновляем заметку синхронно
                int rowsAffected = dbHelper.updateNote(Integer.parseInt(id), title, description);
                if (rowsAffected > 0) {
                    Toast.makeText(getActivity(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
                    editTextId.setText("");
                    editTextTitle.setText("");
                    editTextDescription.setText("");

                    // Обновляем список в FragmentShow
                    FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("Show");
                    if (fragmentShow != null && fragmentShow.isVisible()) {
                        fragmentShow.refreshList();
                        Log.d("FragmentUpdate", "Список обновлен в FragmentShow");
                    } else {
                        Log.d("FragmentUpdate", "FragmentShow не найден, но данные обновлены");
                    }
                } else {
                    Toast.makeText(getActivity(), "Заметка с таким ID не найдена", Toast.LENGTH_SHORT).show();
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