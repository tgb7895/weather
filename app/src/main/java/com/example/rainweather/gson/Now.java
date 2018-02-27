package com.example.rainweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 37046 on 2018/2/27.
 */

public class Now {

    @SerializedName("cond_txt")
    public String info;

    @SerializedName("tmp")
    public String temperature;



}
