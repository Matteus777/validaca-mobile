package com.example.validacao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CreateDB extends SQLiteOpenHelper {

    public CreateDB(Context context) {
        super(context, "validacao", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createClienteTable =    "CREATE TABLE IF NOT EXISTS cliente (id integer primary key," +
                "name text," +
                "document text," +
                "email text," +
                "phone text," +
                "responsiblePerson text," +
                " internalResponsibleId integer," +
                "FOREIGN KEY (internalResponsibleId) REFERENCES usuario (id));";

        String createUsuarioTable = "CREATE TABLE IF NOT EXISTS usuario (id integer primary key," +
                "name text);";
    sqLiteDatabase.execSQL(createUsuarioTable);
    sqLiteDatabase.execSQL(createClienteTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
