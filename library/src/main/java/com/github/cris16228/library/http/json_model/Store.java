package com.github.cris16228.library.http.json_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Store implements Serializable {

    @SerializedName("apps")
    @Expose
    private List<App> apps = null;

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }

}