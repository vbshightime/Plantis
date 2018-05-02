package com.example.webczar.plantis.Structure;

import java.util.ArrayList;

/**
 * Created by webczar on 4/7/2018.
 */

public class ListSoilData {

   public ArrayList<SoilData> listSoilData;

   public ArrayList<SoilData> getListSoilData(){
       return listSoilData;
   }

    public ListSoilData() {
        listSoilData = new ArrayList<>();
    }
}
