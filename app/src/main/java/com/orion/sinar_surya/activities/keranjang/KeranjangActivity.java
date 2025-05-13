package com.orion.sinar_surya.activities.keranjang;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.login.LoginActivity;
import com.orion.sinar_surya.activities.pesanan.PesananActivity;
import com.orion.sinar_surya.activities.product_list.ProductListAdapter;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailAdapter;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.models.KeranjangModel;
import com.orion.sinar_surya.models.ProductListDetailModel;
import com.orion.sinar_surya.models.ProductListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeranjangActivity extends AppCompatActivity {
    //var componen
    private TextView txtTotal;
    public Button btnBeli;
    public CheckBox chbSemua;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;

    //var for adapter/list
    private KeranjangAdapter mAdapter;
    public List<KeranjangModel> ListItems = new ArrayList<>();

    //var global
    private String seqBarangList = "";
    private String seqSatuanList = "";
    private double totalPesananTemp = 0;
    private int totalItemBeliTemp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);
        setTitle("Keranjang");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CreateView();
        InitClass();
        EventClass();
        LoadData();
    }

    private void CreateView() {
        txtTotal = findViewById(R.id.txtTotal);
        btnBeli = findViewById(R.id.btnBeli);
        chbSemua = findViewById(R.id.chbSemua);
        swipe = findViewById(R.id.swipe);
        rcvLoad = findViewById(R.id.rcvLoad);
        SetJenisTampilan();
    }

    private void InitClass() {
        txtTotal.setText(Global.FloatToStrFmt(0, true));
        btnBeli.setText("Beli (0)");
    }

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadData();
            }
        });

        btnBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    Intent intent = new Intent(KeranjangActivity.this, PesananActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("seq_barang_list", seqBarangList);
                    startActivityForResult(intent, 1);
                }else{
                    Snackbar.make(KeranjangActivity.this.findViewById(android.R.id.content), "Pilih barang dulu sebelum beli.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        chbSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox) view).isChecked();
                if (isChecked) {
                    mAdapter.setCheckedAll();
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter.setUncheckedAll();
                    mAdapter.notifyDataSetChanged();
                    InitClass();
                }
            }
        });

    }

    public void LoadData() {
        ProgressDialog loading = Global.createProgresSpinner(this, "Loading");
        loading.show();
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(this, "database_id", 0)
                +"&customer_seq="+ SharedPrefsUtils.getIntegerPreference(this, "customer_seq", 0);

        String url = Routes.url_get_keranjang_for_rekap() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                List<KeranjangModel> itemDataModels = new ArrayList<>();
                ListItems.clear();
                totalItemBeliTemp = 0;
                totalPesananTemp = 0;
                mAdapter.removeAllModel();
                mAdapter.notifyDataSetChanged();

                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    try {
                        for (int i = 0; i < ArrResults.length(); i++) {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            if (obj.getInt("seq_barang") > 0) {
                                double harga_awal = obj.getDouble("harga");
                                double diskon = obj.getDouble("diskon_pct");
                                double harga_akhir = harga_awal - (harga_awal * diskon / 100);
                                int qty = obj.getInt("qty");
                                harga_awal = Global.roundToTwoDecimalPlaces(harga_awal);
                                harga_akhir = Global.roundToTwoDecimalPlaces(harga_akhir);

                                KeranjangModel Data = new KeranjangModel(
                                        obj.getInt("seq_barang"),
                                        obj.getInt("seq_satuan"),
                                        obj.getString("nama_barang"),
                                        obj.getString("nama_satuan"),
                                        obj.getString("file_gambar"),
                                        harga_awal,
                                        harga_akhir,
                                        diskon,
                                        qty,
                                        obj.getString("is_pilih")
                                );
                                itemDataModels.add(Data);

                                if (!seqBarangList.isEmpty()) {
                                    seqBarangList += ",";
                                }
                                seqBarangList += obj.getString("seq_barang");
                                if (obj.getString("is_pilih").equals(JConst.TRUE_STRING)) {
                                    totalItemBeliTemp += qty;
                                    totalPesananTemp += harga_akhir * qty;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        swipe.setRefreshing(false);
                        loading.dismiss();
                    }

                    //isi
                    mAdapter.addModels(itemDataModels);
                    mAdapter.setTotalItemBeliTemp(totalItemBeliTemp);
                    mAdapter.setTotalPesananTemp(totalPesananTemp);
                    mAdapter.notifyDataSetChanged();
                    if (ListItems.size() == 0){
                        InitClass();
                    }
                    setTotalItemBeli(totalItemBeliTemp);
                    setTotalPesanan(totalPesananTemp);
                    swipe.setRefreshing(false);
                    loading.dismiss();

                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
                    swipe.setRefreshing(false);
                    loading.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                swipe.setRefreshing(false);
                loading.dismiss();
            }
        }, error -> {
            swipe.setRefreshing(false);
            loading.dismiss();
            Runnable runReconnect = new Runnable() {
                @Override
                public void run() {
                    LoadData();
                }
            };
            Global.handleError(KeranjangActivity.this, error, runReconnect);
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void SetJenisTampilan() {
        // Use LinearLayoutManager with horizontal orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoad.setLayoutManager(linearLayoutManager);

        // Set the adapter and other configurations
        mAdapter = new KeranjangAdapter(this, ListItems, R.layout.list_item_keranjang);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    public void setTotalPesanan(double totalPesanan){
        txtTotal.setText(Global.FloatToStrFmt(totalPesanan,true));
    }

    public void setTotalItemBeli(int totalItemBeli){
        btnBeli.setText(String.format("Beli (%s)", Global.FloatToStrFmt(totalItemBeli)));
    }

    public void setSeqBarangList(String seqBarangList){
        this.seqBarangList = seqBarangList;
    }
    public String getSeqBarangList() {
        return seqBarangList;
    }

    public void updateQtyKeranjang(int qty, int seqBarang, int seqSatuan){
        String url = Routes.url_update_field_keranjang();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(this, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                //isi value : masukan ke param
                Map<String, String> params = new HashMap<String, String>();
                params.put("database_id", String.valueOf(SharedPrefsUtils.getIntegerPreference(KeranjangActivity.this, "database_id", 0)));
                params.put("customer_seq", String.valueOf(SharedPrefsUtils.getIntegerPreference(KeranjangActivity.this, "customer_seq", 0)));
                params.put("field", "qty");
                params.put("value", String.valueOf(qty));
                params.put("where_clause", " and barang_seq = "+seqBarang+" and satuan_seq = "+seqSatuan);
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public void updateIsPilih(String isChecked, int seqBarang, int seqSatuan, Runnable run){
        String url = Routes.url_update_field_keranjang();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(this, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                }
                run.run();
            } catch (JSONException e) {
                e.printStackTrace();
                run.run();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            run.run();
        }) {
            @Override
            protected Map<String, String> getParams() {
                //isi value : masukan ke param
                Map<String, String> params = new HashMap<String, String>();
                params.put("database_id", String.valueOf(SharedPrefsUtils.getIntegerPreference(KeranjangActivity.this, "database_id", 0)));
                params.put("customer_seq", String.valueOf(SharedPrefsUtils.getIntegerPreference(KeranjangActivity.this, "customer_seq", 0)));
                params.put("field", "is_pilih");
                params.put("value", isChecked);
                params.put("where_clause", " and barang_seq = "+seqBarang+" and satuan_seq = "+seqSatuan);
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public void deleteKeranjang(int seqBarang, int seqSatuan){
        String url = Routes.url_delete_keranjang();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(this, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                //isi value : masukan ke param
                Map<String, String> params = new HashMap<String, String>();
                params.put("database_id", String.valueOf(SharedPrefsUtils.getIntegerPreference(KeranjangActivity.this, "database_id", 0)));
                params.put("customer_seq", String.valueOf(SharedPrefsUtils.getIntegerPreference(KeranjangActivity.this, "customer_seq", 0)));
                params.put("barang_seq", String.valueOf(seqBarang));
                params.put("satuan_seq", String.valueOf(seqSatuan));
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private boolean isValid() {
        boolean isValid = false;

        for (int i = 0; i < ListItems.size(); i++) {
            if (ListItems.get(i).getIs_pilih().equals(JConst.TRUE_STRING)) {
                isValid = true;
                break;
            }
        }

        return isValid;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoadData();
    }

}