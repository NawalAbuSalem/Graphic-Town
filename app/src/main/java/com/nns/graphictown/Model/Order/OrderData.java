
package com.nns.graphictown.Model.Order;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderData implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("app_user_id")
    @Expose
    private Integer appUserId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("total_amount")
    @Expose
    private Double totalAmount;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("fees")
    @Expose
    private Double fees;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("delivery_amount")
    @Expose
    private Double deliveryAmount;
    @SerializedName("delivery_date")
    @Expose
    private Object deliveryDate;
    @SerializedName("cart_id")
    @Expose
    private Integer cartId;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @SerializedName("total_amount_with_fees")
    @Expose
    private Double totalAmountWithFees;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("status_name")
    @Expose
    private String statusName;
    @SerializedName("status_color")
    @Expose
    private String statusColor;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("order_delivery_date")
    @Expose
    private String orderDeliveryDate;
    @SerializedName("vat_number")
    @Expose
    private String vatNumber;
    @SerializedName("items")
    @Expose
    private List<OrderItem> items = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(Integer appUserId) {
        this.appUserId = appUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(Double deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public Object getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Object deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Double getTotalAmountWithFees() {
        return totalAmountWithFees;
    }

    public void setTotalAmountWithFees(Double totalAmountWithFees) {
        this.totalAmountWithFees = totalAmountWithFees;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderDeliveryDate() {
        return orderDeliveryDate;
    }

    public void setOrderDeliveryDate(String orderDeliveryDate) {
        this.orderDeliveryDate = orderDeliveryDate;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

}
