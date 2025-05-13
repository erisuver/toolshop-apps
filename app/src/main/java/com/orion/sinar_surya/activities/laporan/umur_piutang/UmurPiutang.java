package com.orion.sinar_surya.activities.laporan.umur_piutang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.ILoadMore;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.models.UmurPiutangDetailModel;
import com.orion.sinar_surya.models.UmurPiutangModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UmurPiutang extends AppCompatActivity {
    private TextView txtKode, txtNama;
    public RecyclerView rcvData;
    private UmurPiutangAdapter adapter;
    public List<UmurPiutangModel> ListItems = new ArrayList<>();
    private ArrayList<UmurPiutangModel> datas;
    private ILoadMore loadMore;

    int lastVisibleItem, totalItemCount;
    boolean IsLoading;
    private boolean isStart;

    private int mMaxScrollSize;

    private LinearLayoutManager linearLayoutManager;
    int visibleThreshold = 4;
    private Long tgl_dari, tgl_Sampai;
    private long seqCustomer = 0;
    double totalDb = 0;
    double totalHt = 0;
    double totalJt = 0;
    private ProgressDialog Loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umur_piutang);
        CreateView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InitClass();
        EventClass();
    }

    private void CreateView(){
        this.txtKode = (TextView) findViewById(R.id.txtKode);
        this.txtNama = (TextView) findViewById(R.id.txtNama);
        this.rcvData = (RecyclerView) findViewById(R.id.rcvData);
        datas = new ArrayList<UmurPiutangModel>();
        this.adapter = new UmurPiutangAdapter(UmurPiutang.this, ListItems, UmurPiutang.this, R.layout.list_item_umur_piutang);
        rcvData.setAdapter(adapter);

        
    }

    private void InitClass(){
        this.setTitle("Umur Piutang");
        rcvData.setLayoutManager(new LinearLayoutManager(UmurPiutang.this));
        linearLayoutManager = (LinearLayoutManager)rcvData.getLayoutManager();
        this.adapter.removeAllModel();

        adapter.notifyDataSetChanged();

        tgl_dari   = Global.serverNowStartOfTheMonthLong();
        tgl_Sampai = Global.EndOfTheMonthLong(Global.serverNowLong());

        this.Loading = new ProgressDialog(this);

    }

    protected void EventClass(){
        setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.removeModel(ListItems.size()-1);
//                        load();
                    }
                },1000);

            }
        });

        rcvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                RefreshRecyclerView();
            }
        });

    }


    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    public void setLoaded(boolean loading) {
        IsLoading = loading;
    }

    public void RefreshRecyclerView(){
//        totalItemCount  = linearLayoutManager.getItemCount();
//        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//        if (!IsLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
//            if (loadMore != null){
//                loadMore.onLoadMore();
//                setLoaded(true);
//            }
//        }
        Loading.setMessage("Loading...");
        Loading.setCancelable(false);
        Loading.show();
        load();
    }

    public void load(){
        String Filter = "";
        int dbId = SharedPrefsUtils.getIntegerPreference(UmurPiutang.this, "database_id", 0);
        int custSeq = SharedPrefsUtils.getIntegerPreference(UmurPiutang.this, "customer_seq", 0);
        Filter += "?database_id=" + dbId +
               "&customer_seq="+custSeq;
        String url = Routes.url_load_umur_piutang() + Filter;
        JsonArrayRequest jArr = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                List<UmurPiutangModel> itemDataModels = new ArrayList<>();
                UmurPiutangModel Data = new UmurPiutangModel("",0, new ArrayList<UmurPiutangDetailModel>());
                itemDataModels.clear();
                datas.clear();
//                itemDataPrintModels.clear();
                double plafond = 0;
                double total = 0;

                for (int i = 0; i < response.length(); i++) {
                    try { JSONObject obj = response.getJSONObject(i);
                        if (i == 0){
                            txtKode.setText(obj.getString("kode"));
                            txtNama.setText(obj.getString("nama"));
                            plafond = obj.getDouble("plafond");
                        }

                        if (obj.getInt("urutan") == 1){
                            if (i > 0){
                                itemDataModels.add(Data);
                                datas.add(Data);
                            }
                            String tipe = "";
                            if (obj.getInt("tipe") == 1){
                                tipe = "01 - 14";
                            }else if (obj.getInt("tipe") == 2){
                                tipe = "15 - 30";
                            }else if (obj.getInt("tipe") == 3){
                                tipe = "31 - 45";
                            }else if (obj.getInt("tipe") == 4){
                                tipe = "46 - 60";
                            }else if (obj.getInt("tipe") == 5){
                                tipe = "61 - 75";
                            }else if (obj.getInt("tipe") == 6){
                                tipe = "76 - 90";
                            }else if (obj.getInt("tipe") == 7){
                                tipe = ">91";
                            }
                            total = total + obj.getDouble("total");
                            Data = new UmurPiutangModel(
                                    tipe,
                                    obj.getDouble("total"),
                                    new ArrayList<UmurPiutangDetailModel>()
                            );
                        }else{
                            UmurPiutangDetailModel umurPiutangDetailModel = new UmurPiutangDetailModel(obj.getString("nomor"),
                                    obj.getString("tgl_jt"),
                                    obj.getDouble("total"),
                                    obj.getInt("umur"));
                            Data.getDetail().add(umurPiutangDetailModel);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UmurPiutang.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }
                }
                if (response.length() > 0){
                    itemDataModels.add(Data);
                    datas.add(Data);
                }
                //total
                Data = new UmurPiutangModel(
                        "Total",
                        total,
                        new ArrayList<UmurPiutangDetailModel>()
                );
                Data.setIs_total(true);
                itemDataModels.add(Data);
                datas.add(Data);

                //plafond
                Data = new UmurPiutangModel(
                        "Plafond",
                        plafond,
                        new ArrayList<UmurPiutangDetailModel>()
                );
                Data.setIs_total(true);
                itemDataModels.add(Data);
                datas.add(Data);

                //Selisih
                Data = new UmurPiutangModel(
                        "Selisih",
                        plafond - total,
                        new ArrayList<UmurPiutangDetailModel>()
                );
                Data.setIs_total(true);
                itemDataModels.add(Data);
                datas.add(Data);

                adapter.addModels(itemDataModels);
                setLoaded(false);
                Loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                setLoaded(false);
                String errorMessage = e.getMessage();
                Toast.makeText(UmurPiutang.this, errorMessage, Toast.LENGTH_SHORT).show();
                Loading.dismiss();
            }
        });
        JApplication.getInstance().addToRequestQueue(jArr);
    }
//

    @Override
    public void onStart() {
        super.onStart();
        isStart = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bundle extra = data.getExtras();
                seqCustomer     = extra.getLong("CUSTOMER_SEQ");
            }
            rcvData.scrollToPosition(0);
            adapter.removeAllModel();
            RefreshRecyclerView();
        }else{
            if (requestCode == 1) {
                seqCustomer = 0;
                rcvData.scrollToPosition(0);
                adapter.removeAllModel();
                RefreshRecyclerView();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshRecyclerView();
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
