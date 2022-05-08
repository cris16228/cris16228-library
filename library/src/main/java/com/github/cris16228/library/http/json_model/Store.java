package com.github.cris16228.library.http.json_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Store {

    @SerializedName("crisstore")
    @Expose
    private CrisStore crisstore;
    @SerializedName("apps")
    @Expose
    private List<App> apps = null;

    public CrisStore getCrisStore() {
        return crisstore;
    }

    public void setCrisStore(CrisStore crisstore) {
        this.crisstore = crisstore;
    }

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }
}