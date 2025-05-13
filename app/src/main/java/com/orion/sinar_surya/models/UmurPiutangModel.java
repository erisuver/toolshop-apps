package com.orion.sinar_surya.models;

import java.util.List;

public class UmurPiutangModel {
    private String tipe;
    private double total;
    private List<UmurPiutangDetailModel> detail;
    private boolean is_total;
    private boolean isExpand;

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<UmurPiutangDetailModel> getDetail() {
        return detail;
    }

    public void setDetail(List<UmurPiutangDetailModel> detail) {
        this.detail = detail;
    }

    public UmurPiutangModel(String tipe, double total, List<UmurPiutangDetailModel> detail) {
        this.tipe = tipe;
        this.total = total;
        this.detail = detail;
        this.is_total = is_total;
        this.isExpand = false;
    }

    public boolean isIs_total() {
        return is_total;
    }

    public void setIs_total(boolean is_total) {
        this.is_total = is_total;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
