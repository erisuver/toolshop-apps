package com.orion.sinar_surya.models;

public class UrutkanModel {
    private String nama;
    private boolean pilih;

    public UrutkanModel(String nama, boolean pilih) {
        this.nama = nama;
        this.pilih = pilih;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public boolean isPilih() {
        return pilih;
    }

    public void setPilih(boolean pilih) {
        this.pilih = pilih;
    }
}
