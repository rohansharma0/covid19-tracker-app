package com.iamrohan.covidtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    private LinearLayout safetyLayout , countryChangeLayout;
    private ImageButton safetyBtn;
    private String countryName;
    private ProgressBar progressBar;
    ScrollView scrollView;

    private ImageView countryFlagImg;

    private TextView tvCases,
            tvRecovered,
            tvActive,
            tvTodayCases,
            tvDeaths,
            tvTodayDeaths,
            tvTodayRecovered,
            tvCountryTab,
            tvGlobalTab;

    Double totalCases,
        todayCases,
        totalRecovered,
        todayRecovered,
        totalDeaths,
        todayDeaths,
        totalActive;

    Boolean isCountryTab;

    @Override
    protected void onStart() {
        super.onStart();
        isCountryTab = preferences.getBoolean("isActive" , false);
        switchTab();
    }

    @Override
    protected void onResume() {

        super.onResume();
        isCountryTab = preferences.getBoolean("isActive" , false);
        switchTab();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        //sharedPref
        preferences = getSharedPreferences("Covid19Tracker" , MODE_PRIVATE);

        //getting countryCode from sharedPref
        countryName = preferences.getString("countryName" , "India");

        // getting default tab
        isCountryTab = preferences.getBoolean("isActive" , false);

        safetyLayout = findViewById(R.id.symptomsLayout);
        safetyBtn = findViewById(R.id.symptomsBtn);

        countryChangeLayout = findViewById(R.id.countryChangeLayout);
        countryFlagImg = findViewById(R.id.countryFlagImg);

        tvCases = findViewById(R.id.tvConfirmed);
        tvTodayCases = findViewById(R.id.tvTodayConfirmed);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvTodayRecovered = findViewById(R.id.tvTodayRecovered);
        tvActive = findViewById(R.id.tvActive);
        tvDeaths = findViewById(R.id.tvDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);
        tvCountryTab = findViewById(R.id.countryTab);
        tvGlobalTab = findViewById(R.id.globalTab);

        progressBar = findViewById(R.id.progress_circular);
        scrollView = findViewById(R.id.scrollView);


//        safety Layout and btn on click goto to Symptoms Activity

        safetyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSafetyActivity();
            }
        });

        safetyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSafetyActivity();
            }
        });

        tvCountryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab();
            }
        });

        tvGlobalTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab();
            }
        });

        countryChangeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });

        fetchData("https://corona.lmao.ninja/v3/covid-19/all/");
    }

    private void switchTab(){
        if (isCountryTab ){
            tvCountryTab.setTextColor(Color.parseColor("#142237"));
            tvCountryTab.setBackgroundResource(R.drawable.country_back);
            tvGlobalTab.setTextColor(Color.parseColor("#c8815b"));
            tvGlobalTab.setBackgroundColor(Color.parseColor("#00000000"));

        }else{
            tvGlobalTab.setTextColor(Color.parseColor("#142237"));
            tvGlobalTab.setBackgroundResource(R.drawable.country_back);
            tvCountryTab.setTextColor(Color.parseColor("#c8815b"));
            tvCountryTab.setBackgroundColor(Color.parseColor("#00000000"));

        }
        SharedPreferences.Editor editor = preferences.edit();
        isCountryTab = !isCountryTab;
        editor.putBoolean("isActive" ,isCountryTab);
        editor.apply();
    }

    private void fetchData(String url) {

        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());

                            totalCases = Double.parseDouble(jsonObject.getString("cases"));
                            todayCases = Double.parseDouble(jsonObject.getString("todayCases"));
                            totalRecovered = Double.parseDouble(jsonObject.getString("recovered"));
                            todayRecovered = Double.parseDouble(jsonObject.getString("todayRecovered"));
                            totalDeaths = Double.parseDouble(jsonObject.getString("deaths"));
                            todayDeaths = Double.parseDouble(jsonObject.getString("todayDeaths"));
                            totalActive = Double.parseDouble(jsonObject.getString("active"));

                            tvCases.setText(convert(totalCases));
                            tvTodayCases.setText("+ "+convert(todayCases));
                            tvRecovered.setText(convert(totalRecovered));
                            tvTodayRecovered.setText("+ "+convert(todayRecovered));
                            tvActive.setText(convert(totalActive));
                            tvDeaths.setText(convert(totalDeaths));
                            tvTodayDeaths.setText("+ "+convert(todayDeaths));


                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);




                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

    private String convert(Double n){
        if(n < 1000){
            return String.format("%.2f",n);
        }else if(n <100000){
            n = n / 1000;
            return String.format("%.2f K",n);
        }else if(n < 10000000){
            n = n / 100000;
            return String.format("%.2f L",n);
        }else{
            n = n / 10000000;
            return String.format("%.2f Cr",n);
        }
    }

    private void gotoSafetyActivity(){
        startActivity(new Intent(MainActivity.this,SymptomsActivity.class));
    }
}