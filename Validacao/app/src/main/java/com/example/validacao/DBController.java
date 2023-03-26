package com.example.validacao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBController {

    private SQLiteDatabase db;
    private CreateDB create;

    public DBController(Context context) {
        create = new CreateDB(context);
    }

    public boolean insertClient(ClienteEntity cliente) {
        ContentValues values;
        long resultado;

        db = create.getWritableDatabase();
        values = new ContentValues();
        values.put("id", cliente.getId());
        values.put("name", cliente.getName());
        values.put("document", cliente.getDocument());
        values.put("email", cliente.getEmail());
        values.put("phone", cliente.getPhone());
        values.put("responsiblePerson", cliente.getResponsiblePerson());
        if (cliente.getInternalResponsible() != null) {
            values.put("internalResponsibleId", cliente.getInternalResponsible().getId());
        }


        resultado = db.insert("cliente", null, values);
        db.close();

        if (resultado == -1)
            return true;
        else
            return false;

    }

    public boolean insertUsuario(UsuarioEntity usuario) {
        ContentValues values;
        long resultado = -1;

        db = create.getWritableDatabase();
        values = new ContentValues();
        values.put("id", usuario.getId());
        values.put("name", usuario.getName());

        try {
            resultado = db.insertOrThrow("usuario", null, values);
        } catch (SQLiteConstraintException e) {
            System.out.println(e);
        }
        db.close();

        if (resultado == -1)
            return true;
        else
            return false;

    }
public List<UsuarioEntity> getUsuarios() {
    db = create.getReadableDatabase();

    ArrayList<UsuarioEntity> usuarioEntityArrayList
            = new ArrayList<>();

    Cursor cursorUser = db.rawQuery("SELECT * FROM usuario", null);

    if (cursorUser.moveToFirst()) {
        do {
            UsuarioEntity userEntity = new UsuarioEntity();
            userEntity.setId(cursorUser.getInt(0));
            userEntity.setName(cursorUser.getString(1));
       usuarioEntityArrayList.add(userEntity);
        } while (cursorUser.moveToNext());
    }
    return usuarioEntityArrayList;
}
    public List<ClienteEntity> getClients() {
        db = create.getReadableDatabase();

        Cursor cursor
                = db.rawQuery("SELECT * FROM cliente", null);

        ArrayList<ClienteEntity> clienteEntityArrayList
                = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                ClienteEntity clienteEntity = new ClienteEntity();
                clienteEntity.setId(cursor.getInt(0));
                clienteEntity.setName(cursor.getString(1));
                clienteEntity.setDocument(cursor.getString(2));
                clienteEntity.setEmail(cursor.getString(3));
                clienteEntity.setPhone(cursor.getString(4));
                clienteEntity.setResponsiblePerson(cursor.getString(5));
                Cursor cursorUser = db.rawQuery("SELECT * FROM usuario WHERE id = "+cursor.getInt(6), null);

                if (cursorUser.moveToFirst()) {
                    do {
                        UsuarioEntity userEntity = new UsuarioEntity();
                        userEntity.setId(cursorUser.getInt(0));
                        userEntity.setName(cursorUser.getString(1));
                        clienteEntity.setInternalResponsible(userEntity);
                    } while (cursorUser.moveToNext());

                    clienteEntityArrayList.add(clienteEntity);
                }

            } while (cursor.moveToNext());

        }
        db.close();
    return clienteEntityArrayList;
    }


    public void deleteAll(){
        db = create.getWritableDatabase();
        db.delete("cliente","",null);
        db.delete("usuario","",null);
        db.close();
    }

    public void deleteUsers(){
        db = create.getWritableDatabase();
        db.delete("usuario","",null);
        db.close();
    }
}
