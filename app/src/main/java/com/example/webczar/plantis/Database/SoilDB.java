package com.example.webczar.plantis.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.webczar.plantis.Structure.ListSoilData;
import com.example.webczar.plantis.Structure.SoilData;

/**
 * Created by webczar on 4/6/2018.
 */

public class SoilDB {
    private static SoilDBHelper soilDBHelper = null;
    private static SoilDB instance = null;

    public SoilDB() {
    }

    private static class SoilDBHelper extends SQLiteOpenHelper {
        static final int DATABASE_VERSION = 5;
        static final String DATABASE_NAME = "SoilData.db";


        public SoilDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           onUpgrade(db,oldVersion,newVersion);
        }
    }

    public static SoilDB getInstance(Context context){
        if (instance == null){
            soilDBHelper = new SoilDBHelper(context);
            instance = new SoilDB();
        }
        return instance;
    }

    public class ContractDB implements BaseColumns {
        static final String TABLE_NAME = "soil";
        static final String COLUMN_NAME_MOISTURE = "moisture";
        static final String COLUMN_NAME_SOILTEMP = "soiltemp";
        static final String COLUMN_NAME_PH = "ph";
        static final String COLUMN_NAME_WEATHER = "weather";
    }

    public long addSoil(SoilData soilData){
        SQLiteDatabase database = soilDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ContractDB.COLUMN_NAME_MOISTURE, soilData.moisture);
        values.put(ContractDB.COLUMN_NAME_SOILTEMP, soilData.soilTemp);
        values.put(ContractDB.COLUMN_NAME_PH, soilData.ph);
        values.put(ContractDB.COLUMN_NAME_WEATHER, soilData.weather);
        // Insert the new row, returning the primary key value of the new row
        return database.insert(ContractDB.TABLE_NAME, null, values);
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ContractDB.TABLE_NAME + " (" +
                    ContractDB.COLUMN_NAME_MOISTURE + TEXT_TYPE + COMMA_SEP +
                    ContractDB.COLUMN_NAME_SOILTEMP + TEXT_TYPE + COMMA_SEP +
                    ContractDB.COLUMN_NAME_PH + TEXT_TYPE + COMMA_SEP +
                    ContractDB.COLUMN_NAME_WEATHER+ TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContractDB.TABLE_NAME;


    public void dropDB(){
        SQLiteDatabase db = soilDBHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public ListSoilData getListSoil ( ){

        ListSoilData listSoilData = new ListSoilData();
        SQLiteDatabase database = soilDBHelper.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery("select * from " + ContractDB.TABLE_NAME, null);
            while (cursor.moveToNext()){
            SoilData soilData = new SoilData();
            soilData.moisture = cursor.getString(0);
            soilData.soilTemp = cursor.getString(1);
            soilData.ph = cursor.getString(2);
            soilData.weather = cursor.getString(3);
            listSoilData.getListSoilData().add(soilData);
        }
        cursor.close();

       }catch (Exception e){
           return new ListSoilData();
       }
        return listSoilData;
    }

    public String getMoistData(){
        SQLiteDatabase database = soilDBHelper.getReadableDatabase();
        String soilMoist = null;
        try{
            Cursor cursor = database.rawQuery("select * from " + ContractDB.TABLE_NAME, null);
            while (cursor.moveToLast()){
                SoilData soilData = new SoilData();
                soilData.moisture = cursor.getString(0);
                 soilMoist= soilData.moisture;
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return soilMoist;
    }
    public String getsoilTempData() {
        SQLiteDatabase database = soilDBHelper.getReadableDatabase();
        String soilTemp = null;
        try {
            Cursor cursor = database.rawQuery("select * from " + ContractDB.TABLE_NAME, null);
            while (cursor.moveToLast()) {
                SoilData soilData = new SoilData();
                soilData.moisture = cursor.getString(0);
                soilTemp = soilData.moisture;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soilTemp;
    }

    public String getpHData() {
        SQLiteDatabase database = soilDBHelper.getReadableDatabase();
        String soilpH = null;
        try {
            Cursor cursor = database.rawQuery("select * from " + ContractDB.TABLE_NAME, null);
            while (cursor.moveToLast()) {
                SoilData soilData = new SoilData();
                soilData.moisture = cursor.getString(0);
                soilpH = soilData.moisture;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soilpH;
    }

    public String getweatherData() {
        SQLiteDatabase database = soilDBHelper.getReadableDatabase();
        String weather = null;
        try {
            Cursor cursor = database.rawQuery("select * from " + ContractDB.TABLE_NAME, null);
            while (cursor.moveToLast()) {
                SoilData soilData = new SoilData();
                soilData.moisture = cursor.getString(0);
                weather = soilData.moisture;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weather;
    }
}
