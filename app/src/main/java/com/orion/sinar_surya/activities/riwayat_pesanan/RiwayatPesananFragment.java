package com.orion.sinar_surya.activities.riwayat_pesanan;

import static com.orion.sinar_surya.globals.Global.hideSoftKeyboard;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.globals.FungsiGeneral;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.models.RiwayatPesananModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RiwayatPesananFragment extends Fragment {
    //var componen/view
    private View v;
    private Toolbar toolbar;
    private AppCompatActivity thisAppCompat;
    private EditText txtDari, txtSampai, txtNomorOrder;
    private Spinner spnStatus;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;


    //var for adapter/list
    private RiwayatPesananAdapter mAdapter;
    public List<RiwayatPesananModel> ListItems = new ArrayList<>();

    //var global
    private String status;
    private String noOrder;
    private String tglDari;
    private String tglSampai;
    private final String TEXT_SEMUA = "Semua";
    private boolean isInitialSelection;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_riwayat_pesanan, container, false);
        v = view;
        CreateView();
        InitClass();
        EventClass();
        ResetData();
//        LoadData(); //pindah ke onresume
        return view;
    }

    private void CreateView() {
        txtDari = v.findViewById(R.id.txtDari);
        txtSampai = v.findViewById(R.id.txtSampai);
        txtNomorOrder = v.findViewById(R.id.txtNomorOrder);
        spnStatus = v.findViewById(R.id.spnStatus);
        rcvLoad = v.findViewById(R.id.rcvLoad);
        swipe = v.findViewById(R.id.swipe);

        toolbar = v.findViewById(R.id.toolbar);
        thisAppCompat = (AppCompatActivity) getActivity();
        thisAppCompat.setSupportActionBar(toolbar);
    }

    private void InitClass() {
        SetJenisTampilan();
        isInitialSelection = true;

        // Inisialisasi data untuk Spinner
        List<String> optionsList = new ArrayList<>();
        optionsList.add(TEXT_SEMUA);
        optionsList.add(JConst.TEXT_STATUS_BELUM_DIPROSES);
        optionsList.add(JConst.TEXT_STATUS_DIPROSES);
        optionsList.add(JConst.TEXT_STATUS_SELESAI);
        optionsList.add(JConst.TEXT_STATUS_LUNAS);
        optionsList.add(JConst.TEXT_STATUS_DIBATALKAN);
        optionsList.add(JConst.TEXT_STATUS_DARI_APPS);
        optionsList.add(JConst.TEXT_STATUS_DITOLAK);
        // Buat adapter dan atur data ke Spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner_item, optionsList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(arrayAdapter);
    }

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rcvLoad.scrollToPosition(0);
                LoadData();
                swipe.setRefreshing(false);
            }
        });

        txtDari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(getActivity(), getActivity().findViewById(android.R.id.content));
                Long tgl = FungsiGeneral.getMillisDateFmt(tglDari, "yyyy-MM-dd");
                int mYear = (Integer.parseInt(FungsiGeneral.getTahun(tgl)));
                int mMonth = (Integer.parseInt(FungsiGeneral.getBulan(tgl)));
                int mDay = (Integer.parseInt(FungsiGeneral.getHari(tgl)));

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                String tglDariTemp = format.format(calendar.getTime());
                                txtDari.setText(tglDariTemp);

                                tglDari = Global.convertDateFormat(tglDariTemp, "dd-MM-yyyy", "yyyy-MM-dd");
                                rcvLoad.scrollToPosition(0);
                                LoadData();
                            }
                        }, mYear, mMonth-1, mDay);
                datePickerDialog.show();
            }
        });

        txtSampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(getActivity(), getActivity().findViewById(android.R.id.content));
                Long tgl = FungsiGeneral.getMillisDateFmt(tglSampai, "yyyy-MM-dd");
                int mYear = (Integer.parseInt(FungsiGeneral.getTahun(tgl)));
                int mMonth = (Integer.parseInt(FungsiGeneral.getBulan(tgl)));
                int mDay = (Integer.parseInt(FungsiGeneral.getHari(tgl)));

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                String tglSampaiTemp = format.format(calendar.getTime());
                                txtSampai.setText(tglSampaiTemp);

                                tglSampai = Global.convertDateFormat(tglSampaiTemp, "dd-MM-yyyy", "yyyy-MM-dd");
                                rcvLoad.scrollToPosition(0);
                                LoadData();
                            }
                        }, mYear, mMonth-1, mDay);
                datePickerDialog.show();
            }
        });

        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = (String) parentView.getItemAtPosition(position);
                if (selectedOption.equals(TEXT_SEMUA)){
                    status =  "";
                }else if(selectedOption.equals(JConst.TEXT_STATUS_BELUM_DIPROSES)){
                    status =  JConst.STATUS_BELUM_DIPROSES;
                }else if(selectedOption.equals(JConst.TEXT_STATUS_DIPROSES)){
                    status =  JConst.STATUS_DIPROSES;
                }else if(selectedOption.equals(JConst.TEXT_STATUS_SELESAI)){
                    status =  JConst.STATUS_SELESAI;
                }else if(selectedOption.equals(JConst.TEXT_STATUS_DIBATALKAN)){
                    status =  JConst.STATUS_DIBATALKAN;
                }else if(selectedOption.equals(JConst.TEXT_STATUS_LUNAS)){
                    status =  JConst.STATUS_LUNAS;
                }else if(selectedOption.equals(JConst.TEXT_STATUS_DARI_APPS)){
                    status =  JConst.STATUS_DARI_APPS;
                }else if(selectedOption.equals(JConst.TEXT_STATUS_DITOLAK)){
                    status =  JConst.STATUS_DITOLAK;
                }

                // Memeriksa apakah ini adalah seleksi awal (saat pertama buka)
                if (!isInitialSelection) {
                    LoadData();
                } else {
                    // Set flag ke false setelah seleksi pertama
                    isInitialSelection = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle ketika tidak ada item yang dipilih
            }
        });

        txtNomorOrder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        // Tindakan yang diambil saat tombol pencarian di keyboard ditekan
                        noOrder = v.getText().toString();
                        LoadData();
                        return true;
                    }
                    return false;
                }
            });
    }

    public void LoadData() {
        ProgressDialog loading = Global.createProgresSpinner(getActivity(), "Loading");
        loading.show();
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(getContext(), "database_id", 0)
                +"&customer_seq=" + SharedPrefsUtils.getIntegerPreference(getContext(), "customer_seq", 0)
                +"&status="+status
                +"&no_order="+noOrder
                +"&tgl_dari="+tglDari
                +"&tgl_sampai="+tglSampai;

        String url = Routes.url_get_riwayat_pesanan() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                List<RiwayatPesananModel> itemDataModels = new ArrayList<>();
                itemDataModels.clear();
                mAdapter.removeAllModel();

                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    for (int i = 0; i < ArrResults.length(); i++) {
                        try {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            int seq = (obj.getInt("pesanan_cust_seq") == 0) ? obj.getInt("so_seq") : obj.getInt("pesanan_cust_seq");
                            String no_faktur = obj.getString("no_faktur").equals("null") ? "Belum dibuat faktur" : obj.getString("no_faktur");

                            RiwayatPesananModel Data = new RiwayatPesananModel(
                                    seq,
                                    obj.getString("tgl_order"),
                                    no_faktur,
                                    obj.getString("no_order"),
                                    obj.getInt("qty"),
                                    Global.roundToTwoDecimalPlaces(obj.getDouble("total")),
                                    obj.getString("is_proses"),
                                    obj.getString("is_belum_proses"),
                                    obj.getString("is_selesai"),
                                    obj.getString("is_batal"),
                                    obj.getString("is_lunas"),
                                    obj.getString("is_dari_apps"),
                                    obj.getString("is_tolak")
                            );
                            itemDataModels.add(Data);
                        } catch (JSONException e) {
                            loading.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //isi
                    mAdapter.addModels(itemDataModels);
                    mAdapter.notifyDataSetChanged();
                    if (ListItems.size() <= 0){Toast.makeText(getContext(), "Tidak ditemukan", Toast.LENGTH_SHORT).show();}

                    loading.dismiss();
                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    loading.dismiss();
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(getContext(), pesan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                loading.dismiss();
                e.printStackTrace();
            }
        }, error -> {
            loading.dismiss();
            Runnable runReconnect = new Runnable() {
                @Override
                public void run() {
                    LoadData();
                }
            };
            Global.handleError(getActivity(), error, runReconnect);
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void SetJenisTampilan() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new RiwayatPesananAdapter(this, ListItems, R.layout.list_item_riwayat_pesanan);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);
    }

    private void ResetData(){
        txtDari.setText(Global.StartOfTheMonthFormatted("dd-MM-yyyy"));
        tglDari = Global.StartOfTheMonthFormatted("yyyy-MM-dd");
        txtSampai.setText(Global.serverNowFormated("dd-MM-yyyy"));
        tglSampai = Global.serverNowFormated("yyyy-MM-dd");
        txtNomorOrder.setText("");
        spnStatus.setSelection(0);
        status = "";
        noOrder = "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (JApplication.getInstance().isNeedResume) {
            ResetData();
            LoadData();
            JApplication.getInstance().isNeedResume = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        JApplication.getInstance().isNeedResume = true;
    }
}
