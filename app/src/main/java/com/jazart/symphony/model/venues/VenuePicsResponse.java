
package com.jazart.symphony.model.venues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VenuePicsResponse {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("response")
    @Expose
    private PicResponse response;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public PicResponse getResponse() {
        return response;
    }

    public void setResponse(PicResponse response) {
        this.response = response;
    }

}
