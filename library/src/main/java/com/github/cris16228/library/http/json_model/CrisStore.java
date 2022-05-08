package com.github.cris16228.library.http.json_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CrisStore {

    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("versionCode")
    @Expose
    private String versionCode;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

}
