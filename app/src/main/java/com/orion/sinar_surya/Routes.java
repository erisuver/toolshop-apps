package com.orion.sinar_surya;

public class Routes {
//    public static String IP_ADDRESS = "http://192.168.18.5";
//    public static String NAMA_API = "sinar_surya_desktop";
//    public static String IP_ADDRESS = "http://orionbdg.xyz";
    public static String IP_ADDRESS = "http://pasienqu.com";
//    public static String IP_ADDRESS = "http://34.87.145.39";

    public static String NAMA_API = "sinar_surya_api_desktop";//"sinar_surya_api_desktop_errorin";//"sinar_surya_desktop";//
    public static String URL_API_AWAL = IP_ADDRESS + "/"+NAMA_API+"/public/";
    public static String URL_GET_REAL_API = "http://orionbdg.xyz/internal_orion/public/setting_ip/get_real_ip_address?nama_api="+NAMA_API;
    public static String URL_DRIVE_PROFILE = "https://drive.google.com/u/0/uc?id=18cUONBHYxCq4SumXrnA6f2SQJWHJTbdw&export=download";

    // File URLs
    public static String url_folder_file() {
        return JApplication.getInstance().real_url + "uploads/backup_db/sinar_surya_android.zip";
    }

    public static String url_file() {
        return JApplication.getInstance().real_url + "sinar_surya_android.db";
    }
    public static String url_help() {
        return JApplication.getInstance().real_url + "Petunjuk Penggunaan Aplikasi Customer Sinar Surya.pdf";
    }

    public static String url_histori_db() {
        return JApplication.getInstance().real_url + "uploads/backup_db/histori.php";
    }

    // Gambar or FILE URL
    public static String url_path_gambar_promo() {
        return JApplication.getInstance().real_url + "uploads/gambar/Promo";
    }
    public static String url_path_gambar_barang() {
        return JApplication.getInstance().real_url + "uploads/gambar/Barang";
    }
    public static String url_path_file_faktur() {
        return JApplication.getInstance().real_url + "uploads/gambar/fj";
    }

    //Versi
    public static String url_get_versi_app() {
        return JApplication.getInstance().real_url + "versi_data_android.php";
    }
    public static String url_apk() {
        return JApplication.getInstance().real_url + "sinar_surya.apk";
    }

    //LoginActivity
    public static String url_login() {
        return JApplication.getInstance().real_url + "transaksi/login";
    }

//=========================================TRANSAKSI================================================
    //BARANG
    public static String url_get_setting_promo_mst() {
        return JApplication.getInstance().real_url + "barang/get_setting_promo_mst";
    }
    public static String url_get_barang_for_rekap() {
        return JApplication.getInstance().real_url + "barang/get_barang_for_rekap";
    }
    public static String url_get_barang_for_detail() {
        return JApplication.getInstance().real_url + "barang/get_barang_for_detail";
    }


    //LOV
    public static String url_get_jenis_barang() {
        return JApplication.getInstance().real_url + "lov/get_jenis_barang";
    }
    public static String url_get_klasifikasi() {
        return JApplication.getInstance().real_url + "lov/get_klasifikasi";
    }
    public static String url_get_ukuran() {
        return JApplication.getInstance().real_url + "lov/get_ukuran";
    }

    //KERANJANG
    public static String url_save_keranjang() {
        return JApplication.getInstance().real_url + "keranjang/save";
    }
    public static String url_delete_keranjang() {
        return JApplication.getInstance().real_url + "keranjang/delete";
    }
    public static String url_update_field_keranjang() {
        return JApplication.getInstance().real_url + "keranjang/update_field";
    }
    public static String url_get_keranjang_for_rekap() {
        return JApplication.getInstance().real_url + "keranjang/get_keranjang_for_rekap";
    }
    public static String url_get_keranjang_for_pesanan() {
        return JApplication.getInstance().real_url + "keranjang/get_keranjang_for_pesanan";
    }
    public static String url_get_jumlah_keranjang() {
        return JApplication.getInstance().real_url + "keranjang/get_jumlah_keranjang";
    }


    //PESANAN
    public static String url_save_pesanan() {
        return JApplication.getInstance().real_url + "pesanan/save";
    }
    public static String url_get_riwayat_pesanan() {
        return JApplication.getInstance().real_url + "pesanan/get_riwayat_pesanan";
    }
    public static String url_get_get_pesanan() {
        return JApplication.getInstance().real_url + "pesanan/get_pesanan";
    }
    public static String url_batalkan_pesanan() {
        return JApplication.getInstance().real_url + "pesanan/batalkan_pesanan";
    }



    //kartu piutang
    public static String url_load_umur_piutang() {
        return JApplication.getInstance().real_url + "umur_piutang/load_umur_piutang";
    }

    //umur piutang
    public static String url_load_kartu_piutang() {
        return JApplication.getInstance().real_url + "kartu_piutang/load_kartu_piutang";
    }

    //AKUN
    public static String url_get_akun() {
        return JApplication.getInstance().real_url + "akun/get";
    }
    public static String url_ubah_password() {
        return JApplication.getInstance().real_url + "akun/ubah_password";
    }
    public static String url_get_faktur_pajak() {
        return JApplication.getInstance().real_url + "akun/get_faktur_pajak";
    }

}
