package com.orion.sinar_surya.activities.ZoomImageFull;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.orion.sinar_surya.R;
import com.orion.sinar_surya.utility.PermissionFileHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import static com.orion.sinar_surya.globals.JConst.PATH_PDF;

public class ZoomImageFullActivity extends AppCompatActivity {
    private PhotoView photo;
    private ProgressDialog Loading;
    private ImageButton btnDownload, btnClose, btnShare;
    private int ResultGambar;
    private boolean IsAdaGambar;
    private String gambar;
    private String nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_full);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CreateView();
        InitView();
        EventClass();
        LoadData();
    }

    private void InitView() {
        Bundle extra = this.getIntent().getExtras();
        gambar = extra.getString("GAMBAR");
        nama = extra.getString("NAMA");
        this.Loading = new ProgressDialog(ZoomImageFullActivity.this);
    }

    private void CreateView(){
        photo = findViewById(R.id.imgSizePack);
        btnClose = findViewById(R.id.btnClose);
        btnDownload = findViewById(R.id.btnDownload);
        btnShare = findViewById(R.id.btnShare);
    }

    private void EventClass() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionFileHelper.requestPermission(ZoomImageFullActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new Runnable() {
                    @Override
                    public void run() {
                        // Lakukan tindakan setelah izin diberikan di sini
                        downloadGambar();
                    }
                });

            }
        });


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionFileHelper.requestPermission(ZoomImageFullActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new Runnable() {
                    @Override
                    public void run() {
                        // Lakukan tindakan setelah izin diberikan di sini
                        shareGambar();
                    }
                });
            }
        });
    }

    private void downloadGambar(){
        if (IsAdaGambar){
            ResultGambar = 0;
            String Path = CreateGetDir();
            if (!gambar.equals("")){
                Picasso.get().load(gambar.replace("%", "%25")).into(picassoImageTarget(Path, nama+".jpg"));

                int i = 0;
                do {
                    i = 0;
                }while (ResultGambar == 0);
                if (ResultGambar == 1) {
                    Toast.makeText(getApplicationContext(), "Gambar berhasil disimpan di : "+Path+"/"+nama, Toast.LENGTH_SHORT).show();

                    //Koding agar setelah download langsung masuk ke galeri
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{Path+"/"+nama+".jpg"},null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            // now visible in gallery
                        }
                    });
                }
            }else{
                Toast.makeText(getApplicationContext(), "Gambar tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Gambar tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareGambar(){
        if (IsAdaGambar){
            ResultGambar = 0;
            String Path = CreateGetDir();
            if (!gambar.equals("")){
                Picasso.get().load(gambar.replace("%", "%25")).into(picassoImageTarget(Path, nama+".jpg"));

                int i = 0;
                do {
                    i = 0;
                }while (ResultGambar == 0);
                if (ResultGambar == 1) {
                    //Koding agar setelah download langsung masuk ke galeri
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{Path+"/"+nama+".jpg"},null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            kirimgambar(Path+"/"+nama+".jpg");
                        }
                    });
                }
            }else{
                Toast.makeText(getApplicationContext(), "Gambar tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Gambar tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    private void kirimgambar(String filename){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(filename);

        if (fileWithinMyDir.exists()) {
            intentShareFile.setType("application/pdf");
            Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", fileWithinMyDir);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, path);

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Kartu Piutang");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, nama);

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }



    private void LoadData() {
        Loading.setMessage("Loading...");
        Loading.setCancelable(false);
        Loading.show();
        IsAdaGambar = false;

        if ((gambar!= null) && (!gambar.equals(""))){
            Picasso.get().load(gambar.replace("%", "%25")).into(photo, new Callback() {
                @Override
                public void onSuccess() {
                    IsAdaGambar = true;
                    Loading.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    IsAdaGambar = false;
                    Loading.dismiss();
                }
            });
        }else{
            Picasso.get().load(R.drawable.gambar_tidak_tersedia).into(photo);
            IsAdaGambar = false;
            Loading.dismiss();
        }
    }

    //target to save
    private Target picassoImageTarget(final String imageDir, final String imageName) {
        final File directory = new File(imageDir);
        return new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap!= null){
                            final File myImageFile = new File(directory, imageName); // Create image file
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(myImageFile);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                ResultGambar = 1;
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            ResultGambar = 2;
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                ResultGambar = 2;
                Toast.makeText(getApplicationContext(), "Gambar gagal disimpan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }

    private String CreateGetDir() {
        File direct = new File(Environment.getExternalStorageDirectory() +"/" + Environment.DIRECTORY_DOCUMENTS+ "/image_sinar_surya");

        if (!direct.exists()) {
            direct.mkdirs();
        }
        return direct.getPath();
    }


}

