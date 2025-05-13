package com.orion.sinar_surya.models;

public class SatuanModel {
    private int seq;
    private int seq_barang;
    private String satuan;
    private double harga;

    public SatuanModel(int seq, int seq_barang, String satuan, double harga) {
        this.seq = seq;
        this.seq_barang = seq_barang;
        this.satuan = satuan;
        this.harga = harga;
    }

    public SatuanModel() {
        this.seq = 0;
        this.seq_barang = 0;
        this.satuan = "";
        this.harga = 0;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeq_barang() {
        return seq_barang;
    }

    public void setSeq_barang(int seq_barang) {
        this.seq_barang = seq_barang;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }
}
