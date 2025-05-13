package com.orion.sinar_surya.activities.akun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.laporan.kartu_piutang.KartuPiutang;
import com.orion.sinar_surya.activities.laporan.umur_piutang.UmurPiutang;
import com.orion.sinar_surya.activities.login.LoginActivity;
import com.orion.sinar_surya.activities.product_list_detail.ProductListDetailActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.utility.FileDownloadHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AkunFragment extends Fragment {
    //var componen/view
    private TextInputEditText txtKode, txtNama, txtNIK, txtNPWP, txtAlamat, txtEmail, txtNoTelp, txtPlafond, txtSisaPiutang;
    private Button btnDownloadFaktur, btnLogout, btnUbahPassword, btnDownloadHelp;
    private View v;
    private Toolbar toolbar;
    private AppCompatActivity thisAppCompat;

    //var global

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_akun, container, false);
        v = view;
        CreateView();
        InitClass();
        EventClass();
        LoadData();
        return view;
    }

    private void CreateView() {
        txtKode = v.findViewById(R.id.txtKode);
        txtNama = v.findViewById(R.id.txtNama);
        txtNIK = v.findViewById(R.id.txtNIK);
        txtNPWP = v.findViewById(R.id.txtNPWP);
        txtAlamat = v.findViewById(R.id.txtAlamat);
        txtEmail = v.findViewById(R.id.txtEmail);
        txtNoTelp = v.findViewById(R.id.txtNoTelp);
        txtPlafond = v.findViewById(R.id.txtPlafond);
        txtSisaPiutang = v.findViewById(R.id.txtSisaPiutang);
        btnDownloadFaktur = v.findViewById(R.id.btnDownloadFaktur);
        btnDownloadHelp = v.findViewById(R.id.btnDownloadHelp);
        btnLogout = v.findViewById(R.id.btnLogout);
        btnUbahPassword = v.findViewById(R.id.btnUbahPassword);
        toolbar = (androidx.appcompat.widget.Toolbar) v.findViewById(R.id.toolbar);
        thisAppCompat = (AppCompatActivity) getActivity();
        thisAppCompat.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    private void InitClass() {

    }

    private void EventClass() {
        btnDownloadFaktur.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), FakturPajakActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        });
        btnDownloadHelp.setOnClickListener(view -> {
            FileDownloadHelper fileDownloaderHelper = new FileDownloadHelper(getActivity());
            fileDownloaderHelper.downloadFile(Routes.url_help(), "Petunjuk Penggunaan Aplikasi Customer Sinar Surya.pdf");
        });

        btnLogout.setOnClickListener(view -> {
            //clear session
            SharedPrefsUtils.setStringPreference(getContext(), "login_session", "");

            startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        });

        btnUbahPassword.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), UbahPasswordActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        });

    }

    private void LoadData() {
        ProgressDialog loading = Global.createProgresSpinner(getActivity(), "Loading");
        loading.show();
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(getContext(), "database_id", 0)
                + "&customer_seq=" + SharedPrefsUtils.getIntegerPreference(getContext(), "customer_seq", 0);
        String url = Routes.url_get_akun() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    try {
                        for (int i = 0; i < ArrResults.length(); i++) {
                            JSONObject obj = ArrResults.getJSONObject(i);

                            txtKode.setText(getStringValue(obj, "kode"));
                            txtNama.setText(getStringValue(obj, "nama"));
                            txtNIK.setText(getStringValue(obj, "nik"));
                            txtNPWP.setText(getStringValue(obj, "npwp"));
                            txtAlamat.setText(getStringValue(obj, "alamat"));
                            txtEmail.setText(getStringValue(obj, "email"));
                            txtNoTelp.setText(getStringValue(obj, "no_telp1"));
                            txtPlafond.setText(Global.FloatToStrFmt(obj.getDouble("plafond")));
                            txtSisaPiutang.setText(Global.FloatToStrFmt(obj.getDouble("sisa_piutang")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(getActivity(), pesan, Toast.LENGTH_SHORT).show();
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
            Global.handleError(getActivity(), error, runReconnect);
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    // Helper method to get a string value or return "-"
    private String getStringValue(JSONObject obj, String key) {
        String value = obj.optString(key);
        return (value.equals("null") || value.trim().isEmpty()) ? "-" : value;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_akun, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_kartu_piutang:
                Intent i = new Intent(getActivity(), KartuPiutang.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                return true;
            case R.id.menu_umur_piutang:
                Intent intentUmur = new Intent(getActivity(), UmurPiutang.class);
                intentUmur.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentUmur);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
