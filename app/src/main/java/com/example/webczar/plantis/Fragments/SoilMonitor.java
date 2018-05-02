package com.example.webczar.plantis.Fragments;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webczar.plantis.Bluetooth;
import com.example.webczar.plantis.Database.SoilDB;
import com.example.webczar.plantis.NavigateActivity;
import com.example.webczar.plantis.R;
import com.example.webczar.plantis.RunWater;
import com.example.webczar.plantis.SharedPreferanceHelper.SharedPreferanceHelper;
import com.example.webczar.plantis.Structure.SoilData;


/**
 * A simple {@link Fragment} subclass.
 */
public class SoilMonitor extends Fragment implements View.OnClickListener  {

    private static final String TAG = "SoilMonitor" ;
    private TextView recieved_temp;

    private TextView recieved_moist;
    private Button btn_blue,btn_soilrefresh,btn_moistrefresh,btn_temprefresh;
    private Bluetooth bt;
    private BluetoothAdapter bluetoothAdapter;
    private String temp_Values;
    private String moist_Values;
  //  private Animation rotate_foward;
    private FloatingActionButton fab;
    private boolean isFabClicked = false;


    public SoilMonitor() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_soil_monitor, container, false);
        recieved_temp = view.findViewById(R.id.temp_info_id);
        recieved_moist = view.findViewById(R.id.moist_info_id);
        btn_blue = view.findViewById(R.id.btn_blue_id);
        btn_soilrefresh= view.findViewById(R.id.btn_soilrefresh_id);
        btn_moistrefresh = view.findViewById(R.id.btn_moistrefresh_id);
        btn_temprefresh = view.findViewById(R.id.btn_wthrefresh_id);
        fab = view.findViewById(R.id.floatingActionButton);
        /* rotate_foward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_forward);
*/
        btn_blue.setOnClickListener(this);

        btn_soilrefresh.setOnClickListener(this);

        btn_moistrefresh.setOnClickListener(this);

        btn_temprefresh.setOnClickListener(this);

        fab.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.btn_blue_id:
                connectBT();
                break;
            case R.id.btn_soilrefresh_id:
                soilwrite();
                break;
            case R.id.btn_moistrefresh_id:
                moistWrite();
                break;
            case R.id.btn_wthrefresh_id:
                weatherWrite();
                break;
            case R.id.floatingActionButton:
                runWater(v);
        }

    }

    private void runWater(View v) {
        Intent intent = new Intent(getContext(), RunWater.class);
       if (v.getId() == R.id.floatingActionButton){
           isFabClicked =! isFabClicked;
   //        fab.startAnimation(rotate_foward);
       }
        bt.stop();
        startActivity(intent);
    }

    private void weatherWrite() {

    }

    private void moistWrite() {
        String moist = "";
        bt.sendMoistureMessage(moist);
    }

    private void soilwrite() {
        String temp = "";
        bt.sendTemperatureMessage(temp);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bt = new Bluetooth(getContext(),mHandler);
    }

    private void connectBT() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()){
            Log.w(TAG, "Btservice started - bluetooth is not enabled");
            btn_blue.setBackgroundResource(R.drawable.ic_blue_btn2);
           } else {
            btn_blue.setBackgroundResource(R.drawable.ic_blue_btn);
            bt.start();
            bt.connectDevice("HC-05");
            Log.d(TAG, "Btservice started - listening");
            Handler handling = new Handler();
            handling.postDelayed(new Runnable() {
                @Override
                public void run() {

                    btn_blue.setBackgroundResource(R.drawable.ic_blue_btn1);

                }
            }, 4000);
        }
    }

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;
            SoilData soilData = new SoilData();
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ_TEMPERATURE:
                    String received;
                    received = new String(writeBuf);
                    received = received.substring(begin,end);
                    temp_Values = received;
                    Log.d(TAG, "soilTemp "+ soilData.soilTemp);
                    recieved_temp.setText(received );
                    Toast.makeText(getContext(),"Response"+received, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "MESSAGE_READ "+ received);
                    break;
                case Bluetooth.MESSAGE_READ_MOISTURE:
                    String received_moisture = null;
                    received_moisture = new String(writeBuf);
                    received_moisture = received_moisture.substring(begin,end);
                    moist_Values = received_moisture;
                    recieved_moist.setText(received_moisture);
                    Toast.makeText(getContext(),"Response"+received_moisture, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "MESSAGE_READ "+ received_moisture);
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME "+msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST "+msg);
                    break;
            }
            soilData.moisture = recieved_moist.getText().toString();
            soilData.soilTemp = recieved_temp.getText().toString();
            soilData.weather = "32";
            soilData.ph = "7";
            if (soilData != null){
                SoilDB.getInstance(getContext()).addSoil(soilData);
                SharedPreferanceHelper sharedPreferanceHelper = SharedPreferanceHelper.getInstance(getContext());
                sharedPreferanceHelper.saveUserInfo(soilData);
            }
        }
    };

}
