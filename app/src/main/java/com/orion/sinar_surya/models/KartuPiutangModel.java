package com.orion.sinar_surya.models;

public class KartuPiutangModel {
    private String tanggal;
    private String nomor;
    private double total;
    private String tj;

    private boolean isHeader;
    private String saldoAwal;
    private boolean isFooter;
    private String totalFaktur;
    private String totalPeluanasan;
    private String totalTitipan;
    private String totalAkhir;

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getSaldoAwal() {
        return saldoAwal;
    }

    public void setSaldoAwal(String saldoAwal) {
        this.saldoAwal = saldoAwal;
    }

    public boolean isFooter() {
        return isFooter;
    }

    public void setFooter(boolean footer) {
        isFooter = footer;
    }

    public String getTotalFaktur() {
        return totalFaktur;
    }

    public void setTotalFaktur(String totalFaktur) {
        this.totalFaktur = totalFaktur;
    }

    public String getTotalPeluanasan() {
        return totalPeluanasan;
    }

    public void setTotalPeluanasan(String totalPeluanasan) {
        this.totalPeluanasan = totalPeluanasan;
    }

    public String getTotalTitipan() {
        return totalTitipan;
    }

    public void setTotalTitipan(String totalTitipan) {
        this.totalTitipan = totalTitipan;
    }

    public String getTotalAkhir() {
        return totalAkhir;
    }

    public void setTotalAkhir(String totalAkhir) {
        this.totalAkhir = totalAkhir;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getTj() {
        return tj;
    }

    public void setTj(String tj) {
        this.tj = tj;
    }

    public KartuPiutangModel(String tanggal, String nomor, double total, String tj) {
        this.tanggal = tanggal;
        this.nomor = nomor;
        this.total = total;
        this.tj = tj;
        this.isHeader = false;
        this.isFooter = false;
    }
}
