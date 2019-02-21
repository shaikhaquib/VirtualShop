package com.virtualshop.virtualshop.Model;

import com.google.gson.annotations.SerializedName;

public class ResultItem{


    public String tradeId           ;
    public String customer_id       ;
    public String updatedAt         ;
    public String updatdAt          ;
    public String createdAt         ;
    public String productName           ;
	public String companyStockId        ;
    public int    sellSp;
    public int    sellingPrice;
    public int availablestock;
    public int    totalQty;
    public int    qtyPerStock;
    public int    availableQty;
    public int    gst;
    public int    totalStock;
    public int    buySp;
    public int    price;
    public int    productId;
    public int    id;
    public String customerStockId;
    public int status;

	public void setCompanyStockId(String companyStockId){
		this.companyStockId = companyStockId;
	}

	public String getCompanyStockId(){
		return companyStockId;
	}

	public void setSellSp(int sellSp){
		this.sellSp = sellSp;
	}

	public int getSellSp(){
		return sellSp;
	}

	public void setSellingPrice(int sellingPrice){
		this.sellingPrice = sellingPrice;
	}

	public int getSellingPrice(){
		return sellingPrice;
	}

	public void setAvailablestock(int availablestock){
		this.availablestock = availablestock;
	}

	public int getAvailablestock(){
		return availablestock;
	}

	public void setTotalQty(int totalQty){
		this.totalQty = totalQty;
	}

	public int getTotalQty(){
		return totalQty;
	}

	public void setQtyPerStock(int qtyPerStock){
		this.qtyPerStock = qtyPerStock;
	}

	public int getQtyPerStock(){
		return qtyPerStock;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setAvailableQty(int availableQty){
		this.availableQty = availableQty;
	}

	public int getAvailableQty(){
		return availableQty;
	}

	public void setGst(int gst){
		this.gst = gst;
	}

	public int getGst(){
		return gst;
	}

	public void setProductName(String productName){
		this.productName = productName;
	}

	public String getProductName(){
		return productName;
	}

	public void setTotalStock(int totalStock){
		this.totalStock = totalStock;
	}

	public int getTotalStock(){
		return totalStock;
	}

	public void setBuySp(int buySp){
		this.buySp = buySp;
	}

	public int getBuySp(){
		return buySp;
	}

	public void setTradeId(String tradeId){
		this.tradeId = tradeId;
	}

	public String getTradeId(){
		return tradeId;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setPrice(int price){
		this.price = price;
	}

	public int getPrice(){
		return price;
	}

	public void setProductId(int productId){
		this.productId = productId;
	}

	public int getProductId(){
		return productId;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setUpdatdAt(String updatdAt){
		this.updatdAt = updatdAt;
	}

	public String getUpdatdAt(){
		return updatdAt;
	}


	public Object getCustomerStockId(){
		return customerStockId;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"ResultItem{" + 
			"company_stock_id = '" + companyStockId + '\'' + 
			",sell_sp = '" + sellSp + '\'' + 
			",selling_price = '" + sellingPrice + '\'' + 
			",available_sp = '" + availablestock + '\'' +
			",total_qty = '" + totalQty + '\'' + 
			",qty_per_stock = '" + qtyPerStock + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",available_qty = '" + availableQty + '\'' + 
			",gst = '" + gst + '\'' + 
			",product_name = '" + productName + '\'' + 
			",total_stock = '" + totalStock + '\'' + 
			",buy_sp = '" + buySp + '\'' + 
			",trade_id = '" + tradeId + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",price = '" + price + '\'' + 
			",product_id = '" + productId + '\'' + 
			",id = '" + id + '\'' + 
			",updatd_at = '" + updatdAt + '\'' + 
			",customer_stock_id = '" + customerStockId + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}