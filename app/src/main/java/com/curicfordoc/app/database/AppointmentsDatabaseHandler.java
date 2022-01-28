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

public class AppointmentsDatabaseHandler extends SQLiteOpenHelper {

    public AppointmentsDatabaseHandler(Context context) {
        super(context, Params.APPOINTMENTS_DETAILS_DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + Params.APPOINTMENTS_DETAILS_DB_TABLE_NAME + "(" + Params.KEY_ORDER_ID +
                " TEXT PRIMARY KEY," + Params.KEY_DOCTOR_ID + " TEXT," + Params.KEY_PAYMENT_ID +
                " TEXT," + Params.KEY_APPOINTMENT_TIME + " TEXT," + Params.KEY_APPOINTMENT_DATE +
                " TEXT," + Params.KEY_PATIENT_NAME + " TEXT," + Params.KEY_PATIENT_AGE +
                " TEXT," + Params.KEY_PATIENT_GENDER + " TEXT," + Params.KEY_PATIENT_CONTACT +
                " TEXT," + Params.KEY_PATIENT_EMAIL + " TEXT," + Params.KEY_PATIENT_ADDRESS +
                " TEXT," + Params.KEY_DOCTOR_FEE + " TEXT," + Params.KEY_PATIENT_LOGIN_METHOD +
                " TEXT," + Params.KEY_PATIENT_LOGIN_ID + " TEXT" + ")";

        Log.d("DBShuAppoint", create);
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addAppointment(AppointmentDetail details){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Params.KEY_ORDER_ID, details.getOrderId());
        values.put(Params.KEY_DOCTOR_ID, details.getDocId());
        values.put(Params.KEY_PAYMENT_ID,details.getPaymentId());
        values.put(Params.KEY_APPOINTMENT_TIME, details.getAppointmentTime());
        values.put(Params.KEY_APPOINTMENT_DATE, details.getAppointmentDate());
        values.put(Params.KEY_PATIENT_NAME, details.getPatientName());
        values.put(Params.KEY_PATIENT_AGE, details.getPatientAge());
        values.put(Params.KEY_PATIENT_GENDER, details.getPatientGender());
        values.put(Params.KEY_PATIENT_CONTACT, details.getPatientContact());
        values.put(Params.KEY_PATIENT_EMAIL, details.getPatientEmail());
        values.put(Params.KEY_PATIENT_ADDRESS, details.getPatientAddress());
        values.put(Params.KEY_DOCTOR_FEE, details.getDoctorFee());
        values.put(Params.KEY_PATIENT_LOGIN_METHOD, details.getPatientLoginMethod());
        values.put(Params.KEY_PATIENT_LOGIN_ID, details.getPatientLoginId());

        Log.d("DBShuAppoint", "Table Created");
        db.insert(Params.APPOINTMENTS_DETAILS_DB_TABLE_NAME, null, values);
        db.close();
    }

    public List<AppointmentDetail> getAllAppointments() throws Exception{
        List<AppointmentDetail> appointmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Params.APPOINTMENTS_DETAILS_DB_TABLE_NAME;
        Cursor cursor = db.rawQuery(select, null);

        if (cursor.moveToFirst()) {
            do {
                AppointmentDetail appointmentDetails = new AppointmentDetail();
                appointmentDetails.setOrderId(cursor.getString(0));
                appointmentDetails.setDocId(cursor.getString(1));
                appointmentDetails.setPaymentId(cursor.getString(2));
                appointmentDetails.setAppointmentTime(cursor.getString(3));
                appointmentDetails.setAppointmentDate(cursor.getString(4));
                appointmentDetails.setPatientName(cursor.getString(5));
                appointmentDetails.setPatientAge(cursor.getString(6));
                appointmentDetails.setPatientGender(cursor.getString(7));
                appointmentDetails.setPatientContact(cursor.getString(8));
                appointmentDetails.setPatientAddress(cursor.getString(9));
                appointmentDetails.setDoctorFee(cursor.getString(10));
                appointmentDetails.setPatientLoginMethod(cursor.getString(11));
                appointmentDetails.setPatientLoginId(cursor.getString(12));

                appointmentList.add(appointmentDetails);
            } while (cursor.moveToNext());
        }

        return appointmentList;
    }

    public AppointmentDetail getAppointmentDetail(String OrderID) throws Exception{
        AppointmentDetail detail = new AppointmentDetail();
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Params.APPOINTMENTS_DETAILS_DB_TABLE_NAME +
                " WHERE " + Params.KEY_ID + " = " + OrderID;
        Cursor cursor = db.rawQuery(select, null);

        cursor.moveToFirst();

        detail.setOrderId(cursor.getString(0));
        detail.setDocId(cursor.getString(1));
        detail.setPaymentId(cursor.getString(2));
        detail.setAppointmentTime(cursor.getString(3));
        detail.setAppointmentDate(cursor.getString(4));
        detail.setPatientName(cursor.getString(5));
        detail.setPatientAge(cursor.getString(6));
        detail.setPatientGender(cursor.getString(7));
        detail.setPatientContact(cursor.getString(8));
        detail.setPatientAddress(cursor.getString(9));
        detail.setDoctorFee(cursor.getString(10));
        detail.setPatientLoginMethod(cursor.getString(11));
        detail.setPatientLoginId(cursor.getString(12));

        return detail;
    }
}































