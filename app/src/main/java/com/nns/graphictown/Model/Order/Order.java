
package com.nns.graphictown.Model.Order;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order implements Serializable {

    @SerializedName("data")
    @Expose
    private List<OrderData> data = null;

    public List<OrderData> getData() {
        return data;
    }

    public void setData(List<OrderData> data) {
        this.data = data;
    }

}
