package com.github.cris16228.library.http.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("date")
    @Expose
    public Long date;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("pic")
    @Expose
    private String pic;
    @SerializedName("id")
    @Expose
    private String id;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
