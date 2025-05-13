package com.orion.sinar_surya.activities.login;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.home.HomeActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.globals.ShowDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private ImageButton btnLogin;
    private TextInputEditText txtUserId, txtPassword;
    private CheckBox chbRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CreateView();
        InitView();
        EventView();
    }

    private void CreateView() {
        btnLogin = findViewById(R.id.btnLogin);
        txtUserId = findViewById(R.id.txtUserId);
        txtPassword = findViewById(R.id.txtPassword);
        chbRememberMe = findViewById(R.id.chbRememberMe);
    }

    private void InitView() {
        txtUserId.setText("");
        txtPassword.setText("");
        txtPassword.setHint("Password");
        txtUserId.setHint("Kode Login");
        boolean isRememberMe = SharedPrefsUtils.getBooleanPreference(this, "remember_me", false);
        chbRememberMe.setChecked(isRememberMe);
        if (isRememberMe){
            txtUserId.setText(SharedPrefsUtils.getStringPreference(this, "last_user"));
        }
    }

    private void EventView() {
        btnLogin.setOnClickListener(view -> {
            if (isValid()) {
                StartLogin();
            }
        });
        chbRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "remember_me", true);
                }else{
                    SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "remember_me", false);
                }
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    txtPassword.setHint("");
                } else {
                    txtPassword.setHint("Password");
                }
            }
        });
        txtUserId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    txtUserId.setHint("");
                } else {
                    txtUserId.setHint("Kode Login");
                }
            }
        });
    }

    private boolean isValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(txtUserId.getText().toString())) {
            // Set error jika teks kosong
            txtUserId.setError("Kode Login tidak boleh kosong");
            isValid = false;
        } else {
            // Hapus pesan kesalahan jika teks tidak kosong
            txtUserId.setError(null);
        }
        if (TextUtils.isEmpty(txtPassword.getText().toString())) {
            // Set error jika teks kosong
            txtPassword.setError("Password tidak boleh kosong");
            isValid = false;
        } else {
            // Hapus pesan kesalahan jika teks tidak kosong
            txtPassword.setError(null);
        }
        return isValid;
    }

    private void StartLogin(){
        ProgressDialog loading = Global.createProgresSpinner(this, "Loading");
        loading.show();
        String url = Routes.url_login();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Snackbar.make(LoginActivity.this.findViewById(android.R.id.content), jsonObjectData.getString("message"), Snackbar.LENGTH_SHORT).show();
                } else {
                    JSONObject obj = new JSONObject(response).getJSONObject("result");
                    int DATABASE_ID = obj.getInt("database_id");
                    int CUST_SEQ = obj.getInt("seq");
                    String NAMA_CUST = obj.getString("nama");
                    String ALAMAT_CUST = obj.getString("alamat_1");
                    String KOTA_CUST = obj.getString("kota");
                    String PROPINSI_CUST = obj.getString("propinsi");
                    String NO_TELP_CUST = obj.getString("no_telp1");

                    //masukan ke sharepref
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "login_session", txtUserId.getText().toString());
                    SharedPrefsUtils.setIntegerPreference(getApplicationContext(), "database_id", DATABASE_ID);
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "last_user", txtUserId.getText().toString());
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "last_password", txtPassword.getText().toString());
                    SharedPrefsUtils.setIntegerPreference(getApplicationContext(), "customer_seq", CUST_SEQ);
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "nama_customer", NAMA_CUST);
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "alamat_customer", ALAMAT_CUST);
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "kota_customer", KOTA_CUST);
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "propinsi_customer", PROPINSI_CUST);
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "no_telp_customer", NO_TELP_CUST);

                    JApplication.getInstance().isLoggedIn = true;
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    loading.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
                if (e.getMessage().contains("Invalid User ID")){
                    Snackbar.make(LoginActivity.this.findViewById(android.R.id.content), "Kode Login atau Password tidak sesuai.", Snackbar.LENGTH_SHORT).show();
                }else {
                    Snackbar.make(LoginActivity.this.findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }

            }
        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                //isi value : masukan ke param
                Map<String, String> params = new HashMap<String, String>();
                params.put("kode_login",txtUserId.getText().toString());
                params.put("password",txtPassword.getText().toString());
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}
