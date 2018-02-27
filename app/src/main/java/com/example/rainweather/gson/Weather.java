package com.example.rainweather.gson;

import android.view.textservice.SuggestionsInfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 37046 on 2018/2/27.
 */

public class Weather {
    public String status;
    public Basic basic;
    public Now now;

    public Update update;

    @SerializedName("lifestyle")
    public List<LifeStyle> lifeStyleList;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
