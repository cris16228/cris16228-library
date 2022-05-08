package com.github.cris16228.library.http.json_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Download {

    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("updater")
    @Expose
    private String updater;
    @SerializedName("changes")
    @Expose
    private List<String> changes = null;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public List<String> getChanges() {
        return changes;
    }

    public void setChanges(List<String> changes) {
        this.changes = changes;
    }
}