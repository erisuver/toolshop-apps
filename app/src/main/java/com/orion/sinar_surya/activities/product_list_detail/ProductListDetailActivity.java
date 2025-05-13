package com.orion.sinar_surya.activities.product_list_detail;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.activities.ZoomImageFull.ZoomImageFullActivity;
import com.orion.sinar_surya.activities.keranjang.KeranjangActivity;
import com.orion.sinar_surya.activities.pesanan.PesananActivity;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.JConst;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.models.ProductListDetailModel;
import com.orion.sinar_surya.models.SatuanModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListDetailActivity  extends AppCompatActivity {
    //var componen
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private ImageView imgCart, imgBack;
    private PhotoView imgBarang;
    private RecyclerView rcvLoad;
    private TextView jmlCart, txtNamaBarang, txtHargaAkhir, txtHargaAwal, txtDiskon, txtKeterangan, txtNamaInduk;
    private ChipGroup chipVariasi, chipSatuan;
    private Button btnKeranjang;

    //var for adapter/list
    private ProductListDetailAdapter mAdapter;
    public List<ProductListDetailModel> ListItems = new ArrayList<>();
    private List<ProductListDetailModel> ListGambar = new ArrayList<>();
    private List<ProductListDetailModel> ListVariasi = new ArrayList<>();
    private List<SatuanModel> ListSatuan = new ArrayList<>();
    private ImagePagerAdapter imagePagerAdapter;
    private ViewPager viewPager;

    //var global
    private int seqBarangPilih;
    private int seqSatuanPilih;
    private String namaInduk;
    private String pathGambar;
    private String pathGambarInduk;

    private TextView textViewCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_detail);

        CreateView();
        InitClass();
        EventClass();
        LoadData();


    }

    private void CreateView() {
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        imgBarang = findViewById(R.id.imgBarang);
        imgCart = findViewById(R.id.imgCart);
        imgBack = findViewById(R.id.imgBack);
        rcvLoad = findViewById(R.id.rcvLoad);
        jmlCart = findViewById(R.id.jmlCart);
        txtNamaBarang = findViewById(R.id.txtNamaBarang);
        txtHargaAkhir = findViewById(R.id.txtHargaAkhir);
        txtHargaAwal = findViewById(R.id.txtHargaAwal);
        txtDiskon = findViewById(R.id.txtDiskon);
        txtKeterangan = findViewById(R.id.txtKeterangan);
        txtNamaInduk = findViewById(R.id.txtNamaInduk);
        chipVariasi = findViewById(R.id.chipVariasi);
        chipSatuan = findViewById(R.id.chipSatuan);
        btnKeranjang = findViewById(R.id.btnKeranjang);
        viewPager = findViewById(R.id.viewPager);
        textViewCounter = findViewById(R.id.textViewCounter);
    }

    private void InitClass() {
        Bundle extra = this.getIntent().getExtras();
        namaInduk = extra.getString("nama_induk");
        pathGambarInduk = extra.getString("path_gambar_induk");
        SetJenisTampilan();

        jmlCart.setText("0");
        txtNamaBarang.setText("");
        txtHargaAkhir.setText("Rp. 0");
        txtHargaAwal.setText("Rp. 0");
        txtDiskon.setText("0");
        txtKeterangan.setText("");
        txtNamaInduk.setText("");
        chipVariasi.removeAllViews();
        chipSatuan.removeAllViews();
        imagePagerAdapter = new ImagePagerAdapter(this, ListGambar);
        viewPager.setAdapter(imagePagerAdapter);
        imgBarang.setZoomable(false);
    }

    private void EventClass() {
        btnKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductListDetailActivity.this, KeranjangActivity.class).setFlags(FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position >= 0 && position < ListGambar.size()) {
                    ProductListDetailModel data = ListGambar.get(position); // Retrieve data from the ArrayList based on the position
                    textViewCounter.setText((position + 1) + "/" + ListGambar.size());
                    updateUI(data.getSeq(), 0);
                }
            }
        });
    }

    private void LoadData() {
        ProgressDialog loading = Global.createProgresSpinner(this, "Loading");
        loading.show();
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(this, "database_id", 0)
                + "&nama_induk=" + namaInduk
                + "&customer_seq=" + SharedPrefsUtils.getIntegerPreference(getApplicationContext(), "customer_seq", 0);

        String url = Routes.url_get_barang_for_detail() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                mAdapter.removeAllModel();
                imagePagerAdapter.removeAllModel();
                ListGambar = new ArrayList<>();
                ListVariasi = new ArrayList<>();
                ListSatuan = new ArrayList<>();
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    try {
                        JSONObject objResult = new JSONObject(response).getJSONObject("data");
                        JSONArray ArrResults = objResult.getJSONArray("detail_barang");
                        for (int i = 0; i < ArrResults.length(); i++) {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            ProductListDetailModel productListDetailModel;
                            productListDetailModel = createProductListDetailModel(obj, "file_1");
                            //masukan semua ke variasi untuk dibuat chip
                            ListVariasi.add(productListDetailModel);

                            // Cek dan tambahkan objek baru ke ListGambar sesuai dengan file yang ada
                            if (!obj.getString("file_1").isEmpty() && !obj.getString("file_1").equals("null")) {
                                ListGambar.add(createProductListDetailModel(obj, "file_1"));
                            }
                            if (!obj.getString("file_2").isEmpty() && !obj.getString("file_2").equals("null")) {
                                ListGambar.add(createProductListDetailModel(obj, "file_2"));
                            }
                            if (!obj.getString("file_3").isEmpty() && !obj.getString("file_3").equals("null")) {
                                ListGambar.add(createProductListDetailModel(obj, "file_3"));
                            }
                        }
                        JSONArray ArrSatuan = objResult.getJSONArray("satuan_ids");
                        for (int i = 0; i < ArrSatuan.length(); i++) {
                            JSONObject obj = ArrSatuan.getJSONObject(i);
                            SatuanModel satuanModel = new SatuanModel(
                                    obj.getInt("seq"),
                                    obj.getInt("seq_barang"),
                                    obj.getString("satuan"),
                                    obj.getDouble("harga")
                            );
                            //masukan semua ke satuan untuk dibuat chip
                            ListSatuan.add(satuanModel);
                        }

                        //isi atau update UI
                        createVariasiChip();
                        mAdapter.addModels(ListGambar);
                        if (ListGambar.size() == 0) {
                            rcvLoad.setVisibility(View.GONE);
                            imgBarang.setVisibility(View.VISIBLE);
                            viewPager.setVisibility(View.GONE);
                            textViewCounter.setVisibility(View.GONE);
                        }else{
;
                            viewPager.setVisibility(View.VISIBLE);
                            rcvLoad.setVisibility(View.VISIBLE);
                            imgBarang.setVisibility(View.GONE);
                            textViewCounter.setVisibility(View.VISIBLE);
                        }

                        imagePagerAdapter.addModels(ListGambar);
                        textViewCounter.setText(1 + "/" + ListGambar.size());

                        loading.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
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
            Global.handleError(ProductListDetailActivity.this, error, runReconnect);
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    // Helper method untuk membuat objek ProductListDetailModel baru
    private ProductListDetailModel createProductListDetailModel(JSONObject obj, String fileKey) throws JSONException {
        return new ProductListDetailModel(
                obj.getInt("seq"),
                obj.getString("nama_induk"),
                obj.getString(fileKey),
                obj.getDouble("harga_awal"),
                obj.getDouble("harga_akhir"),
                obj.getDouble("diskon_pct"),
                obj.getString("variasi").isEmpty() ? obj.getString("nama_induk") : obj.getString("variasi"),
                obj.getString("deskripsi"),
                obj.getInt("satuan_primer_seq"),
                obj.getString("nama_satuan_primer")
        );
    }

    private void SetJenisTampilan() {
        // Use LinearLayoutManager with horizontal orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcvLoad.setLayoutManager(linearLayoutManager);

        // Set the adapter and other configurations
        mAdapter = new ProductListDetailAdapter(this, ListItems, R.layout.list_item_product_list_detail, this);
        rcvLoad.setAdapter(mAdapter);
        rcvLoad.setLayoutManager(linearLayoutManager);

        rcvLoad.setPadding(0, 0, 0, 0);
        rcvLoad.setClipToPadding(false);
    }

    private void createVariasiChip() {
        chipVariasi.removeAllViews();

        for (int i = 0; i < ListVariasi.size(); i++) {
            ProductListDetailModel item = ListVariasi.get(i);

            // Create chip
            Chip chip = new Chip(this);
            chip.setText(item.getVariasi());
            chip.setChecked(false);
            chip.setCloseIcon(null);

            //set default checked
            //jika dibuka dari form dialog maka cek seq pilih, jika ada maka default dari seq pilih tersebut
            if (item.getSeq() == seqBarangPilih || seqBarangPilih == 0) {
                setThemeChipClicked(chip, true);
                updateUI(item.getSeq(), seqSatuanPilih);
                if (!item.getGambar().isEmpty() && !item.getGambar().equals("null")) {
                    pathGambar = Routes.url_path_gambar_barang() + "/" + item.getGambar();
                } else {
                    pathGambar = pathGambarInduk;
                }
                setGambar(pathGambar);
                int idx = 0;
                if (!item.getGambar().isEmpty() && !item.getGambar().equals("null")) {
                    for (int j = 0; j < ListGambar.size(); j++) {
                        if (ListGambar.get(j).getGambar().equals(item.getGambar())) {
                            idx = j;
                            break;
                        }
                    }
                }
                setActiveImage(idx);
                createSatuanChip();
            }

            chip.setOnClickListener(v -> {
                // Set color based on the updated state
                uncheckAllChips(chipVariasi);
                setThemeChipClicked(chip, true);
                updateUI(item.getSeq(), item.getSeqSatuan());
                createSatuanChip();
                if (!item.getGambar().isEmpty() && !item.getGambar().equals("null")) {
                    pathGambar = Routes.url_path_gambar_barang() + "/" + item.getGambar();
                } else {
                    pathGambar = pathGambarInduk;
                }
                setGambar(pathGambar);
                int idx = 0;
                if (!item.getGambar().isEmpty() && !item.getGambar().equals("null")) {
                    for (int j = 0; j < ListGambar.size(); j++) {
                        if (ListGambar.get(j).getGambar().equals(item.getGambar())) {
                            idx = j;
                            break;
                        }
                    }
                }
                setActiveImage(idx);
            });

            chipVariasi.addView(chip);
        }

    }

    private void createSatuanChip() {
        chipSatuan.removeAllViews();

        for (int i = 0; i < ListVariasi.size(); i++) {
            ProductListDetailModel item = ListVariasi.get(i);

            if (item.getSeq() == seqBarangPilih || seqBarangPilih == 0) {
                // Create chip
                Chip chip = new Chip(this);
                chip.setText(item.getNamaSatuan());
                chip.setChecked(false);
                chip.setCloseIcon(null);
                //default ceklis jika satuan sama
                setThemeChipClicked(chip, item.getSeqSatuan() == seqSatuanPilih || seqSatuanPilih == 0);
                if (seqSatuanPilih == 0) {
                    //set default checked
                    setThemeChipClicked(chip, true);
                    seqSatuanPilih = item.getSeqSatuan();
                } else {
                    setThemeChipClicked(chip, item.getSeqSatuan() == seqSatuanPilih);
                }
                chip.setOnClickListener(v -> {
                    // Set color based on the updated state
                    uncheckAllChips(chipSatuan);
                    setThemeChipClicked(chip, true);
                    updateUI(item.getSeq(), item.getSeqSatuan());
                });

                chipSatuan.addView(chip);
            }
        }

        for (int i = 0; i < ListSatuan.size(); i++) {
            SatuanModel item = ListSatuan.get(i);

            if (item.getSeq_barang() == seqBarangPilih || seqBarangPilih == 0) {
                // Create chip
                Chip chip = new Chip(this);
                chip.setText(item.getSatuan());
                chip.setChecked(false);
                chip.setCloseIcon(null);
                //default ceklis jika satuan sama
                setThemeChipClicked(chip, item.getSeq() == seqSatuanPilih);

                chip.setOnClickListener(v -> {
                    // Set color based on the updated state
                    uncheckAllChips(chipSatuan);
                    setThemeChipClicked(chip, true);
                    updateUI(item.getSeq_barang(), item.getSeq());
                });

                chipSatuan.addView(chip);
            }
        }
    }

    private boolean setThemeChipClicked(Chip chip, boolean chipClicked) {
        chip.setCloseIconVisible(true);
        chip.setChecked(true);
        if (!(chipClicked)) {
            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.gray300));
            chip.setTextColor(getResources().getColorStateList(R.color.black));
            chipClicked = true;
        } else if (chipClicked) {
            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.sinar_surya_color_6));
            chip.setTextColor(getResources().getColorStateList(R.color.white));
            chipClicked = false;
        }
        return chipClicked;
    }

    private void uncheckAllChips(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setChecked(false);
                setThemeChipClicked(chip, false);
            }
        }
    }

    public void updateUI(int seqBarang, int seqSatuan) {
        double diskon = 0;
        double harga = 0;
        for (int i = 0; i < ListVariasi.size(); i++) {
            ProductListDetailModel data = ListVariasi.get(i);
            if (seqBarang == data.getSeq()) {
                txtNamaInduk.setText(data.getNama());
                txtNamaBarang.setText(data.getVariasi());
                txtDiskon.setText(String.format("%s%%", Global.FloatToStrFmt(data.getDiskon_pct())));
                txtHargaAkhir.setText(Global.FloatToStrFmt(data.getHarga_akhir(), true));
                txtHargaAwal.setText(Global.FloatToStrFmt(data.getHarga_awal(), true));
                txtKeterangan.setText(data.getDeskripsi());
                diskon = data.getDiskon_pct();
                if (diskon > 0) {
                    txtHargaAwal.setVisibility(View.VISIBLE);
                    txtDiskon.setVisibility(View.VISIBLE);
                    txtHargaAwal.setPaintFlags(txtHargaAwal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    txtHargaAwal.setVisibility(View.GONE);
                    txtDiskon.setVisibility(View.GONE);
                }
                break;
            }
        }
        if (seqSatuan > 0) {
            for (int i = 0; i < ListSatuan.size(); i++) {
                SatuanModel data = ListSatuan.get(i);
                if (seqSatuan == data.getSeq() && seqBarang == data.getSeq_barang()) {
                    harga = data.getHarga();
                    txtDiskon.setText(String.format("%s%%", Global.FloatToStrFmt(diskon)));
                    txtHargaAkhir.setText(Global.FloatToStrFmt(harga - (harga * diskon / 100), true));
                    txtHargaAwal.setText(Global.FloatToStrFmt(harga, true));
                    if (diskon > 0) {
                        txtHargaAwal.setVisibility(View.VISIBLE);
                        txtDiskon.setVisibility(View.VISIBLE);
                        txtHargaAwal.setPaintFlags(txtHargaAwal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        txtHargaAwal.setVisibility(View.GONE);
                        txtDiskon.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        }
        seqBarangPilih = seqBarang;
        seqSatuanPilih = seqSatuan;
    }

    public void setGambar(String path) {
        Picasso.get().load(path.replace("%", "%25")).into(imgBarang);
        if (imgBarang.getDrawable() == null) {
            imgBarang.setImageResource(R.drawable.gambar_tidak_tersedia);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_detail_pesanan);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        // createview replace view
        txtNamaInduk = dialog.findViewById(R.id.txtNamaInduk);
        txtNamaBarang = dialog.findViewById(R.id.txtNamaBarang);
        txtHargaAkhir = dialog.findViewById(R.id.txtHargaAkhir);
        txtHargaAwal = dialog.findViewById(R.id.txtHargaAwal);
        txtDiskon = dialog.findViewById(R.id.txtDiskon);
        chipVariasi = dialog.findViewById(R.id.chipVariasi);
        chipSatuan = dialog.findViewById(R.id.chipSatuan);
        imgBarang = dialog.findViewById(R.id.imgBarang);
        ImageButton btnClose = dialog.findViewById(R.id.btnClose);
        Button btnKeranjang = dialog.findViewById(R.id.btnKeranjang);


        createVariasiChip();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = dialog.findViewById(android.R.id.content);
                if (isValid()) {
                    saveKeranjang(dialogView);

                    dialog.dismiss();
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                CreateView();
            }
        });
        imgBarang.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {// Tangani event klik di sini
                    Intent s = new Intent(ProductListDetailActivity.this, ZoomImageFullActivity.class);
                    s.putExtra("GAMBAR", pathGambar);
                    s.putExtra("NAMA", "gambar" + seqBarangPilih);
                    startActivity(s);
                }
                return true;
            }
        });
        dialog.show();
    }

    private void showBottomDialogSucces() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_detail_pesanan_berhasil);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        // createview replace view
        imgBarang = dialog.findViewById(R.id.imgBarang);
        ImageButton btnClose = dialog.findViewById(R.id.btnClose);
        Button btnKeranjang = dialog.findViewById(R.id.btnKeranjang);

        setGambar(pathGambar);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductListDetailActivity.this, KeranjangActivity.class));
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                CreateView();
            }
        });
        dialog.show();
    }

    private boolean isValid(){
        boolean isValid = true;
        if (seqBarangPilih == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Variasi Barang belum di pilih.", Snackbar.LENGTH_SHORT).show();
            isValid = false;
        }
        if (seqSatuanPilih == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Satuan belum di pilih.", Snackbar.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void saveKeranjang(View view) {
        ProgressDialog loading = Global.createProgresSpinner(this, "Loading");
        loading.show();
        String url = Routes.url_save_keranjang();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                View dialogView = view.findViewById(android.R.id.content);
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Snackbar.make(dialogView, jsonObjectData.getString("message"), Snackbar.LENGTH_SHORT).show();
                } else {
                    loading.dismiss();
//                    Snackbar.make(dialogView, "Barang berhasil ditambahkan ke keranjang.", Snackbar.LENGTH_SHORT).show();
                    GetJumlahKeranjang();
                    showBottomDialogSucces();
                }
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("database_id", String.valueOf(SharedPrefsUtils.getIntegerPreference(ProductListDetailActivity.this, "database_id", 0)));
                params.put("customer_seq", String.valueOf(SharedPrefsUtils.getIntegerPreference(ProductListDetailActivity.this, "customer_seq", 0)));
                params.put("barang_seq", String.valueOf(seqBarangPilih));
                params.put("satuan_seq", String.valueOf(seqSatuanPilih));
                return params;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    private void GetJumlahKeranjang() {
        String filter = "?database_id=" + SharedPrefsUtils.getIntegerPreference(this, "database_id", 0)
                + "&customer_seq=" + SharedPrefsUtils.getIntegerPreference(this, "customer_seq", 0);
        String url = Routes.url_get_jumlah_keranjang() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    JSONArray ArrResults = new JSONObject(response).getJSONArray("data");
                    try {
                        for (int i = 0; i < ArrResults.length(); i++) {
                            JSONObject obj = ArrResults.getJSONObject(i);
                            jmlCart.setText(obj.getString("jml"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetJumlahKeranjang();
    }

    public void setActiveImage(int idx){
        viewPager.setCurrentItem(idx);
    }
}