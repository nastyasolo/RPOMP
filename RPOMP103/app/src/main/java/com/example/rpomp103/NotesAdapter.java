package com.example.rpomp103;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class NotesAdapter extends CursorAdapter {
    public NotesAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewId = view.findViewById(R.id.textViewId);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        Button buttonExpand = view.findViewById(R.id.buttonExpand);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION));

        textViewId.setText("ID: " + id);
        textViewTitle.setText("Название: " + title);
        textViewDescription.setText("Описание: " + description);


        buttonExpand.setOnClickListener(v -> {
            if (textViewDescription.getMaxLines() == 2) {
                textViewDescription.setMaxLines(Integer.MAX_VALUE);
                buttonExpand.setText("Свернуть");
            } else {
                textViewDescription.setMaxLines(2);
                buttonExpand.setText("Развернуть");
            }
        });
    }
    public void closeCursor() {
        if (getCursor() != null) {
            getCursor().close();
        }
    }
}