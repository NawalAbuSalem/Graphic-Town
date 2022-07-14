
package com.nns.graphictown.Model.Cart;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("app_user_id")
    @Expose
    private Integer appUserId;
    @SerializedName("delivery_location")
    @Expose
    private String deliveryLocation;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("total_amount")
    @Expose
    private double totalAmount;

    @SerializedName("total_amount_with_fees")
    @Expose
    private double totalAmountWithFee;

    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(Integer appUserId) {
        this.appUserId = appUserId;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public double getTotalAmountWithFee() {
        return totalAmountWithFee;
    }

    public void setTotalAmountWithFee(double totalAmountWithFee) {
        this.totalAmountWithFee = totalAmountWithFee;
    }
}
