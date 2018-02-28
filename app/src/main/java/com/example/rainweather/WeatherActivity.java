package com.example.rainweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rainweather.gson.Weather;
import com.example.rainweather.util.HttpUtil;
import com.example.rainweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 37046 on 2018/2/27.
 */

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView srText;
    private TextView ssText;
    private TextView comfortText;
    private TextView drsgText;
    private TextView sportText;
    private ImageView bingPicImg;

    /**
     * 初始化控件
     */
    private void initControl() {
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        srText = findViewById(R.id.sr_text);
        ssText = findViewById(R.id.ss_text);
        comfortText = findViewById(R.id.comfor_text);
        drsgText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 版本判断是否用图片  大于安卓5.0才可以使用
         */
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initControl();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);

        } else {
            loadBingPic();
        }


        if (weatherString != null) {
            //有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);

        } else {
            //无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }


    }

    /**
     * 加载每日一图
     */

    private void loadBingPic() {
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "https://free-api.heweather.com/s6/weather?location="
                + weatherId + "&key=e4264477e3874b968d869e7008b234a9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败啦1", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.getHeWeather6().get(0).getStatus())) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);

                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败啦2", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        loadBingPic();
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {

        Weather.HeWeather6Bean heWeather6Bean = weather.getHeWeather6().get(0);

        Weather.HeWeather6Bean.BasicBean basic = heWeather6Bean.getBasic();
        Weather.HeWeather6Bean.UpdateBean update = heWeather6Bean.getUpdate();
        Weather.HeWeather6Bean.NowBean now = heWeather6Bean.getNow();
        List<Weather.HeWeather6Bean.DailyForecastBean> daily_forecast = heWeather6Bean.getDaily_forecast();
        List<Weather.HeWeather6Bean.LifestyleBean> lifestyle = heWeather6Bean.getLifestyle();

        /**
         * 当前天气情况
         */
        String cityName = basic.getLocation();
        String updateTime=update.getLoc().split(" ")[1];
        String degree=now.getTmp()+ "℃";  //当前温度
        String weatherInfo=now.getCond_txt();  //多云


        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();  //调用此方法从ViewGroup删除所有子视图

        /**
         * 日出日落时间
         */
        String sr = daily_forecast.get(0).getSr();
        String ss = daily_forecast.get(0).getSs();
        srText.setText(sr);
        ssText.setText(ss);


        /**
         * 预报
         */
        for (Weather.HeWeather6Bean.DailyForecastBean forecast:daily_forecast){


            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);

            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(forecast.getDate());
            infoText.setText(forecast.getCond_txt_d());
            maxText.setText(forecast.getTmp_max());
            minText.setText(forecast.getTmp_min());

            forecastLayout.addView(view);

        }


        String comf = "舒适度:" + lifestyle.get(0).getTxt();
        String drsg = "穿衣指数:" + lifestyle.get(1).getTxt();
        String sport = "运动指数:" + lifestyle.get(3).getTxt();

        comfortText.setText(comf);
        drsgText.setText(drsg);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
