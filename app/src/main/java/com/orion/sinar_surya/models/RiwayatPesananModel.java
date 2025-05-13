package com.orion.sinar_surya.models;

public class RiwayatPesananModel {
    private int seq;
    private String tgl_order;
    private String no_faktur;
    private String no_order;
    private int qty;
    private double total;
    private String is_proses;
    private String is_belum_proses;
    private String is_selesai;
    private String is_batal;
    private String is_lunas;
    private String is_dari_apps;
    private String is_tolak;

    public RiwayatPesananModel(int seq, String tgl_order, String no_faktur, String no_order, int qty, double total, String is_proses, String is_belum_proses, String is_selesai, String is_batal, String is_lunas, String is_dari_apps, String is_tolak) {
        this.seq = seq;
        this.tgl_order = tgl_order;
        this.no_faktur = no_faktur;
        this.no_order = no_order;
        this.qty = qty;
        this.total = total;
        this.is_proses = is_proses;
        this.is_belum_proses = is_belum_proses;
        this.is_selesai = is_selesai;
        this.is_batal = is_batal;
        this.is_lunas = is_lunas;
        this.is_dari_apps = is_dari_apps;
        this.is_tolak = is_tolak;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getTgl_order() {
        return tgl_order;
    }

    public void setTgl_order(String tgl_order) {
        this.tgl_order = tgl_order;
    }

    public String getNo_faktur() {
        return no_faktur;
    }

    public void setNo_faktur(String no_faktur) {
        this.no_faktur = no_faktur;
    }

    public String getNo_order() {
        return no_order;
    }

    public void setNo_order(String no_order) {
        this.no_order = no_order;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getIs_proses() {
        return is_proses;
    }

    public void setIs_proses(String is_proses) {
        this.is_proses = is_proses;
    }

    public String getIs_belum_proses() {
        return is_belum_proses;
    }

    public void setIs_belum_proses(String is_belum_proses) {
        this.is_belum_proses = is_belum_proses;
    }

    public String getIs_selesai() {
        return is_selesai;
    }

    public void setIs_selesai(String is_selesai) {
        this.is_selesai = is_selesai;
    }

    public String getIs_batal() {
        return is_batal;
    }

    public void setIs_batal(String is_batal) {
        this.is_batal = is_batal;
    }

    public String getIs_lunas() {
        return is_lunas;
    }

    public void setIs_lunas(String is_lunas) {
        this.is_lunas = is_lunas;
    }

    public String getIs_dari_apps() {
        return is_dari_apps;
    }

    public void setIs_dari_apps(String is_dari_apps) {
        this.is_dari_apps = is_dari_apps;
    }

    public String getIs_tolak() {
        return is_tolak;
    }

    public void setIs_tolak(String is_tolak) {
        this.is_tolak = is_tolak;
    }
}
