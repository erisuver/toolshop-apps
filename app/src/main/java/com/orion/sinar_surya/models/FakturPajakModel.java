package com.orion.sinar_surya.models;

public class FakturPajakModel {
    private String attachment;
    private String tgl_faktur;
    private String no_faktur;
    private String no_order;
    private double total;

    public FakturPajakModel(String attachment, String tgl_faktur, String no_faktur, String no_order, double total) {
        this.attachment = attachment;
        this.tgl_faktur = tgl_faktur;
        this.no_faktur = no_faktur;
        this.no_order = no_order;
        this.total = total;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getTgl_faktur() {
        return tgl_faktur;
    }

    public void setTgl_faktur(String tgl_faktur) {
        this.tgl_faktur = tgl_faktur;
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
