package com.curicfordoc.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DoctorsDatabaseHandler extends SQLiteOpenHelper {
    public DoctorsDatabaseHandler(Context context) {
        super(context, Params.DOCTORS_DETAILS_DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create = "CREATE TABLE " + Params.DOCTORS_DETAILS_DB_TABLE_NAME + "("
                + Params.KEY_ID + " TEXT PRIMARY KEY," + Params.KEY_NAME
                + " TEXT," + Params.KEY_SPEC + " TEXT," + Params.KEY_IMAGE
                + " TEXT," + Params.KEY_EXP + " TEXT," + Params.KEY_FEE
                + " TEXT" + ")";

        db.execSQL(Create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addDoctor(DocDetail DocDetails) {

        if (checkIfDetailsAvailable(DocDetails.getId())){
            updateDocDetails(DocDetails);
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(Params.KEY_ID, DocDetails.getId());
            values.put(Params.KEY_NAME, DocDetails.getName());
            values.put(Params.KEY_SPEC, DocDetails.getSpecialization());
            values.put(Params.KEY_EXP, DocDetails.getExperience());
            values.put(Params.KEY_IMAGE, DocDetails.getImage());
            values.put(Params.KEY_FEE, DocDetails.getFee());

            db.insert(Params.DOCTORS_DETAILS_DB_TABLE_NAME, null, values);
            db.close();
        }

    }

    public List<DocDetail> getAllDoctors() throws Exception{
        List<DocDetail> docList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Params.DOCTORS_DETAILS_DB_TABLE_NAME;
        Cursor cursor = db.rawQuery(select, null);

        if (cursor.moveToFirst()) {
            do {
                DocDetail docDetail = new DocDetail();
                docDetail.setId(cursor.getString(0));
                docDetail.setName(cursor.getString(1));
                docDetail.setSpecialization(cursor.getString(2));
                docDetail.setImage(cursor.getString(3));
                docDetail.setExperience(cursor.getString(4));
                docDetail.setFee(cursor.getString(5));

                docList.add(docDetail);
            } while (cursor.moveToNext());
        }

        return docList;
    }

    public DocDetail getDocDetail(String DocID) throws Exception{
        DocDetail detail = new DocDetail();
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Params.DOCTORS_DETAILS_DB_TABLE_NAME +
                " WHERE " + Params.KEY_ID + " = " + DocID;
        Cursor cursor = db.rawQuery(select, null);

        cursor.moveToFirst();

        detail.setId(cursor.getString(0));
        detail.setName(cursor.getString(1));
        detail.setSpecialization(cursor.getString(2));
        detail.setImage(cursor.getString(3));
        detail.setExperience(cursor.getString(4));
        detail.setFee(cursor.getString(5));

        return detail;
    }

    public boolean checkIfDetailsAvailable(String DocID){

        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Params.DOCTORS_DETAILS_DB_TABLE_NAME +
                " WHERE " + Params.KEY_ID + " = " + DocID;
        Cursor cursor = db.rawQuery(select, null);

       if (cursor.getCount() > 0)
           return true;
       else
           return false;

    }

    public void updateDocDetails(DocDetail detail){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Params.KEY_ID, detail.getId());
        values.put(Params.KEY_NAME, detail.getName());
        values.put(Params.KEY_SPEC, detail.getSpecialization());
        values.put(Params.KEY_EXP, detail.getExperience());
        values.put(Params.KEY_IMAGE, detail.getImage());
        values.put(Params.KEY_FEE, detail.getFee());

        db.update(Params.DOCTORS_DETAILS_DB_TABLE_NAME, values, Params.KEY_ID + "=?",
                            new String[]{detail.getId()});
        db.close();

        // Add int return statement to view affected rows;
    }

    public void deleteDocDetail(String DocID){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Params.DOCTORS_DETAILS_DB_TABLE_NAME, Params.KEY_ID + "=?", new String[]{DocID});
        db.close();
    }
}














