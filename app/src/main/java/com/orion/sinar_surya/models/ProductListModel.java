package com.orion.sinar_surya.models;

public class ProductListModel {
    private String nama;
    private String gambar;
    private double harga_awal;
    private double harga_akhir;
    private double diskon_pct;

    public ProductListModel(String nama, String gambar, double harga_awal, double harga_akhir, double diskon_pct) {
        this.nama = nama;
        this.gambar = gambar;
        this.harga_awal = harga_awal;
        this.diskon_pct = diskon_pct;
        this.harga_akhir = harga_akhir;
    }

    public ProductListModel() {
        this.nama = "";
        this.gambar = "";
        this.harga_awal = 0.0;
        this.harga_akhir = 0.0;
        this.diskon_pct = 0.0;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public double getDiskon_pct() {
        return diskon_pct;
    }

    public void setDiskon_pct(double diskon_pct) {
        this.diskon_pct = diskon_pct;
    }

    public double getHarga_awal() {
        return harga_awal;
    }

    public void setHarga_awal(double harga_awal) {
        this.harga_awal = harga_awal;
    }

    public double getHarga_akhir() {
        return harga_akhir;
    }

    public void setHarga_akhir(double harga_akhir) {
        this.harga_akhir = harga_akhir;
    }
}
