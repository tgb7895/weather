package com.example.rainweather.gson;

import android.support.v4.media.session.MediaSessionCompat;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 37046 on 2018/2/27.
 */

public class Forecast {

    @SerializedName("date")
    public String date;

    @SerializedName("tmp_max")
    public String tmp_max;

    @SerializedName("tmp_min")
    public String tmp_min;


    @SerializedName("cond_txt_n")
    public String info;



}
