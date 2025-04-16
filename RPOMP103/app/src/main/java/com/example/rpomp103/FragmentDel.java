package com.example.rpomp103;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {
    private EditText editTextId;
    private Button buttonDel;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        editTextId = view.findViewById(R.id.editTextId);
        buttonDel = view.findViewById(R.id.buttonDel);

        dbHelper = new DBHelper(getActivity());

        buttonDel.setOnClickListener(v -> {
            String id = editTextId.getText().toString();

            if (!id.isEmpty()) {
                // Удаляем заметку синхронно
                dbHelper.deleteNote(Integer.parseInt(id));
                Toast.makeText(getActivity(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                editTextId.setText("");

                // Обновляем список в FragmentShow
                FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("Show");
                if (fragmentShow != null && fragmentShow.isVisible()) {
                    fragmentShow.refreshList();
                }
            } else {
                Toast.makeText(getActivity(), "Введите ID", Toast.LENGTH_SHORT).show();
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