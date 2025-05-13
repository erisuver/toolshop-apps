package com.orion.sinar_surya.models;

import java.util.List;

public class UmurPiutangDetailModel {
    private String noFaktur;
    private String tglJT;
    private double nilai;
    private int umur;

    public UmurPiutangDetailModel(String noFaktur, String tglJT, double nilai, int umur) {
        this.noFaktur = noFaktur;
        this.tglJT = tglJT;
        this.nilai = nilai;
        this.umur = umur;
    }

    public String getNoFaktur() {
        return noFaktur;
    }

    public void setNoFaktur(String noFaktur) {
        this.noFaktur = noFaktur;
    }

    public String getTglJT() {
        return tglJT;
    }

    public void setTglJT(String tglJT) {
        this.tglJT = tglJT;
    }

    public double getNilai() {
        return nilai;
    }

    public void setNilai(double nilai) {
        this.nilai = nilai;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }
}
