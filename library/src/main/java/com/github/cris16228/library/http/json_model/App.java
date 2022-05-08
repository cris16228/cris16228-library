package com.github.cris16228.library.http.json_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class App {

    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("versionCode")
    @Expose
    private String versionCode;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("screenshots")
    @Expose
    private List<String> screenshots = null;
    @SerializedName("links")
    @Expose
    private List<String> links = null;
    @SerializedName("download")
    @Expose
    private Download download;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public Download getDownload() {
        return download;
    }

    public void setDownload(Download download) {
        this.download = download;
    }


}
