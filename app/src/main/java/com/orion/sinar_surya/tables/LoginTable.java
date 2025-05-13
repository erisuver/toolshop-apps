package com.orion.sinar_surya.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orion.sinar_surya.DBConn;
import com.orion.sinar_surya.models.LoginModel;

import java.util.ArrayList;

/**
 * Created by user on 2/18/2018.
 */

public class LoginTable {
    private DBConn dbConn;
    private SQLiteDatabase db;
    private ArrayList<LoginModel> records;
    private Context context;

    public LoginTable(Context context, DBConn dbConn) {
        this.context = context;
        this.dbConn = dbConn;
        this.db = this.dbConn.getWritableDatabase();
        this.records = new ArrayList<LoginModel>();
    }

    private ContentValues setValues(LoginModel LoginModel){
        ContentValues cv = new ContentValues();
        cv.put("user_id", LoginModel.getUser_id());
        cv.put("password", LoginModel.getPassword());
        cv.put("database_id", LoginModel.getDatabase_id());
        return cv;
    }

    public void insert(LoginModel LoginModel) {
        this.deleteAll();
        ContentValues cv = this.setValues(LoginModel);
        this.db.insert("login", null, cv);
    }

    public void deleteAll(){
        this.db.delete("login", null, null);
        this.reloadList();
    }

    private void reloadList(){
        this.records.clear();
        Cursor cr = this.db.rawQuery("SELECT * FROM login ", null);

        LoginModel tempData;
        int idx = 0;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new LoginModel(
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getString(cr.getColumnIndexOrThrow("password")),
                        cr.getInt(cr.getColumnIndexOrThrow("database_id"))
                );
                idx = idx + 1;
                this.records.add(tempData);
            } while(cr.moveToNext());
        }
    }

    public LoginModel getLoginTableByIndex(int index){
        return this.records.get(index);
    }

    public ArrayList<LoginModel> getRecords(){
        this.reloadList();
        return this.records;
    }

    public LoginModel getLastLogin(){
        Cursor cr = this.db.rawQuery("SELECT customer_seq, api_key, tipe, user_id, is_ethica, is_seply, is_ethica_hijab, jenis FROM login ", null);

        LoginModel tempData = new LoginModel();
        int idx = 0;
        if (cr != null && cr.moveToFirst()){
            do {
                tempData = new LoginModel(
                        cr.getString(cr.getColumnIndexOrThrow("user_id")),
                        cr.getString(cr.getColumnIndexOrThrow("password")),
                        cr.getInt(cr.getColumnIndexOrThrow("database_id"))
                );
                idx = idx + 1;
                return tempData;
            } while(cr.moveToNext());
        }
        return tempData;
    }

    public ArrayList<LoginModel> getRecordsNotReload() {
        return this.records;
    }

}
