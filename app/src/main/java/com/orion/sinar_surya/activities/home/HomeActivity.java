package com.orion.sinar_surya.activities.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.akun.AkunFragment;
import com.orion.sinar_surya.activities.keranjang.KeranjangActivity;
import com.orion.sinar_surya.activities.laporan.kartu_piutang.KartuPiutang;
import com.orion.sinar_surya.activities.laporan.umur_piutang.UmurPiutang;
import com.orion.sinar_surya.activities.login.LoginActivity;
import com.orion.sinar_surya.activities.product_list.ProductListFragment;
import com.orion.sinar_surya.activities.riwayat_pesanan.RiwayatPesananFragment;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.globals.ShowDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {
    ProductListFragment frgProductList = new ProductListFragment();
    AkunFragment frgAkun = new AkunFragment();
    RiwayatPesananFragment frgRiwayatOrder = new RiwayatPesananFragment();

    BottomNavigationView navigation;
    private BottomNavigationItemView itemViewCart, itemViewOrderList, itemViewInbox;
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    String destination = "";
    private final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private boolean isDariPesanan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        get_ip_address();
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        navigation = findViewById(R.id.navigation);
        navigation.getMenu().clear();
        navigation.inflateMenu(R.menu.navigation);

        Bundle extra = this.getIntent().getExtras();
        if (extra != null) {
            isDariPesanan = extra.getBoolean("isDariPesanan");
            if (isDariPesanan) {
                navigation.setSelectedItemId(R.id.navigation_order_list);
                loadFragment(frgRiwayatOrder);
            }
        }
    }

    private void InitClass(){
        destination = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/sinar_surya.apk";
    }

    private void EventClass() {
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return loadFragment(frgProductList);
                    case R.id.navigation_chart: {
                        startActivity(new Intent(HomeActivity.this, KeranjangActivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        return false;
                    }
                    case R.id.navigation_order_list:{
                        return loadFragment(frgRiwayatOrder);
                    }
                    case R.id.navigation_account:{
                        return loadFragment(frgAkun);
                    }
                }
                loadFragment(null);
                return false;
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private Fragment getCurrentFragment(){
        return getSupportFragmentManager().findFragmentById(R.id.fl_container);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void ValidasiVersi(){
        String url = Routes.url_get_versi_app();
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int versiLama = Global.convertVersiToInt(JApplication.getInstance().getVersionName());
                int versiCloud = Global.convertVersiToInt(response);
                if (versiLama < versiCloud){
                    final Runnable runOk = new Runnable() {
                        @Override
                        public void run() {
                            new DownloadFileFromURL().execute(Routes.url_apk());
                        }
                    };

                    Global.inform(HomeActivity.this, "Anda menggunakan versi lama, klik OK untuk mendownload versi terbaru", "Pemberitahuan Update", runOk);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Global.inform(HomeActivity.this, JConst.MSG_GAGAL_KONEKSI_KE_SERVER, String.valueOf(getTitle()), new Runnable() {
                    @Override
                    public void run() {
                        finishAffinity();
                    }
                });
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void InformVersi(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                //.setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading apk. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(destination);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            installAPK();
            dismissDialog(progress_bar_type);
        }

    }

    void installAPK(){
        String PATH = destination;
        File file = new File(PATH);
        if(file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriFromFile(getApplicationContext(), new File(PATH)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                getApplicationContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Log.e("TAG", "Error in opening the file!");
            }
        }else{
            Toast.makeText(getApplicationContext(),"installing",Toast.LENGTH_LONG).show();
        }
    }

    Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, getPackageName() + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Tekan lagi untuk keluar.", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    private void relogin (Runnable runnable){
        String url = Routes.url_login();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Snackbar.make(HomeActivity.this.findViewById(android.R.id.content), jsonObjectData.getString("message"), Snackbar.LENGTH_SHORT).show();
                } else {
                    JApplication.getInstance().isLoggedIn = true;
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (e.getMessage().contains("Invalid User ID")){
                    //clear session
                    SharedPrefsUtils.setStringPreference(HomeActivity.this, "login_session", "");
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                }else {
                    Snackbar.make(HomeActivity.this.findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }

            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                //isi value : masukan ke param
                Map<String, String> params = new HashMap<String, String>();
                params.put("kode_login", SharedPrefsUtils.getStringPreference(HomeActivity.this, "last_user"));
                params.put("password", SharedPrefsUtils.getStringPreference(HomeActivity.this, "last_password"));
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    private void get_ip_address(){
        /*ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Biarkan false jika Anda ingin mencegah pengguna menutupnya
        progressDialog.show();*/
        //pertama cobain konek ke ip sesuai sharedfreef
        Runnable runSukses = new Runnable() {
            @Override
            public void run() {
//                progressDialog.dismiss();
                //cek session login
                if (SharedPrefsUtils.getStringPreference(HomeActivity.this, "login_session").isEmpty()){
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                }else{
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (!isDariPesanan) {
                                loadFragment(frgProductList);
                                ValidasiVersi(); //cek versi
                            }
                        }
                    };
                    //cek apakah data akun masih sama?. kalo tidak maka paksa logout.
                    relogin(runnable);
                }
            }
        };

        Runnable runGagal = new Runnable() {
            @Override
            public void run() {
//                progressDialog.dismiss();
                Runnable runReconnect = new Runnable() {
                    @Override
                    public void run() {
                        get_ip_address();
                    }
                };
                ShowDialog.confirmDialog(HomeActivity.this, getString(R.string.information), "Tidak terhubung ke server", runReconnect);
            }
        };
        Runnable runAmbilIPDariDrive = new Runnable() {
            @Override
            public void run() {
                JApplication.getInstance().real_url = Routes.URL_API_AWAL;
                String url = Routes.URL_DRIVE_PROFILE;
                StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String ip = cariNilai(response, Routes.NAMA_API);
                        ip = decryptNew(ip);
                        ip = "http://"+ip;
                        cek_koneksi(ip, runSukses, runGagal);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        runGagal.run();
                    }
                });
                JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
            }
        };

        Runnable runAmbilIPOrionbdg = new Runnable() {
            @Override
            public void run() {
                JApplication.getInstance().real_url = Routes.URL_API_AWAL;
                String url = Routes.URL_GET_REAL_API;
                StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Hello")){
                            runAmbilIPDariDrive.run();
                        }else{
                            cek_koneksi(response, runSukses, runAmbilIPDariDrive);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        runAmbilIPDariDrive.run();
                    }
                });
                JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
            }
        };

        String ip = Routes.IP_ADDRESS;  //default
        cek_koneksi(ip, runSukses, runAmbilIPOrionbdg);
    }


    public static String cariNilai(String input, String cari) {
        // Split string berdasarkan newline
        String[] lines = input.split("\n");

        // Loop melalui setiap baris
        for (String line : lines) {
            // Split baris berdasarkan tanda sama (=)
            String[] parts = line.split("=");
            if (parts.length == 2 && parts[0].equals(cari)) {
                // Jika kunci ditemukan, kembalikan nilainya
                return parts[1];
            }
        }

        // Kembalikan string kosong jika kunci tidak ditemukan
        return "";
    }

    private void cek_koneksi(String url, Runnable runSukses, Runnable runGagal){
        String link = url+"/"+ Routes.NAMA_API+"/public/cek_koneksi.php";
        StringRequest strReq = new StringRequest(Request.Method.GET, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JApplication.getInstance().real_url = url;
                JApplication.getInstance().updateIPSharePref(url);
                JApplication.getInstance().real_url = JApplication.getInstance().real_url+"/"+Routes.NAMA_API+"/public/";
                runSukses.run();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                runGagal.run();
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static String decryptNew(String input) {
        HashMap<String, String> list = new HashMap<>();
        list.put("e9r", "a");
        list.put("asF", "b");
        list.put("ytH", "c");
        list.put("43y", "d");
        list.put("nSu", "e");
        list.put("fdQ", "f");
        list.put("uyo", "g");
        list.put("jhr", "h");
        list.put("vgb", "i");
        list.put("nm7", "j");
        list.put("cvF", "k");
        list.put("yKV", "l");
        list.put("k93", "m");
        list.put("fdk", "n");
        list.put("vfd", "o");
        list.put("34g", "p");
        list.put("320", "q");
        list.put("eds", "r");
        list.put("ehj", "s");
        list.put("ebv", "t");
        list.put("etr", "u");
        list.put("w94", "v");
        list.put("vcf", "w");
        list.put("pty", "x");
        list.put("j9f", "y");
        list.put("xje", "z");
        list.put("e92", "A");
        list.put("asD", "B");
        list.put("yFH", "C");
        list.put("4Xy", "D");
        list.put("n1u", "E");
        list.put("GdQ", "F");
        list.put("Yyo", "G");
        list.put("jJr", "H");
        list.put("v7b", "I");
        list.put("NM7", "J");
        list.put("c0F", "K");
        list.put("QeV", "L");
        list.put("kg3", "M");
        list.put("jhk", "N");
        list.put("cir", "O");
        list.put("dZi", "P");
        list.put("kR1", "Q");
        list.put("fdd", "R");
        list.put("jLi", "S");
        list.put("f9w", "T");
        list.put("DYx", "U");
        list.put("vde", "V");
        list.put("akb", "W");
        list.put("dsf", "X");
        list.put("gfv", "Y");
        list.put("jul", "Z");
        list.put("cHs", "0");
        list.put("dIe", "1");
        list.put("lgu", "2");
        list.put("mIe", "3");
        list.put("SuM", "4");
        list.put("heN", "5");
        list.put("32x", "6");
        list.put("efr", "7");
        list.put("fd8", "8");
        list.put("fUK", "9");

        StringBuilder hasil = new StringBuilder();
        StringBuilder cari = new StringBuilder();
        int charke = 0;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (Character.isLetterOrDigit(currentChar)) {
                charke++;
                cari.append(currentChar);
                if (charke == 3) {
                    String temp = list.get(cari.toString());
                    if (temp == null || temp.isEmpty()) {
                        temp = cari.toString();
                    }
                    hasil.append(temp);
                    cari = new StringBuilder();
                    charke = 0;
                }
            } else {
                hasil.append(currentChar);
            }
        }

        return hasil.toString();
    }
}