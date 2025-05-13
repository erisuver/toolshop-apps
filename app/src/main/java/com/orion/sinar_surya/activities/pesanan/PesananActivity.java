package com.orion.sinar_surya.activities.pesanan;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.akun.UbahPasswordActivity;
import com.orion.sinar_surya.activities.home.HomeActivity;
import com.orion.sinar_surya.activities.keranjang.KeranjangActivity;
import com.orion.sinar_surya.activities.laporan.kartu_piutang.KartuPiutang;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.activities.riwayat_pesanan.RiwayatPesananFragment;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.globals.ShowDialog;
import com.orion.sinar_surya.models.PesananModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PesananActivity extends AppCompatActivity {
    //var componen
    private TextView txtTotalPesanan, txtSisaPlafond, txtAlamat;
    private Button btnPesan, btnBatalPesan;
    private RecyclerView rcvLoad;
    private TextInputEditText txtCatatan;
    private TextView txtTglOrder, txtNoFaktur, txtNomorOrder;
    private TextView txtBelumProses, txtProses, txtSelesai, txtLunas, txtBatal, txtDariApps, txtTolak;
    private LinearLayoutCompat layoutHeader, layoutPlafond;

    //var for adapter/list
    private PesananAdapter mAdapter;
    public List<PesananModel> ListItems = new ArrayList<>();

    //var global
    private String seqBarangList;
    private double totalPesananTemp = 0;
    private double sisaPlafond = 0;
    private double jmlHutang = 0;
    private double batasPlafond = 0;
    private double jmlBlmTerpakai = 0;
    private int seqPesanan;
    private String isDariApps;
    private boolean isDetail;
    private String tglOrder, noOrder, noFaktur, status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        txtSisaPlafond = findViewById(R.id.txtSisaPlafond);
        txtTotalPesanan = findViewById(R.id.txtTotalPesanan);
        txtAlamat = findViewById(R.id.txtAlamat);
        txtCatatan = findViewById(R.id.txtCatatan);
        btnPesan = findViewById(R.id.btnPesan);
        btnBatalPesan = findViewById(R.id.btnBatalPesan);
        rcvLoad = findViewById(R.id.rcvLoad);
        txtTglOrder = findViewById(R.id.txtTglOrder);
        txtNoFaktur = findViewById(R.id.txtNoFaktur);
        txtNomorOrder = findViewById(R.id.txtNomorOrder);
        layoutHeader = findViewById(R.id.layoutHeader);
        layoutPlafond = findViewById(R.id.layoutPlafond);
        txtBelumProses = findViewById(R.id.txtBelumProses);
        txtProses = findViewById(R.id.txtProses);
        txtSelesai = findViewById(R.id.txtSelesai);
        txtLunas = findViewById(R.id.txtLunas);
        txtBatal = findViewById(R.id.txtBatal);
        txtDariApps = findViewById(R.id.txtDariApps);
        txtTolak = findViewById(R.id.txtTolak);
    }

    private void InitClass() {
        SetJenisTampilan();
        Bundle extra = this.getIntent().getExtras();
        seqBarangList = extra.getString("seq_barang_list");
        seqPesanan = extra.getInt("seq_pesanan");
        isDariApps = extra.getString("is_dari_apps");
        tglOrder = extra.getString("tgl_order");
        noOrder = extra.getString("no_order");
        noFaktur = extra.getString("no_faktur");
        status = extra.getString("status");

        if (seqPesanan != 0){
            setTitle("Detail Pesanan");
            isDetail = true;
            LoadPesanan();
            isiHeader();

        }else{
            setTitle("Buat Pesanan");
            isDetail = false;
            LoadData();
        }

        String nama = SharedPrefsUtils.getStringPreference(this, "nama_customer");
        String alamat = SharedPrefsUtils.getStringPreference(this, "alamat_customer");
        String kota = SharedPrefsUtils.getStringPreference(this, "kota_customer");
        String propinsi = SharedPrefsUtils.getStringPreference(this, "propinsi_customer");
        String no_telp = SharedPrefsUtils.getStringPreference(this, "no_telp_customer");

        String alamatKirim = "Kepada\n" +
                "Yth : " + nama + "\n" +
                "Alamat : " + alamat + ", " + kota + ", " + propinsi + "\n" +
                "No. HP : " + no_telp;

        txtAlamat.setText(alamatKirim);
        setVisibilityComponent();
    }

    private void EventClass() {
        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    savePesanan();
                }
            }
        });

        btnBatalPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runBatal1 = new Runnable() {
                    @Override
                    public void run() {
                        batalkanPesanan(JConst.TRUE_STRING);
                    }
                };
                Runnable runBatal2 = new Runnable() {
                    @Override
                    public void run() {
                        batalkanPesanan(JConst.FALSE_STRING);
                    }
                };
                ShowDialog.confirmDialog(PesananActivity.this, "Batalkan Pesanan",
                                "Apakah barang ingin dimasukan ke keranjang?",
                                "Yes", "No", runBatal1, runBatal2);
            }
        });
    }

    private void setVisibilityComponent(){
        if (isDetail){
            layoutHeader.setVisibility(View.VISIBLE);
            btnPesan.setVisibility(View.GONE);
            layoutPlafond.setVisibility(View.GONE);
            txtCatatan.setEnabled(false);
        }else{
            layoutHeader.setVisibility(View.GONE);
            btnBatalPesan.setVisibility(View.GONE);
            btnPesan.setVisibility(View.VISIBLE);
            layoutPlafond.setVisibility(View.VISIBLE);
            txtCatatan.setEnabled(true);
        }
    }
    
    private void isiHeader(){
        txtTglOrder.setText(tglOrder);
        txtNomorOrder.setText(noOrder);
        txtNoFaktur.setText(noFaktur);

        txtBelumProses.setVisibility(View.GONE);
        txtProses.setVisibility(View.GONE);
        txtSelesai.setVisibility(View.GONE);
        txtLunas.setVisibility(View.GONE);
        txtBatal.setVisibility(View.GONE);
        txtDariApps.setVisibility(View.GONE);
        btnBatalPesan.setVisibility(View.GONE);
        txtTolak.setVisibility(View.GONE);

        if (status.contains(JConst.TEXT_STATUS_BELUM_DIPROSES)){
            txtBelumProses.setVisibility(View.VISIBLE);
            if (status.contains(JConst.TEXT_STATUS_DARI_APPS)){
                btnBatalPesan.setVisibility(View.VISIBLE);
            }
        }
        if (status.contains(JConst.TEXT_STATUS_DIPROSES) && !status.contains(JConst.TEXT_STATUS_BELUM_DIPROSES)){
            txtProses.setVisibility(View.VISIBLE);
        }
        if (status.contains(JConst.TEXT_STATUS_SELESAI)){
            txtSelesai.setVisibility(View.VISIBLE);
        }
        if (status.contains(JConst.TEXT_STATUS_LUNAS)){
            txtLunas.setVisibility(View.VISIBLE);
        }
        if (status.contains(JConst.TEXT_STATUS_DIBATALKAN)){
            txtBatal.setVisibility(View.VISIBLE);
        }
        if (status.contains(JConst.TEXT_STATUS_DARI_APPS)){
            txtDariApps.setVisibility(View.VISIBLE);
        }
        if (status.contains(JConst.TEXT_STATUS_DITOLAK)){
            txtTolak.setVisibility(View.VISIBLE);
        }
    }

    private void LoadData() {
        ProgressDialog loading = Global.createProgresSpinner(this, "Loading");
        loading.show();
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(this, "database_id", 0)
                +"&customer_seq="+ SharedPrefsUtils.getIntegerPreference(this, "customer_seq", 0)
                +"&seq_barang_list=";

        String url = Routes.url_get_keranjang_for_pesanan() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                List<PesananModel> itemDataModels = new ArrayList<>();
                mAdapter.removeAllModel();
                totalPesananTemp = 0;
                sisaPlafond = 0;

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

                                PesananModel Data = new PesananModel(
                                        obj.getInt("seq_barang"),
                                        obj.getInt("seq_satuan"),
                                        obj.getInt("seq_promo"),
                                        obj.getString("nama_barang"),
                                        obj.getString("nama_satuan"),
                                        obj.getString("file_gambar"),
                                        harga_awal,
                                        harga_akhir,
                                        diskon,
                                        qty
                                );
                                itemDataModels.add(Data);
//                                sisaPlafond = obj.getDouble("sisa_plafond");
                                jmlHutang = obj.getDouble("jml_hutang");
                                batasPlafond = obj.getDouble("batas_plafond");
                                jmlBlmTerpakai = obj.getDouble("jml_blm_terpakai");

                                if (batasPlafond == 0){
                                    sisaPlafond = 0;
                                }else{
                                    sisaPlafond = batasPlafond - (jmlHutang + jmlBlmTerpakai);
                                }
                                txtSisaPlafond.setText(Global.FloatToStrFmt(sisaPlafond, true));

                                totalPesananTemp += harga_akhir * qty ;
                                txtTotalPesanan.setText(Global.FloatToStrFmt(totalPesananTemp, true));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }

                    //isi
                    mAdapter.addModels(itemDataModels);
                    if (ListItems.size() <= 0){Toast.makeText(this, "Tidak ditemukan", Toast.LENGTH_SHORT).show();}
                    loading.dismiss();

                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            Runnable runReconnect = new Runnable() {
                @Override
                public void run() {
                    LoadData();
                }
            };
            Global.handleError(PesananActivity.this, error, runReconnect);
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void LoadPesanan() {
        ProgressDialog loading = Global.createProgresSpinner(this, "Loading");
        loading.show();
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(this, "database_id", 0)
                +"&customer_seq="+ SharedPrefsUtils.getIntegerPreference(this, "customer_seq", 0)
                +"&seq_pesanan="+ seqPesanan
                +"&is_dari_apps="+ isDariApps;

        String url = Routes.url_get_get_pesanan() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                List<PesananModel> itemDataModels = new ArrayList<>();
                mAdapter.removeAllModel();
                totalPesananTemp = 0;
                sisaPlafond = 0;

                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    try {
                        for (int i = 0; i < ArrResults.length(); i++) {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            if (obj.getInt("seq_barang") > 0) {
                                double harga_awal = obj.getDouble("harga");
                                double diskon = obj.getDouble("diskon_pct");
                                double harga_akhir = harga_awal;// - (harga_awal * diskon / 100);
                                int qty = obj.getInt("qty");
                                harga_awal = Global.roundToTwoDecimalPlaces(harga_awal);
                                harga_akhir = Global.roundToTwoDecimalPlaces(harga_akhir);

                                PesananModel Data = new PesananModel(
                                        obj.getInt("seq_barang"),
                                        obj.getInt("seq_satuan"),
                                        obj.getInt("seq_promo"),
                                        obj.getString("nama_barang"),
                                        obj.getString("nama_satuan"),
                                        obj.getString("file_gambar"),
                                        harga_awal,
                                        harga_akhir,
                                        diskon,
                                        qty
                                );
                                itemDataModels.add(Data);
                                sisaPlafond = obj.getDouble("sisa_plafond");
                                totalPesananTemp += harga_akhir * qty ;
                                txtCatatan.setText(obj.getString("keterangan"));
                                txtTotalPesanan.setText(Global.FloatToStrFmt(totalPesananTemp, true));
                                txtSisaPlafond.setText(Global.FloatToStrFmt(sisaPlafond, true));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }

                    //isi
                    mAdapter.addModels(itemDataModels);
                    if (ListItems.size() <= 0){Toast.makeText(this, "Tidak ditemukan", Toast.LENGTH_SHORT).show();}

                    loading.dismiss();
                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            loading.dismiss();
            Runnable runReconnect = new Runnable() {
                @Override
                public void run() {
                    LoadData();
                }
            };
            Global.handleError(PesananActivity.this, error, runReconnect);
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void SetJenisTampilan() {
        // Use LinearLayoutManager with horizontal orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvLoad.setLayoutManager(linearLayoutManager);

        // Set the adapter and other configurations
        mAdapter = new PesananAdapter(this, ListItems, R.layout.list_item_pesanan);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    public void savePesanan(){
        ProgressDialog loading = Global.createProgresSpinner(this, "Loading");
        loading.show();
        String url = Routes.url_save_pesanan();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(this, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Runnable runOk = new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            JApplication.getInstance().isNeedResume = true;
//                            startActivity(new Intent(PesananActivity.this, KeranjangActivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));

                            Intent i = new Intent(PesananActivity.this, HomeActivity.class);
//                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            i.putExtra("isDariPesanan", true);
                            startActivity(i);
                        }
                    };
                    Global.inform(PesananActivity.this, "Pesanan berhasil.","", runOk);
                }
                loading.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                //isi value : masukan ke param
                Gson gson = new Gson();
                String detail_ids = gson.toJson(mAdapter.Datas);

                Map<String, String> params = new HashMap<String, String>();
                params.put("database_id", String.valueOf(SharedPrefsUtils.getIntegerPreference(PesananActivity.this, "database_id", 0)));
                params.put("customer_seq", String.valueOf(SharedPrefsUtils.getIntegerPreference(PesananActivity.this, "customer_seq", 0)));
                params.put("total", String.valueOf(totalPesananTemp));
                params.put("catatan", txtCatatan.getText().toString());
                params.put("detail_ids", detail_ids);
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void batalkanPesanan(String isMasukanKeranjang) {
        ProgressDialog loading = Global.createProgresSpinner(this, "Loading");
        loading.show();
        String url = Routes.url_batalkan_pesanan();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(this, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Runnable runOk = new Runnable() {
                        @Override
                        public void run() {
                            if (isMasukanKeranjang.equals(JConst.TRUE_STRING)){
                                finish();
                                startActivity(new Intent(PesananActivity.this, KeranjangActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                            }else {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                    };
                    Global.inform(PesananActivity.this, "Pesanan berhasil dibatalkan.","", runOk);
                }
                loading.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Gson gson = new Gson();
                String detail_ids = gson.toJson(mAdapter.Datas);

                Map<String, String> params = new HashMap<String, String>();
                params.put("database_id", String.valueOf(SharedPrefsUtils.getIntegerPreference(PesananActivity.this, "database_id", 0)));
                params.put("customer_seq", String.valueOf(SharedPrefsUtils.getIntegerPreference(PesananActivity.this, "customer_seq", 0)));
                params.put("seq_pesanan", String.valueOf(seqPesanan));
                params.put("is_masukan_keranjang", isMasukanKeranjang);
                params.put("detail_ids", detail_ids);
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private boolean isValid() {
        boolean isValid = true;
        if (totalPesananTemp > sisaPlafond && sisaPlafond != 0) {
            Snackbar.make(PesananActivity.this.findViewById(android.R.id.content), "Total Pesanan tidak boleh melebihi Sisa Plafond.", Snackbar.LENGTH_SHORT).show();
            isValid = false;
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
    public void onBackPressed() {
        super.onBackPressed();
        JApplication.getInstance().isNeedResume = false;
    }
}