package com.github.cris16228.library.http.updater;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JsonModel implements Serializable {

    @SerializedName("version")
    @Expose
    public String version;

    @SerializedName("versionCode")
    @Expose
    public Integer versionCode;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }
}
