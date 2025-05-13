package com.orion.sinar_surya.activities.laporan.kartu_piutang;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.orion.sinar_surya.JApplication;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.Routes;
import com.orion.sinar_surya.globals.FungsiGeneral;
import com.orion.sinar_surya.globals.Global;
import com.orion.sinar_surya.globals.ILoadMore;
import com.orion.sinar_surya.globals.PDFHelper;
import com.orion.sinar_surya.globals.SharedPrefsUtils;
import com.orion.sinar_surya.models.KartuPiutangModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.orion.sinar_surya.globals.FungsiGeneral.getSimpleDate;
import static com.orion.sinar_surya.globals.FungsiGeneral.getTglFormat;
import static com.orion.sinar_surya.globals.FungsiGeneral.getTglFormatMySql;
import static com.orion.sinar_surya.globals.Global.hideSoftKeyboard;
import static com.orion.sinar_surya.globals.JConst.PATH_PDF;

public class KartuPiutang extends AppCompatActivity {
    private TextView txtDari, txtSampai;
    private TextView txtTotalFaktur, txtTotalPembayaran, txtTotalTitipan, txtTotalAkhir, txtSaldoAwal;
    public RecyclerView rcvData;
    private SwipeRefreshLayout swipe;
    private KartuPiutangAdapter adapter;
    public List<KartuPiutangModel> ListItems = new ArrayList<>();
    private ArrayList<KartuPiutangModel> datas;
    private CardView cvSaldoAwal, cvHeader, cvFooter;
//    public List<KartuPiutangPrintModel> itemDataPrintModels = new ArrayList<>();
    private ILoadMore loadMore;
    Button btnPrint;

    int lastVisibleItem, totalItemCount;
    boolean IsLoading;
    private boolean isStart;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

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
        setContentView(R.layout.activity_kartu_piutang);
        CreateView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InitClass();
        EventClass();
    }

    private void CreateView(){
        this.cvSaldoAwal = (CardView) findViewById(R.id.cvSaldoAwal);
        this.cvHeader = (CardView) findViewById(R.id.cvHeader);
        this.cvFooter = (CardView) findViewById(R.id.cvFooter);
        this.rcvData = (RecyclerView) findViewById(R.id.rcvData);
        this.swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        txtDari   = (TextView) findViewById(R.id.txtDari);
        txtSampai = (TextView) findViewById(R.id.txtSampai);
        txtSaldoAwal = (TextView) findViewById(R.id.txtSaldoAwal);
        txtTotalFaktur = (TextView) findViewById(R.id.txtTotalFaktur);
        txtTotalPembayaran = (TextView) findViewById(R.id.txtTotalPembayaran);
        txtTotalTitipan = (TextView) findViewById(R.id.txtTotalTitipan);
        txtTotalAkhir = (TextView) findViewById(R.id.txtTotalAkhir);

        btnPrint = (Button) findViewById(R.id.btnPrint);
        datas = new ArrayList<KartuPiutangModel>();
        this.adapter = new KartuPiutangAdapter(KartuPiutang.this, ListItems, KartuPiutang.this, R.layout.list_item_kartu_piutang);
        rcvData.setAdapter(adapter);

        
    }

    private void InitClass(){
        this.setTitle("Kartu Piutang");
        rcvData.setLayoutManager(new LinearLayoutManager(KartuPiutang.this));
        linearLayoutManager = (LinearLayoutManager)rcvData.getLayoutManager();
        this.adapter.removeAllModel();

        adapter.notifyDataSetChanged();

        swipe.setRefreshing(false);

        tgl_dari   = Global.serverNowStartOfTheMonthLong();
        tgl_Sampai = Global.EndOfTheMonthLong(Global.serverNowLong());

        txtDari.setText(Global.serverNowFormated("dd-MM-yyyy"));
        txtSampai.setText(Global.serverNowFormated("dd-MM-yyyy"));
        txtDari.setFocusable(false);
        txtSampai.setFocusable(false);
        this.Loading = new ProgressDialog(this);

    }

    protected void EventClass(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rcvData.scrollToPosition(0);
                adapter.removeAllModel();
                RefreshRecyclerView();
                swipe.setRefreshing(false);
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                GetPermission();
            }
        });


        setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.removeMoel(ListItems.size()-1);
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

        txtDari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(KartuPiutang.this, KartuPiutang.this.findViewById(android.R.id.content));
                Long tgl = FungsiGeneral.getMillisDateFmt(txtDari.getText().toString(), "dd-MM-yyyy");
                int mYear = (Integer.parseInt(FungsiGeneral.getTahun(tgl)));
                int mMonth = (Integer.parseInt(FungsiGeneral.getBulan(tgl)));
                int mDay = (Integer.parseInt(FungsiGeneral.getHari(tgl)));

                DatePickerDialog datePickerDialog = new DatePickerDialog(KartuPiutang.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                String tglDariTemp = format.format(calendar.getTime());
                                txtDari.setText(tglDariTemp);

                                rcvData.scrollToPosition(0);
                                adapter.removeAllModel();
                                RefreshRecyclerView();
                            }
                        }, mYear, mMonth-1, mDay);
                datePickerDialog.show();
            }
        });

        txtSampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(KartuPiutang.this, KartuPiutang.this.findViewById(android.R.id.content));
                Long tgl = FungsiGeneral.getMillisDateFmt(txtSampai.getText().toString(), "dd-MM-yyyy");
                int mYear = (Integer.parseInt(FungsiGeneral.getTahun(tgl)));
                int mMonth = (Integer.parseInt(FungsiGeneral.getBulan(tgl)));
                int mDay = (Integer.parseInt(FungsiGeneral.getHari(tgl)));

                DatePickerDialog datePickerDialog = new DatePickerDialog(KartuPiutang.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                String tglSampaiTemp = format.format(calendar.getTime());
                                txtSampai.setText(tglSampaiTemp);

                                rcvData.scrollToPosition(0);
                                adapter.removeAllModel();
                                RefreshRecyclerView();
                            }
                        }, mYear, mMonth-1, mDay);
                datePickerDialog.show();
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

        tgl_dari   = getSimpleDate(txtDari.getText().toString());
        tgl_Sampai = getSimpleDate(txtSampai.getText().toString());
        String Filter = "";
        int dbId = SharedPrefsUtils.getIntegerPreference(KartuPiutang.this, "database_id", 0);
        int custSeq = SharedPrefsUtils.getIntegerPreference(KartuPiutang.this, "customer_seq", 0);
        Filter += "?database_id=" + dbId +
                "&dari="+getTglFormatMySql(tgl_dari)+"&sampai="+getTglFormatMySql(tgl_Sampai)+
               "&customer_seq="+custSeq;
        String url = Routes.url_load_kartu_piutang() + Filter;
        JsonArrayRequest jArr = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                List<KartuPiutangModel> itemDataModels = new ArrayList<>();
                KartuPiutangModel Data;
//                KartuPiutangPrintModel Data1;
                itemDataModels.clear();
                datas.clear();
//                itemDataPrintModels.clear();

                double saldo = 0;
                double titipan = 0;
                double faktur = 0;
                double pelunasan = 0;

                for (int i = 0; i < response.length(); i++) {
                    try { JSONObject obj = response.getJSONObject(i);
                        if (i == 0){
                            saldo = obj.getDouble("saldo");
                            titipan = obj.getDouble("titipan");
                        }

                        if (!obj.getString("nomor").equals("DATATIDAKADA") && !obj.getString("nomor").equals("null")){
                            Data = new KartuPiutangModel(
                                    obj.getString("tgl"),
                                    obj.getString("nomor"),
                                    obj.getDouble("total"),
                                    obj.getString("tgl_jt")
                            );
                            itemDataModels.add(Data);
                            if (obj.getDouble("total") >= 0){
                                faktur += obj.getDouble("total");
                            }else{
                                pelunasan += Math.abs(obj.getDouble("total"));
                            }
//                            Data1 = new KartuPiutangPrintModel(
//                                    obj.getString("tanggal"),
//                                    obj.getString("no_trans"),
//                                    obj.getString("tgl_jth_tempo"),
//                                    FungsiGeneral.FloatToStrFmt(obj.getDouble("debet")),
//                                    FungsiGeneral.FloatToStrFmt(obj.getDouble("saldo_akhir")),
//                                    JConst.TIPE_VIEW_DETAIL
//                            );
//                            itemDataPrintModels.add(Data1);
                            datas.add(Data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(KartuPiutang.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        swipe.setRefreshing(false);
                    }
                }
                txtSaldoAwal.setText(Global.FloatToStrFmt(saldo));
                txtTotalFaktur.setText(Global.FloatToStrFmt(faktur));
                txtTotalPembayaran.setText(Global.FloatToStrFmt(pelunasan));
                txtTotalTitipan.setText(Global.FloatToStrFmt(titipan));
                txtTotalAkhir.setText(Global.FloatToStrFmt(saldo +faktur - pelunasan - titipan));
//                EthicaApplication.getInstance().setArrayKartuPiutang(itemDataPrintModels);
                adapter.addModels(itemDataModels);
                setLoaded(false);
                swipe.setRefreshing(false);
                Loading.dismiss();
                if (datas.size() == 0){
                    btnPrint.setEnabled(false);
                }else{
                    btnPrint.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                swipe.setRefreshing(false);
                setLoaded(false);
                String errorMessage = e.getMessage();
                Toast.makeText(KartuPiutang.this, errorMessage, Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void savePdf(){
        try {
//            KartuPiutangModel data = new KartuPiutangModel("", "", 0, "");
//            data.setHeader(true);
//            data.setSaldoAwal(txtSaldoAwal.getText().toString());
//            adapter.addMoel(0, data);
//
//            KartuPiutangModel data2 = new KartuPiutangModel("", "", 0, "");
//            data2.setFooter(true);
//            data2.setTotalAkhir(txtTotalAkhir.getText().toString());
//            data2.setTotalFaktur(txtTotalFaktur.getText().toString());
//            data2.setTotalPeluanasan(txtTotalPembayaran.getText().toString());
//            data2.setTotalTitipan(txtTotalTitipan.getText().toString());
//            adapter.addMoel(adapter.getItemCount(), data2);
            Bitmap bmpSaldo = FungsiGeneral.getCardViewScreenshot(cvSaldoAwal);
            Bitmap bmpHeader = FungsiGeneral.getCardViewScreenshot(cvHeader);
            Bitmap bmpAkhir = FungsiGeneral.mergeBitmaps(bmpSaldo, bmpHeader);

            Bitmap bmp = FungsiGeneral.getRecyclerViewScreenshot(rcvData);
            bmpAkhir = FungsiGeneral.mergeBitmaps(bmpAkhir, bmp);
            Bitmap bmpFooter = FungsiGeneral.getCardViewScreenshot(cvFooter);
            bmpAkhir = FungsiGeneral.mergeBitmaps(bmpAkhir, bmpFooter);
            File f = new File(getCacheDir(), "testgambar.png");
            f.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 0 , bos);
            byte[] bitmapdata = bos.toByteArray();

            File f2 = new File(getCacheDir(), "pdf");
            PDFHelper pdf = new PDFHelper(f2, KartuPiutang.this);
            final String Filename = "Kartu Piutang "+ txtDari.getText()+" sd "+txtSampai.getText();
            String path = Environment.getExternalStorageDirectory() +"/" + Environment.DIRECTORY_DOCUMENTS+ "/"+PATH_PDF+"/"+Filename+".pdf";
            imageToPdf(bmpAkhir, path);


            final String pathAkhir = path;
            final AlertDialog dialogInform = new AlertDialog.Builder(KartuPiutang.this).create();
            dialogInform.setMessage("Apakah data akan dikirim");
            dialogInform.setCancelable(true);
            dialogInform.setButton(DialogInterface.BUTTON_POSITIVE, "Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                    kirimpdf(pathAkhir);
                }
            });
            dialogInform.setButton(DialogInterface.BUTTON_NEGATIVE, "Tidak", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int buttonId) {
                    Toast.makeText(KartuPiutang.this, "data sudah disimpan di Internal_Storage/"+PATH_PDF+"/"+Filename+".pdf", Toast.LENGTH_SHORT).show();
                    dialogInform.dismiss();
                }
            });
            dialogInform.setIcon(android.R.drawable.ic_dialog_alert);
            dialogInform.show();

//            adapter.removeMoel(adapter.getItemCount()-1);
//            adapter.removeMoel(0);
        } catch (Exception e) {
            Log.e("PrintActivity", "Exe ", e);
        }
        rcvData.scrollToPosition(0);
        adapter.removeAllModel();
        RefreshRecyclerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void imageToPdf(Bitmap bitmap, String filename){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);


        // write the document content
        String targetPdf = filename;
        File filePath = new File(targetPdf);

        File dir = new File(Environment.getExternalStorageDirectory() + "/"+Environment.DIRECTORY_DOCUMENTS+ "/"+PATH_PDF);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }

    private void kirimpdf(String filename){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(filename);

        if (fileWithinMyDir.exists()) {
            intentShareFile.setType("application/pdf");
            Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", fileWithinMyDir);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, path);

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Kartu Piutang");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Kartu Piutang "+ txtDari.getText()+" sd "+txtSampai.getText());

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void GetPermission(){
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){  //kalo android 13 ga butuh permission
            //permission granted (berhasil) lakukan sesuatu disini atau biarkan kosong
            KartuPiutang.this.savePdf();
        }else if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show permission explanation dialog...
            } else {
                // No explanation needed; request the permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permission request code is just an int
                } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permisison request code is just an int
                }
            }
        }else {
            //permission granted (berhasil) lakukan sesuatu disini atau biarkan kosong

            KartuPiutang.this.savePdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetPermission();
                } else {
                    // Permission Denied
                        Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
