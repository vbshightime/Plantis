package com.example.webczar.plantis.SharedPreferanceHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.webczar.plantis.Structure.SoilData;
import com.example.webczar.plantis.Structure.SoilQual;

/**
 * Created by webczar on 4/7/2018.
 */

public class SharedPreferanceHelper {

    private static SharedPreferanceHelper instance = null;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static String MY_PREFERENCES = "preferances";
    private static String SHARE_KEY_MOISTURE = "moisture";
    private static String SHARE_KEY_TEMP = "temp";
    private static String SHARE_KEY_PH = "pH";
    private static String SHARE_KEY_WEATHER = "weather";
    private static String SHARE_KEY_STATE;
    private static String SHARE_KEY_SMR;
    private static String SHARE_KEY_STR;
    public SharedPreferanceHelper() {
    }

    public static SharedPreferanceHelper getInstance(Context context) {

        if (instance == null){
            instance = new SharedPreferanceHelper();
            sharedPreferences = context.getSharedPreferences(MY_PREFERENCES,context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return instance;
    }

    public void saveUserInfo(SoilData soilData){

        editor.putString(SHARE_KEY_MOISTURE, soilData.moisture);
        editor.putString(SHARE_KEY_TEMP, soilData.soilTemp);
        editor.putString(SHARE_KEY_PH,soilData.ph);
        editor.putString(SHARE_KEY_WEATHER, soilData.weather);
        editor.apply();
    }

    public SoilData getUserSavedValue(){
        String moisture = sharedPreferences.getString(SHARE_KEY_MOISTURE, "");
        String soilTemp = sharedPreferences.getString(SHARE_KEY_TEMP, "");
        String ph = sharedPreferences.getString(SHARE_KEY_PH, "");
        String weather = sharedPreferences.getString(SHARE_KEY_WEATHER,"");



        SoilData soilData = new SoilData();
        soilData.moisture = moisture;
        soilData.soilTemp = soilTemp;
        soilData.ph = ph;
        soilData.weather = weather;

        return soilData;
    }


    public void saveSoilQualInfo(SoilQual soilqual){

        editor.putString(SHARE_KEY_STATE, soilqual.county);
        editor.putString(SHARE_KEY_SMR, soilqual.smr);
        editor.putString(SHARE_KEY_STR,soilqual.str);
        editor.apply();
    }


    public SoilQual getUserSavedSoilValue(){
        String country = sharedPreferences.getString(SHARE_KEY_STATE, "");
        String smr = sharedPreferences.getString(SHARE_KEY_SMR, "");
        String str = sharedPreferences.getString(SHARE_KEY_STR, "");


        SoilQual soilQual = new SoilQual();
        soilQual.county = country;
        soilQual.smr = smr;
        soilQual.str = str;

        return soilQual;
    }
}
