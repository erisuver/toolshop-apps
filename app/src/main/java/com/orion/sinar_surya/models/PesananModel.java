package com.orion.sinar_surya.models;

public class PesananModel {
    private int seq_barang;
    private int seq_satuan;
    private int seq_promo;
    private String nama_barang;
    private String nama_satuan;
    private String gambar;
    private double harga_awal;
    private double harga_akhir;
    private double diskon_pct;
    private int qty;

    public PesananModel(int seq_barang, int seq_satuan, int seq_promo, String nama_barang, String nama_satuan, String gambar, double harga_awal, double harga_akhir, double diskon_pct, int qty) {
        this.seq_barang = seq_barang;
        this.seq_satuan = seq_satuan;
        this.seq_promo = seq_promo;
        this.nama_barang = nama_barang;
        this.nama_satuan = nama_satuan;
        this.gambar = gambar;
        this.harga_awal = harga_awal;
        this.harga_akhir = harga_akhir;
        this.diskon_pct = diskon_pct;
        this.qty = qty;
    }

    public int getSeq_barang() {
        return seq_barang;
    }

    public void setSeq_barang(int seq_barang) {
        this.seq_barang = seq_barang;
    }

    public int getSeq_satuan() {
        return seq_satuan;
    }

    public void setSeq_satuan(int seq_satuan) {
        this.seq_satuan = seq_satuan;
    }

    public int getSeq_promo() {
        return seq_promo;
    }

    public void setSeq_promo(int seq_promo) {
        this.seq_promo = seq_promo;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public String getNama_satuan() {
        return nama_satuan;
    }

    public void setNama_satuan(String nama_satuan) {
        this.nama_satuan = nama_satuan;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
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

    public double getDiskon_pct() {
        return diskon_pct;
    }

    public void setDiskon_pct(double diskon_pct) {
        this.diskon_pct = diskon_pct;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
