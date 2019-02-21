package com.virtualshop.virtualshop.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockModel {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("result")
    @Expose
    private List<Result> result = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }



    public class Result {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("customer_id")
        @Expose
        private Integer customerId;
        @SerializedName("customer_stock_id")
        @Expose
        private String customerStockId;
        @SerializedName("company_stock_id")
        @Expose
        private Integer companyStockId;
        @SerializedName("trade_id")
        @Expose
        private String tradeId;
        @SerializedName("buy_sp")
        @Expose
        private Integer buySp;
        @SerializedName("sell_sp")
        @Expose
        private Integer sellSp;
        @SerializedName("purchased_stock")
        @Expose
        private Integer purchasedStock;
        @SerializedName("available_stock")
        @Expose
        private Integer availableStock;
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Integer customerId) {
            this.customerId = customerId;
        }

        public String getCustomerStockId() {
            return customerStockId;
        }

        public void setCustomerStockId(String customerStockId) {
            this.customerStockId = customerStockId;
        }

        public Integer getCompanyStockId() {
            return companyStockId;
        }

        public void setCompanyStockId(Integer companyStockId) {
            this.companyStockId = companyStockId;
        }

        public String getTradeId() {
            return tradeId;
        }

        public void setTradeId(String tradeId) {
            this.tradeId = tradeId;
        }

        public Integer getBuySp() {
            return buySp;
        }

        public void setBuySp(Integer buySp) {
            this.buySp = buySp;
        }

        public Integer getSellSp() {
            return sellSp;
        }

        public void setSellSp(Integer sellSp) {
            this.sellSp = sellSp;
        }

        public Integer getPurchasedStock() {
            return purchasedStock;
        }

        public void setPurchasedStock(Integer purchasedStock) {
            this.purchasedStock = purchasedStock;
        }

        public Integer getAvailableStock() {
            return availableStock;
        }

        public void setAvailableStock(Integer availableStock) {
            this.availableStock = availableStock;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }}