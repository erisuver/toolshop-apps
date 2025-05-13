package com.orion.sinar_surya;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.orion.sinar_surya.R;

import java.util.UUID;

public class DBConn extends SQLiteOpenHelper {
    Context ctx;
    public DBConn(Context context) {
        super(context, context.getString(R.string.database_name), null, 1);
        ctx = context;
    }


    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE IF NOT EXISTS login(" +
                        "user_id varchar(255), " +
                        "password varchar(255), " +
                        "database_id integer " +
                     ")";
        sqLiteDatabase.execSQL(sql);


        sql = "CREATE TABLE IF NOT EXISTS last_user_login(user_id varchar(255))";
        sqLiteDatabase.execSQL(sql);
    }


    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int version, int newVersion) {
        String sql ="";

        if (version <= newVersion) {
            try {
                //untuk nambah field
            } catch (SQLiteException ex) {
                //lakukan sesuatu, atau kosongin aja is fine
            }
        }
    }

    public String getDatabaseLocation() {
        String location = ctx.getDatabasePath(ctx.getString(R.string.database_name)).toString();
        return location;
    }

}
