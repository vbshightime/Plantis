package com.example.webczar.plantis;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webczar.plantis.Database.SoilDB;
import com.example.webczar.plantis.SharedPreferanceHelper.SharedPreferanceHelper;
import com.example.webczar.plantis.Structure.ListSoilData;
import com.example.webczar.plantis.Structure.SoilQual;

import java.util.ArrayList;

public class RunWater extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button btn_water;
    private TextView tv_moist, tv_soilTemp, tv_weather, tv_pH;
    private ListView listView;
    private ListSoilData listSoilData = null;
    private ArrayList<String> moistDataList = null;
    private boolean isNull = false;
   private Bluetooth bluetooth;
    private Handler hihandle;
    private Spinner spinner;
    //private Toolbar toolbar;
    //private ActionBar actionBar;
    private boolean isButonClicked = false;

    private static final String TAG = RunWater.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_water);
        btn_water = findViewById(R.id.btn_runWater_id);
        tv_moist = findViewById(R.id.tv_moist_info_id);
        tv_soilTemp = findViewById(R.id.tv_soilTemp_info_id);
        tv_weather = findViewById(R.id.tv_weather_info_id);
        tv_pH = findViewById(R.id.tv_ph_info_id);
        spinner = findViewById(R.id.spin_state_id);
      //  toolbar = findViewById(R.id.toolbar_runWater_id);

        //listView = findViewById(R.id.lv_id);

        BluetoothAdapter btAdapt = BluetoothAdapter.getDefaultAdapter();
        bluetooth = new Bluetooth(getApplicationContext(), handler);
        connectingBT(btAdapt,bluetooth);
        final SharedPreferanceHelper sharedPreferanceHelper = SharedPreferanceHelper.getInstance(this);

        tv_moist.setText(sharedPreferanceHelper.getUserSavedValue().moisture);
        tv_soilTemp.setText(sharedPreferanceHelper.getUserSavedValue().soilTemp);
        tv_weather.setText(sharedPreferanceHelper.getUserSavedValue().weather);
        tv_pH.setText(sharedPreferanceHelper.getUserSavedValue().ph);

        btn_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String waterOn = "";
                bluetooth.sendOnWaterMessage(waterOn);
                if (v.getId() == R.id.btn_runWater_id) {
                    isButonClicked = !isButonClicked;
                    v.setBackgroundResource(isButonClicked ? R.drawable.drop_off : R.drawable.drop_on);
                    runWater();
                }
            }
        });

        /*toolbar.setTitle(RunWater.class.getSimpleName());
        actionBar = getSupportActionBar();
        setSupportActionBar(toolbar);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
*/
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_enteries, android.R.layout.simple_expandable_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void runWater() {
        String moisture = SharedPreferanceHelper.getInstance(this).getUserSavedValue().moisture;

        int moist = Integer.parseInt(moisture);
        moist *= 1000;
        int i = 150000 - moist;
        hihandle = new Handler();
        hihandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                String waterOn = "";
                bluetooth.sendOffWaterMessage(waterOn);
                btn_water.setBackgroundResource(R.drawable.drop_on);
            }
        }, i);
    }


  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            bluetooth.stop();
            startActivity(new Intent(RunWater.this, NavigateActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bluetooth.stop();
        startActivity(new Intent(RunWater.this, NavigateActivity.class));
        finish();
    }

*/
  private void connectingBT(BluetoothAdapter btAdapt, Bluetooth bluetooth) {

                if (btAdapt.isEnabled()==true){
                    Log.d(TAG, String.valueOf(btAdapt.isEnabled()));
                    this.bluetooth = bluetooth;
                    bluetooth.start();
                    bluetooth.connectDevice("HC-05");
                }
            }


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST " + msg);
                    Toast.makeText(getApplicationContext(), msg.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String smr = "Soil Moisture Regime-";
        String str = "Soil Temperature Regime-";
        String country = null;
        String smrData = null;
        String strData = null;
        String smrinfo[] = {"Aridic", "Ustic", "Xeric", "Udic"};
        String strinfo[] = {"Cryic", "Frigid", "Mesic", "Thermic", "Hyperthermic", "isothermic", "isohyperthermic"};
        SoilQual soilQual = new SoilQual();
        SharedPreferanceHelper sharedPreferanceHelper = SharedPreferanceHelper.getInstance(this);
        StringBuilder stringBuilder = new StringBuilder();

        switch (position) {

            case 0:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[3] + "\n");
                country = "Arunanchal Pradesh";
                smrData = smrinfo[3];
                strData = strinfo[2];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);
                break;
            case 1:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Assam";
                smrData = smrinfo[3];
                strData = strinfo[4];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 2:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Bihar";
                smrData = smrinfo[1];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                strData = strinfo[4];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 3:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Chattisgarh";
                smrData = smrinfo[1];
                strData = strinfo[4];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);
                break;
            case 4:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[6] + "\n");
                country = "Goa";
                smrData = smrinfo[1];
                strData = strinfo[6];
                soilQual.county = country;
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 5:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Gujrat";
                smrData = smrinfo[1];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                strData = strinfo[4];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 6:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Haryana";
                smrData = smrinfo[1];
                strData = strinfo[4];
                soilQual.county = country;
                soilQual.smr = smrData;
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 7:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[3] + "\n");
                country = "Himachal Pradesh";
                smrData = smrinfo[3];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                strData = strinfo[3];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);
                break;
            case 8:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[3] + "\n");
                country = "Jammu and Kashmir";
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                smrData = smrinfo[3];
                strData = strinfo[3];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 9:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Jharkhand";
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                smrData = smrinfo[1];
                strData = strinfo[4];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 10:
                stringBuilder.append(smr + smrinfo[0] + "\n");
                stringBuilder.append(str + strinfo[6] + "\n");
                country = "Karnataka";
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                smrData = smrinfo[0];
                strData = strinfo[6];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 11:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[6] + "\n");
                country = "Kerala";
                smrData = smrinfo[1];
                strData = strinfo[6];
                soilQual.county = country;
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 12:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Madhaya Pradesh";
                smrData = smrinfo[1];
                strData = strinfo[4];
                soilQual.county = country;
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 13:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[6] + "\n");
                country = "Maharashtra";
                smrData = smrinfo[1];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                strData = strinfo[6];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 14:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[3] + "\n");
                country = "Manipur";
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                smrData = smrinfo[3];
                strData = strinfo[3];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 15:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[3] + "\n");
                country = "Meghalaya";
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                smrData = smrinfo[3];
                strData = strinfo[2];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 16:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[3] + "\n");
                country = "Mizoram";
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                smrData = smrinfo[3];
                strData = strinfo[2];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 17:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[2] + "\n");
                country = "Nagaland";
                smrData = smrinfo[3];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                strData = strinfo[3];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 18:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[6] + "\n");
                country = "Orrisa";
                smrData = smrinfo[1];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                strData = strinfo[6];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 19:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Punjab";
                smrData = smrinfo[1];
                strData = strinfo[4];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 20:
                stringBuilder.append(smr + smrinfo[0] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Rajasthan";
                smrData = smrinfo[0];
                strData = strinfo[4];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 21:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[3] + "\n");
                country = "Sikkim";
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                smrData = smrinfo[3];
                strData = strinfo[3];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 22:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[6] + "\n");
                country = "Tamil Nadu";
                smrData = smrinfo[1];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                strData = strinfo[6];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 23:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[6] + "\n");
                country = "Telangana";
                smrData = smrinfo[1];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                strData = strinfo[6];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 24:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[6] + "\n");
                country = "Tripura";
                smrData = smrinfo[3];
                strData = strinfo[6];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 25:
                stringBuilder.append(smr + smrinfo[3] + "\n");
                stringBuilder.append(str + strinfo[3] + "\n");
                country = "Uttarakhand";
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                smrData = smrinfo[3];
                strData = strinfo[3];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 26:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "Uttar Pradesh";
                smrData = smrinfo[1];
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                strData = strinfo[4];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);

                break;
            case 27:
                stringBuilder.append(smr + smrinfo[1] + "\n");
                stringBuilder.append(str + strinfo[4] + "\n");
                country = "West Bengal";
                smrData = smrinfo[1];
                strData = strinfo[4];
                soilQual.county = country;
                soilQual.smr = smrData;
                soilQual.str = strData;
                Toast.makeText(this, stringBuilder, Toast.LENGTH_LONG).show();
                sharedPreferanceHelper.saveSoilQualInfo(soilQual);
                break;

            default:
                country = sharedPreferanceHelper.getUserSavedSoilValue().county;
                smrData = sharedPreferanceHelper.getUserSavedSoilValue().smr;
                strData = sharedPreferanceHelper.getUserSavedSoilValue().str;
                Toast.makeText(this, smr + smrData + "\n" + str + strData, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}