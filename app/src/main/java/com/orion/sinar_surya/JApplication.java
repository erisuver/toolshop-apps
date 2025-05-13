package com.orion.sinar_surya;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class JApplication extends Application {
    public DBConn dbConn;
    public SQLiteDatabase db;
    private static JApplication mInstance;
    public View viewTempProduct = null;
    public Boolean IsReload = false;
    private RequestQueue mRequestQueue;
    public static final String TAG = JApplication.class.getSimpleName();
    public final static NumberFormat fmt = NumberFormat.getInstance(new Locale("in", "ID"));
    public final static DecimalFormat df1 = new DecimalFormat("#.##");
    private String VersionName;
    private int VersionCode;
    public boolean isLogOn;
    private RequestQueue _requestQueue;
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "session_id";
    public String SelectedLov = "";
    public SharedPreferences sharedpreferences;
    public String lokasi_db;
    public String real_url;
    public SharedPreferences sharedPreferencesSetting;
    public boolean isLoggedIn = false;
    public boolean isNeedResume = true;
    public boolean isAlreadyCreatePageList = false;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        this.dbConn = new DBConn(getApplicationContext());
        this.db = new DBConn(JApplication.this).getWritableDatabase();
        mInstance = this;
        this.isLogOn = false;
        GetVersion();
        this.lokasi_db = this.dbConn.getDatabaseLocation();

        //create share pref
        sharedpreferences = getSharedPreferences("share_preference", Context.MODE_PRIVATE);

        _requestQueue = Volley.newRequestQueue(this);

        sharedPreferencesSetting = getSharedPreferences("setting", Context.MODE_PRIVATE);
        if (!sharedPreferencesSetting.contains("ip")){
            updateIPSharePref(Routes.IP_ADDRESS);
        }
    }

    public void updateIPSharePref(String ip){
        SharedPreferences.Editor editor = sharedPreferencesSetting.edit();
        editor.putString("ip", ip);
        editor.apply();
    }

    private void GetVersion(){
        try {
            this.VersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            this.VersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static synchronized JApplication getInstance() {
        return mInstance;
    }

    public String getVersionName() {
        return VersionName;
    }

    public void setVersionName(String versionName) {
        VersionName = versionName;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }


    public RequestQueue getRequestQueue() {
        return _requestQueue;
    }



    public void clearSharedPreperence(){
        sharedpreferences.edit().clear().commit();
    }
}
