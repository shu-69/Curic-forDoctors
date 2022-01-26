package com.curicfordoc.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(Context context) {
        super(context, Params.DOCTORS_DETAILS_DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create = "CREATE TABLE " + Params.DOCTORS_DETAILS_DB_NAME + "("
                + Params.KEY_ID + " INTEGER PRIMARY KEY," + Params.KEY_NAME
                + " TEXT," + Params.KEY_SPEC + " TEXT," + Params.KEY_IMAGE
                + " TEXT," + Params.KEY_EXP + " TEXT," + Params.KEY_FEE
                + " FEE" + ")";

        Log.d("DBShu", Create);
        db.execSQL(Create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addDoctor(DocDetail DocDetails){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Params.KEY_ID, DocDetails.getId());
        values.put(Params.KEY_NAME, DocDetails.getName());
        values.put(Params.KEY_SPEC, DocDetails.getSpecialization());
        values.put(Params.KEY_EXP, DocDetails.getExperience());
        values.put(Params.KEY_IMAGE, DocDetails.getImage());
        values.put(Params.KEY_FEE, DocDetails.getFee());

        db.insert(Params.DOCTORS_DETAILS_DB_TABLE_NAME, null, values);
        Log.d("DBShu", "Successfully Inserted");
        db.close();

    }



}














