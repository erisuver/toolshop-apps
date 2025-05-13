package com.orion.sinar_surya.globals;

import android.os.Environment;

import com.orion.sinar_surya.JApplication;

public class JConst {

    //MISSAGE
    public static final String MSG_API_KEY_UNAUTORIZED = "Sesi login telah berakhir, silahkan login ulang";
    public static final String MSG_NOT_CONNECTION = "Tidak tersambung ke server, pastikan terkoneksi ke internet";
    public static final String KONDISI_HARUS_UPDATE = "harus update";
    public static final String KONDISI_TERUPDATE = "terupdate";
    public static final String MSG_HARUS_UPDATE     = "Versi baru telah tersedia, update aplikasi sekarang?";
    public static String MSG_API_GAGAL_LOAD    = "Terjadi kesalahan koneksi ke server";
    public static String MSG_GAGAL_KONEKSI_KE_SERVER = "Gagal koneksi ke server";
    public static String STATUS_API_SUCCESS     = "success";
    public static String STATUS_API_FAILED      = "failed";
    public static String TRUE_STRING = "T";
    public static String FALSE_STRING = "F";

    public static String mediaLocationPath = Environment.getExternalStorageDirectory() + "/Android/Data/"+JApplication.getInstance().getPackageName()+ "/Media/";
    public static final String tag_json_obj = "json_obj_req";

    public static final String CG_DB_GLOBAL = "1";
    public static final String CG_DB_BANDUNG = "2";
    public static final String CG_DB_CIREBON = "3";
    public static final String CG_DB_KARAWANG = "4";
    public static final String CG_DB_GLOBAL_TEXT = "inv_sinar_surya_global";
    public static final String CG_DB_BANDUNG_TEXT = "inv_sinar_surya";
    public static final String CG_DB_CIREBON_TEXT = "inv_sinar_surya_cirebon";
    public static final String CG_DB_KARAWANG_TEXT = "inv_sinar_surya_karawang";

    //status
    public static final String TEXT_STATUS_BELUM_DIPROSES = "Belum Diproses";
    public static final String TEXT_STATUS_DIPROSES = "Diproses";
    public static final String TEXT_STATUS_SELESAI = "Selesai";
    public static final String TEXT_STATUS_LUNAS = "Lunas";
    public static final String TEXT_STATUS_DIBATALKAN = "Dibatalkan";
    public static final String TEXT_STATUS_DARI_APPS = "Dari Apps Customer";
    public static final String TEXT_STATUS_DITOLAK = "Ditolak";

    public static final String STATUS_BELUM_DIPROSES = "BP";
    public static final String STATUS_DIPROSES = "P";
    public static final String STATUS_SELESAI = "S";
    public static final String STATUS_LUNAS = "L";
    public static final String STATUS_DIBATALKAN = "B";
    public static final String STATUS_DARI_APPS = "A";
    public static final String STATUS_DITOLAK = "T";


    public static final String PATH_PDF = "pdf_sinar_surya";
}


