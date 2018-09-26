package org.sairaa.news360degree.model;

import com.google.gson.annotations.SerializedName;

class SourceX {
    @SerializedName("id")
    private String id = null;
    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
