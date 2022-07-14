
package com.nns.graphictown.Model.Order;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderProduct implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("sub_category_id")
    @Expose
    private Integer subCategoryId;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("material_type")
    @Expose
    private String materialType;
    @SerializedName("images_count")
    @Expose
    private String imagesCount;
    @SerializedName("images_urls")
    @Expose
    private List<String> imagesUrls = null;
    @SerializedName("agent_id")
    @Expose
    private Integer agentId;
    @SerializedName("agent_name")
    @Expose
    private String agentName;
    @SerializedName("user")
    @Expose
    private OrderUser user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Integer subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getImagesCount() {
        return imagesCount;
    }

    public void setImagesCount(String imagesCount) {
        this.imagesCount = imagesCount;
    }

    public List<String> getImagesUrls() {
        return imagesUrls;
    }

    public void setImagesUrls(List<String> imagesUrls) {
        this.imagesUrls = imagesUrls;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public OrderUser getUser() {
        return user;
    }

    public void setUser(OrderUser user) {
        this.user = user;
    }

}
