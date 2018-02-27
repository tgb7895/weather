package com.example.rainweather.util;

import android.os.CountDownTimer;
import android.text.TextUtils;

import com.example.rainweather.db.City;
import com.example.rainweather.db.County;
import com.example.rainweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 37046 on 2018/2/27.
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */

    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)) {
            try{
                JSONArray allProvinces=new JSONArray(response);

                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);

                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceID){
        if(!TextUtils.isEmpty(response)) {
            try{
                JSONArray allCity=new JSONArray(response);

                for (int i=0;i<allCity.length();i++){
                    JSONObject cityObject=allCity.getJSONObject(i);


                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceID(provinceID);
                    city.save();

                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handeCountyResponse(String response,int cityID){
        if(!TextUtils.isEmpty(response)) {
            try{
                JSONArray allCounty=new JSONArray(response);

                for (int i=0;i<allCounty.length();i++){
                    JSONObject countyObject=allCounty.getJSONObject(i);

                    County county = new County();
                    county.setCityID(cityID);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherID(countyObject.getString("weather_id"));
                    county.save();

                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
