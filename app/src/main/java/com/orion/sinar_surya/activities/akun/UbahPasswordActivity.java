package com.orion.sinar_surya.activities.akun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.SharedPrefsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UbahPasswordActivity extends AppCompatActivity {
    //var componen
    private ImageButton btnChangePassword;
    private EditText txtPasswordLama, txtPasswordBaru, txtPasswordKonfirmasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        btnChangePassword = findViewById(R.id.btnChangePassword);
        txtPasswordLama = findViewById(R.id.txtPasswordLama);
        txtPasswordBaru = findViewById(R.id.txtPasswordBaru);
        txtPasswordKonfirmasi = findViewById(R.id.txtPasswordKonfirmasi);
    }

    private void InitClass() {
        ResetData();
    }

    private void EventClass() {
        btnChangePassword.setOnClickListener(view -> {
            if (isValid()) {
                ChangePassword();
            }
        });
    }

    private void ResetData() {
        txtPasswordLama.setText("");
        txtPasswordBaru.setText("");
        txtPasswordKonfirmasi.setText("");
    }

    private boolean isValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(txtPasswordLama.getText().toString())) {
            txtPasswordLama.setError("Password Lama tidak boleh kosong.");
            isValid = false;
        }
        if (TextUtils.isEmpty(txtPasswordBaru.getText().toString())) {
            txtPasswordBaru.setError("Password Baru tidak boleh kosong.");
            isValid = false;
        }
        if (TextUtils.isEmpty(txtPasswordKonfirmasi.getText().toString())) {
            txtPasswordKonfirmasi.setError("Konfirmasi Password tidak boleh kosong.");
            isValid = false;
        }

        String passwordLama = SharedPrefsUtils.getStringPreference(this, "last_password");
        if (!txtPasswordLama.getText().toString().equals(passwordLama)) {
            txtPasswordLama.setError("Password Lama salah");
            isValid = false;
        }
        if (!txtPasswordBaru.getText().toString().equals(txtPasswordKonfirmasi.getText().toString())) {
            txtPasswordBaru.setError("Password Baru dan Konfirmasi Password tidak sama.");
            txtPasswordKonfirmasi.setError("Password Baru dan Konfirmasi Password tidak sama.");
            isValid = false;
        }

        return isValid;
    }

    private void ChangePassword() {
        ProgressDialog loading = Global.createProgresSpinner(this, "Loading");
        loading.show();
        String url = Routes.url_ubah_password();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Snackbar.make(UbahPasswordActivity.this.findViewById(android.R.id.content), jsonObjectData.getString("message"), Snackbar.LENGTH_SHORT).show();
                } else {
                    loading.dismiss();
                    Runnable runOk = new Runnable() {
                        @Override
                        public void run() {
                            SharedPrefsUtils.setStringPreference(getApplicationContext(), "last_password", txtPasswordBaru.getText().toString());
                            onBackPressed();
                        }
                    };
                    Global.inform(UbahPasswordActivity.this, "Password berhasil diubah.","Informasi", runOk);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
                Snackbar.make(UbahPasswordActivity.this.findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                //isi value : masukan ke param
                Map<String, String> params = new HashMap<String, String>();
                params.put("database_id", String.valueOf(SharedPrefsUtils.getIntegerPreference(UbahPasswordActivity.this, "database_id", 0)));
                params.put("customer_seq", String.valueOf(SharedPrefsUtils.getIntegerPreference(UbahPasswordActivity.this, "customer_seq", 0)));
                params.put("password_baru",txtPasswordBaru.getText().toString());
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}