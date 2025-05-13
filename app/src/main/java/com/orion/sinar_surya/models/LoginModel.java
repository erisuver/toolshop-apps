package com.orion.sinar_surya.models;

public class LoginModel {
    private String user_id;
    private String password;
    private int database_id;

    public LoginModel(String user_id, String password, int database_id) {
        this.user_id = user_id;
        this.password = password;
        this.database_id = database_id;
    }

    public LoginModel() {
        this.user_id = "";
        this.password = "";
        this.database_id = 0;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase_id() {
        return database_id;
    }

    public void setDatabase_id(int database_id) {
        this.database_id = database_id;
    }
}