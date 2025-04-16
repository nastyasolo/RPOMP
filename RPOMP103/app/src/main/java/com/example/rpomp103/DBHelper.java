package com.example.rpomp103;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mynotes.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NOTES + " ADD COLUMN title TEXT;");
        }
    }

    public void addNote(String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        long result = db.insert(TABLE_NOTES, null, values);
        if (result == -1) {
            Log.e("DBHelper", "Ошибка при добавлении заметки");
        } else {
            Log.d("DBHelper", "Заметка добавлена, ID: " + result);
        }
        db.close();
    }

    // Метод для получения всех заметок
    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NOTES, null, null, null, null, null, null);
    }

    // Метод для обновления заметки
    public int updateNote(int id, String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        int rowsAffected = db.update(TABLE_NOTES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        Log.d("DBHelper", "Заметка обновлена, строк изменено: " + rowsAffected);
        return rowsAffected;
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        Log.d("DBHelper", "Заметка удалена, строк удалено: " + rowsDeleted);
    }

    public Cursor searchNotesByTitle(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_NOTES,
                null,
                COLUMN_TITLE + " LIKE ?",
                new String[]{"%" + query + "%"},
                null,
                null,
                null
        );
    }

    // Метод для сортировки заметок по ID
    public Cursor getAllNotesSortedById() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_NOTES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ID + " ASC"
        );
    }

    // Метод для сортировки заметок по названию
    public Cursor getAllNotesSortedByTitle() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_NOTES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_TITLE + " ASC"
        );
    }
}