package com.orion.sinar_surya.activities.akun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.orion.sinar_surya.BuildConfig;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.keranjang.KeranjangAdapter;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.activities.riwayat_pesanan.RiwayatPesananAdapter;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.models.FakturPajakModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FakturPajakActivity extends AppCompatActivity {
    //var view
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;

    //var for adapter/list
    private FakturPajakAdapter mAdapter;
    public List<FakturPajakModel> ListItems = new ArrayList<>();

    //var global
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    String destination = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faktur_pajak);
        setTitle("Daftar Faktur Pajak");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CreateView();
        InitClass();
        EventClass();
        LoadData();
    }

    private void CreateView() {
        rcvLoad = findViewById(R.id.rcvLoad);
        swipe = findViewById(R.id.swipe);
        SetJenisTampilan();
    }

    private void InitClass() {
    }

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadData();
            }
        });
    }

    private void SetJenisTampilan() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new FakturPajakAdapter(this, ListItems, R.layout.list_item_faktur_pajak);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    public void LoadData() {
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(this, "database_id", 0)
                +"&customer_seq="+ SharedPrefsUtils.getIntegerPreference(this, "customer_seq", 0);

        String url = Routes.url_get_faktur_pajak() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                List<FakturPajakModel> itemDataModels = new ArrayList<>();
                mAdapter.removeAllModel();

                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    try {
                        for (int i = 0; i < ArrResults.length(); i++) {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            FakturPajakModel Data = new FakturPajakModel(
                                    obj.getString("attachment"),
                                    obj.getString("tgl_faktur"),
                                    obj.getString("no_faktur"),
                                    obj.getString("no_order"),
                                    obj.getDouble("total")
                            );
                            itemDataModels.add(Data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        swipe.setRefreshing(false);
                    }

                    //isi
                    mAdapter.addModels(itemDataModels);
                    mAdapter.notifyDataSetChanged();
                    swipe.setRefreshing(false);
                    if (ListItems.size() <= 0){Toast.makeText(this, "Tidak ditemukan", Toast.LENGTH_SHORT).show();}

                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
                    swipe.setRefreshing(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                swipe.setRefreshing(false);
            }
        }, error -> {
            swipe.setRefreshing(false);
            Runnable runReconnect = new Runnable() {
                @Override
                public void run() {
                    LoadData();
                }
            };
            Global.handleError(FakturPajakActivity.this, error, runReconnect);
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public void downlaodFile(String url, String fileName){
        url = url.replace("\\", "/");
        destination = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/"+fileName;
        new DownloadFileFromURL().execute(url);
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
            openFile();
            dismissDialog(progress_bar_type);
        }

    }

    void openFile(){
        String PATH = destination;
        File file = new File(PATH);
        if(file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = uriFromFile(getApplicationContext(), new File(PATH));
            String mime = getContentResolver().getType(uri);
            intent.setDataAndType(uri, mime);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                getApplicationContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Log.e("TAG", "Error in opening the file!");
                Toast.makeText(getApplicationContext(), "Tidak ditemukan aplikasi untuk membuka file ini.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Download gagal.",Toast.LENGTH_LONG).show();
        }
    }
    Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

}