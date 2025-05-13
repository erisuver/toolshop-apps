package com.orion.sinar_surya.models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductListDetailModel implements Serializable {
    private int seq;
    private String nama;
    private String gambar;
    private double harga_awal;
    private double harga_akhir;
    private double diskon_pct;
    private String variasi;
    private String deskripsi;
    private int seq_satuan;
    private String nama_satuan;

    public ProductListDetailModel(int seq, String nama, String gambar, double harga_awal, double harga_akhir, double diskon_pct, String variasi, String deskripsi, int seq_satuan, String nama_satuan) {
        this.seq = seq;
        this.nama = nama;
        this.gambar = gambar;
        this.harga_awal = harga_awal;
        this.harga_akhir = harga_akhir;
        this.diskon_pct = diskon_pct;
        this.variasi = variasi;
        this.deskripsi = deskripsi;
        this.seq_satuan = seq_satuan;
        this.nama_satuan = nama_satuan;
    }

    public ProductListDetailModel() {
        this.seq = 0;
        this.nama = "";
        this.gambar = "";
        this.harga_awal = 0;
        this.harga_akhir = 0;
        this.diskon_pct = 0;
        this.variasi = "";
        this.deskripsi = "";
        this.seq_satuan = 0;
        this.nama_satuan = "";
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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

    public String getVariasi() {
        return variasi;
    }

    public void setVariasi(String variasi) {
        this.variasi = variasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public int getSeqSatuan() {
        return seq_satuan;
    }

    public void setSeqSatuan(int seq_satuan) {
        this.seq_satuan = seq_satuan;
    }

    public String getNamaSatuan() {
        return nama_satuan;
    }

    public void setNamaSatuan(String nama_satuan) {
        this.nama_satuan = nama_satuan;
    }

}
