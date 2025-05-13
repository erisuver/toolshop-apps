package com.orion.sinar_surya.activities.product_list;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FilterProductListActivity extends AppCompatActivity {
    private Button btnFilter;
    private Spinner spnJenisBarang, spnKlasifikasi, spnUkuran;
    private TextView txtTahun;

    private List<String> ListJenisBarang;
    private ArrayAdapter<String> AdapterJenisBarang;

    private List<String> ListKlasifikasi;
    private ArrayAdapter<String> AdapterKlasifikasi;

    private List<String> ListUkuran;
    private ArrayAdapter<String> AdapterUkuran;

    String JENIS_BARANG, KLASIFIKASI, UKURAN;
    String JENIS_BARANG_TMP, KLASIFIKASI_TMP, UKURAN_TMP;

    private final String TEXT_SEMUA = "NONE";

    private ProgressDialog Loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_product_list);
        setTitle("Filter Produk");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
    }
    private void CreateView() {
        btnFilter = findViewById(R.id.btnFilter);
        spnJenisBarang = findViewById(R.id.spnJenisBarang);
        spnKlasifikasi = findViewById(R.id.spnKlasifikasi);
        spnUkuran = findViewById(R.id.spnUkuran);
    }

    private void InitClass() {
        ListJenisBarang = new ArrayList<>();
        AdapterJenisBarang = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListJenisBarang);
        AdapterJenisBarang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnJenisBarang.setAdapter(AdapterJenisBarang);

        ListKlasifikasi = new ArrayList<>();
        AdapterKlasifikasi = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListKlasifikasi);
        AdapterKlasifikasi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnKlasifikasi.setAdapter(AdapterKlasifikasi);

        ListUkuran = new ArrayList<>();
        AdapterUkuran = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ListUkuran);
        AdapterUkuran.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUkuran.setAdapter(AdapterUkuran);

        Bundle extra   = this.getIntent().getExtras();
        this.JENIS_BARANG     = extra.getString("JENIS_BARANG");
        this.KLASIFIKASI = extra.getString("KLASIFIKASI");
        this.UKURAN     = extra.getString("UKURAN");

        this.JENIS_BARANG_TMP     = this.JENIS_BARANG;
        this.KLASIFIKASI_TMP = this.KLASIFIKASI;
        this.UKURAN_TMP     = this.UKURAN;

        this.Loading = new ProgressDialog(FilterProductListActivity.this);
        Loading.setMessage("Loading...");
        Loading.setCancelable(false);
        Loading.show();

        setJenisBarang();
        setKlasifikasi();
        setUkuran();
        Loading.dismiss();
    }

    private void EventClass() {
        spnJenisBarang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                JENIS_BARANG_TMP = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnKlasifikasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KLASIFIKASI_TMP = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnUkuran.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UKURAN_TMP = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFilter();
            }
        });

    }

    private void resetFilter() {
        JENIS_BARANG_TMP= TEXT_SEMUA;
        KLASIFIKASI_TMP = TEXT_SEMUA;
        UKURAN_TMP      = TEXT_SEMUA;

        spnJenisBarang.setSelection(ListJenisBarang.indexOf(JENIS_BARANG_TMP));
        spnKlasifikasi.setSelection(ListKlasifikasi.indexOf(KLASIFIKASI_TMP));
        spnUkuran.setSelection(ListUkuran.indexOf(UKURAN_TMP));

        AdapterJenisBarang.notifyDataSetChanged();
        AdapterKlasifikasi .notifyDataSetChanged();
        AdapterUkuran.notifyDataSetChanged();
    }



    public void startFilter() {
        JENIS_BARANG     = JENIS_BARANG_TMP;
        KLASIFIKASI = KLASIFIKASI_TMP;
        UKURAN     = UKURAN_TMP;

        Intent intent = getIntent();
        intent.putExtra("JENIS_BARANG", JENIS_BARANG);
        intent.putExtra("KLASIFIKASI", KLASIFIKASI);
        intent.putExtra("UKURAN", UKURAN);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setJenisBarang() {
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(this, "database_id", 0);
        String url = Routes.url_get_jenis_barang() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {

                    ListJenisBarang.clear();
                    ListJenisBarang.add(TEXT_SEMUA);
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    try {
                        for (int i = 0; i < ArrResults.length(); i++) {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            ListJenisBarang.add(obj.getString("nama"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }

                    AdapterJenisBarang.notifyDataSetChanged();

                    if (!JENIS_BARANG_TMP.equals(TEXT_SEMUA)){
                        spnJenisBarang.setSelection(ListJenisBarang.indexOf(JENIS_BARANG_TMP));
                        AdapterJenisBarang.notifyDataSetChanged();
                    }
                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("ERROR REQUEST",error.getMessage());
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void setKlasifikasi() {
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(this, "database_id", 0);
        String url = Routes.url_get_klasifikasi() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {

                    ListKlasifikasi.clear();
                    ListKlasifikasi.add(TEXT_SEMUA);
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    try {
                        for (int i = 0; i < ArrResults.length(); i++) {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            ListKlasifikasi.add(obj.getString("nama"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }

                    AdapterKlasifikasi.notifyDataSetChanged();

                    if (!KLASIFIKASI_TMP.equals(TEXT_SEMUA)){
                        spnKlasifikasi.setSelection(ListKlasifikasi.indexOf(KLASIFIKASI_TMP));
                        AdapterKlasifikasi.notifyDataSetChanged();
                    }
                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void setUkuran() {
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(this, "database_id", 0);
        String url = Routes.url_get_ukuran() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {

                    ListUkuran.clear();
                    ListUkuran.add(TEXT_SEMUA);
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    try {
                        for (int i = 0; i < ArrResults.length(); i++) {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            ListUkuran.add(obj.getString("nama"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }

                    AdapterUkuran.notifyDataSetChanged();

                    if (!UKURAN_TMP.equals(TEXT_SEMUA)){
                        spnUkuran.setSelection(ListUkuran.indexOf(UKURAN_TMP));
                        AdapterUkuran.notifyDataSetChanged();
                    }
                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            case R.id.menu_reset:
                resetFilter();
        }
        return true;
    }
}